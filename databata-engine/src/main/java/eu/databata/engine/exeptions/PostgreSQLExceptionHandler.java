/**
 *   Copyright 2014 Nortal AS
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package eu.databata.engine.exeptions;

import java.sql.Connection;

import org.hsqldb.cmdline.SqlFile;

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * Exceptions handler according to documentation http://www.postgresql.org/docs/9.3/static/errcodes-appendix.html
 * 
 */
public class PostgreSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(PostgreSQLExceptionHandler.class);

  @Override
  public boolean isHandled(SQLException e, String sql, SqlFile sqlFile, Connection newConnection) {
    String lowerCaseSql = sql.toLowerCase();

    LOG.info("ErrorCode = [" + e.getErrorCode() + "]; SQLState = [" + e.getSQLState() + "]");

    try {
      //IGNORED_SQL_STATE state = IGNORED_SQL_STATE.valueOf("_" + e.getSQLState());
      // O1 class is ignored because it is of warning class.
      
      boolean ignored = e.getSQLState().startsWith("01"); //|| state.isIgnored(lowerCaseSql);
      if (sqlFile != null && ignored) {
        // In case an error was handled e.g. script execution should continue we must reset connection
        sqlFile.getConnection().rollback();
        // XXX: do we need to close old connection here?
        sqlFile.setConnection(newConnection);
      }
      return ignored;
    } catch (IllegalArgumentException iae) {
      return e.getSQLState().startsWith("01");
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
