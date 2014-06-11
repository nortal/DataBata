package eu.databata;

import eu.databata.engine.util.DBHistoryLogger;
import eu.databata.engine.util.PropagationUtils;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.SqlExecutionCallback;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class StandardSqlExecutionCallback implements SqlExecutionCallback {
  private static final Logger LOG = Logger.getLogger(StandardSqlExecutionCallback.class);
  private final DBHistoryLogger historyLogger;

  public StandardSqlExecutionCallback(DBHistoryLogger historyLogger) {
    this.historyLogger = historyLogger;
  }

  @Override
  public void handleExecuteSuccess(String sql, int updateCount, double executionTime) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("\n===============\n" + sql + "\n--SUCCESS. Changed " + updateCount + " rows.\n===============");
    }
    historyLogger.log(sql, updateCount, 0, null, executionTime / 1000.00);
  }

  @Override
  public void handleException(SQLException exception, String sql) throws SQLException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("\n===============\n" + sql + "\n--ERROR.\n===============");
    }
    PropagationUtils.handleDataAccessException(exception, sql, historyLogger.getStandardExceptionHandler());
  }
}
