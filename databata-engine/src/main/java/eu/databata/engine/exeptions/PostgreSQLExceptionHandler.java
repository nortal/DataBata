package eu.databata.engine.exeptions;

import java.sql.Connection;

import org.hsqldb.cmdline.SqlFile;

import java.sql.SQLException;

import org.apache.log4j.Logger;

public class PostgreSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(PostgreSQLExceptionHandler.class);

  @Override
  public boolean isHandled(SQLException e, String sql, SqlFile sqlFile, Connection newConnection) {
    String lowerCaseSql = sql.toLowerCase();

    LOG.info("ErrorCode = [" + e.getErrorCode() + "]; SQLState = [" + e.getSQLState() + "]");

    try {
      IGNORED_SQL_STATE state = IGNORED_SQL_STATE.valueOf("_" + e.getSQLState());
      boolean ignored = state.isIgnored(lowerCaseSql);
      if (sqlFile != null && ignored) {
        // In case an error was handled e.g. script execution should continue we must reset connection
        sqlFile.getConnection().rollback();
        // XXX: do we need to close old connection here?
        sqlFile.setConnection(newConnection);
      }
      return ignored;
    } catch (IllegalArgumentException iae) {
      return false;
    } catch (SQLException sqlException) {
      LOG.error(sqlException);
      return false;
    }
  }

  private static enum IGNORED_SQL_STATE {

    _23505 { // unique_violation
      public boolean isIgnored(String lowerCaseSql) {
        if (lowerCaseSql.contains("values")) { // Adding an existing record where entry already exists
          LOG.warn("Unique constraint violated. Ignoring error.");
          return true;
        }
        return false;
      }
    },
    _25P02 { // in_failed_sql_transaction
      public boolean isIgnored(String lowerCaseSql) {
        if (lowerCaseSql.contains("values")) { // Adding an existing record where entry allready exists
          LOG.warn("Unique constraint violated. Ignoring error.");
          return true;
        }
        return false;
      }
    },
    _42P07 { // duplicate_table
      public boolean isIgnored(String lowerCaseSql) {
        LOG.warn("Creating existing table. Ignoring error.");
        return true;
      }
    },
    _42P01 { // Undefined table - OK when dropping it
      public boolean isIgnored(String lowerCaseSql) {
        if (lowerCaseSql.contains("drop")) {
          // An entry already exists
          LOG.warn("Dropping non-existant table. Ignoring error.");
          return true;
        }
        return false;
      }
    };

    public boolean isIgnored(String lowerCaseSql) {
      return true;
    }
  }

}
