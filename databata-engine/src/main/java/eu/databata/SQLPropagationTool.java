package eu.databata;

import eu.databata.engine.util.DBHistoryLogger;
import eu.databata.engine.util.PropagationUtils;

import eu.databata.engine.dao.PropagationDAO;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.hsqldb.cmdline.StandardTransformer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Wrapper for SqlTool class, which handles some common exceptions.
 * 
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class SQLPropagationTool {
  private static final Logger LOG = Logger.getLogger(SQLPropagationTool.class);

  private Map<String, String> varMap = new HashMap<String, String>();
  private boolean simulationMode;
  private JdbcTemplate jdbcTemplate;
  private DBHistoryLogger historyLogger;
  private String databaseCode;
  private PropagationDAO propagationDAO;
  private String currentDbChange;
  private StandardTransformer transformer;
  private String moduleName;

  public boolean executeFile(String id, File propagatedFile) {
    if (currentDbChange != null) {
      historyLogger.setCurrentDbChange(currentDbChange);
    }
    Connection connection = null;
    try {
      if (!simulationMode) {
        connection = jdbcTemplate.getDataSource().getConnection();
      }
      SqlFile sqlFile = new SqlFile(propagatedFile, new StandardSqlExecutionCallback(historyLogger), transformer);
      sqlFile.setSimulationMode(simulationMode);
      sqlFile.setConnection(connection);
      sqlFile.addUserVars(varMap);
      sqlFile.execute();
      LOG.debug("File executed using SqlTool");
      if (id != null && !simulationMode) {
        propagationDAO.updateHistoryTable(moduleName, id);
      }
      return sqlFile.isReloadFired();
    } catch (IOException e) {
      LOG.warn("Wrong file specified " + propagatedFile.getName(), e);
      throw new RuntimeException(e);
    } catch (SqlToolError e) {
      LOG.warn("Error when executing sql tool with file " + propagatedFile.getName(), e);
      throw new RuntimeException(e);
    } catch (SQLException e) {
      LOG.warn("Error during SQL execution ", e);
      throw new RuntimeException(e);
    } finally {
      try {
        if (!simulationMode) {
          connection.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public int executeSqlUpdate(String sql) {
    if (simulationMode) {
      return -1;
    }
    return jdbcTemplate.update(sql);
  }

  public int executeSqlUpdate(String sql, Object[] params) {
    if (simulationMode) {
      return -1;
    }
    return jdbcTemplate.update(sql, params);
  }

  protected void executeSql(String _sql) {
    String sql = _sql;
    if (StringUtils.isBlank(sql)) {
      LOG.warn("Empty command found. Skipping.");
      return;
    }
    if (sql.startsWith("exec")) {
      if (LOG.isDebugEnabled())
        LOG.debug("Replacing exec command with call.");
      sql = "{call " + sql.substring(4) + "}";
    }
    if (LOG.isInfoEnabled())
      LOG.info("SQL command to be executed: " + sql);
    int rowCount = 0;
    try {
      long start = System.currentTimeMillis();
      rowCount = executeSqlUpdate(sql);
      long finish = System.currentTimeMillis();
      historyLogger.log(sql, rowCount, 0, null, (finish - start) / 1000.00);
    } catch (DataAccessException e) {
      PropagationUtils.handleDataAccessException(e, sql, historyLogger.getStandardExceptionHandler());
    }
  }

  public void dropObject(String objectName) {
    String sql = "DROP " + objectName;
    try {
      long start = System.currentTimeMillis();
      jdbcTemplate.update(sql);
      long finish = System.currentTimeMillis();
      historyLogger.log(sql, 0, 0, null, (finish - start) / 1000.00);
    } catch (DataAccessException e) {
      PropagationUtils.handleDataAccessException(e, sql, historyLogger.getStandardExceptionHandler());
    }
  }

  public void setDatabaseCode(String databaseCode) {
    this.databaseCode = databaseCode;
    varMap.put("DBMS", databaseCode);
  }

  public void setEnvironmentCode(String environmentCode) {
    varMap.put("ENVIRONMENT", environmentCode);
  }

  public void setSimulationMode(boolean simulationMode) {
    this.simulationMode = simulationMode;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public void setHistoryLogger(DBHistoryLogger historyLogger) {
    this.historyLogger = historyLogger;
  }

  public String getDatabaseCode() {
    return databaseCode;
  }

  public void setPropagationDAO(PropagationDAO propagationDAO) {
    this.propagationDAO = propagationDAO;
  }

  public void setCurrentDbChange(String currentDbChange) {
    this.currentDbChange = currentDbChange;
  }

  public void setTransformer(StandardTransformer transformer) {
    this.transformer = transformer;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }
}
