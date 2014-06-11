package eu.databata.engine.util;


import eu.databata.engine.model.HistoryLogEntry;

import eu.databata.engine.exeptions.SQLExceptionHandler;

import eu.databata.engine.dao.PropagationDAO;


import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Provides propagation logging to database. 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public class DBHistoryLogger {
  private Queue<HistoryLogEntry> queue = new LinkedList<HistoryLogEntry>();
  private SQLExceptionHandler exceptionHandler;
  private String currentDbChange;
  private String moduleName;

  private boolean isCaching = false;
  private boolean simulationMode = false;
  private TransactionTemplate transactionTemplate;
  private PropagationDAO propagationDAO;

  public DBHistoryLogger(TransactionTemplate transactionTemplate, PropagationDAO propagationDAO, SQLExceptionHandler exceptionHandler, String moduleName, boolean simulationMode) {
    this.propagationDAO = propagationDAO;
    this.transactionTemplate = transactionTemplate;
    this.moduleName = moduleName;
    this.simulationMode = simulationMode;
    this.exceptionHandler = new WrapperSQLExceptionHandler(exceptionHandler);
    if(!simulationMode) {
      checkTable();
    }
  }
  
  public void log(String sqlText, int sqlRows, int sqlErrorCode, String sqlErrorText, double executionTime) {
    HistoryLogEntry entry = new HistoryLogEntry(sqlText, sqlRows, sqlErrorCode, sqlErrorText, new Date(), currentDbChange, executionTime);
    if (isCaching) {
      queue.add(entry);
    } else {
      outputInTransaction(entry);
    }
  }

  public void startCaching() {
    isCaching = true;
  }

  public void logCached(final String moduleName) {
    if(simulationMode) {
      return;
    }
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        HistoryLogEntry e;
        while ((e = queue.poll()) != null) {
          propagationDAO.insertHistoryLog(moduleName, e);
        }        
      }
    });
    isCaching = false;
  }

  protected void outputInTransaction(final HistoryLogEntry entry) {
    if(simulationMode) {
      return;
    }
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        propagationDAO.insertHistoryLog(moduleName, entry);
      }
    });
  }
  
  protected void checkTable() {
    try {
      propagationDAO.checkHistoryTable();
    } catch (BadSqlGrammarException e) {
      propagationDAO.createHistoryLogTable();
    }
  }

  public SQLExceptionHandler getStandardExceptionHandler() {
    return exceptionHandler;
  }
  
  public void setCurrentDbChange(String currentDbChange){
    this.currentDbChange = currentDbChange;
  }

  private class WrapperSQLExceptionHandler implements SQLExceptionHandler {
    private SQLExceptionHandler handler;

    public WrapperSQLExceptionHandler(SQLExceptionHandler handler) {
      this.handler = handler;
    }

    public boolean isHandled(SQLException e, String sql) {
      boolean ignored = handler.isHandled(e, sql);
      String logMessage = (ignored ? "IGNORED:" : "FATAL:") + e.getMessage();
      log(sql, 0, e.getErrorCode(), logMessage, -0.01);
      return ignored;
    }
  }
}
