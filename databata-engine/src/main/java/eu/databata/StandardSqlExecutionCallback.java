/**
 * Copyright 2014 Nortal AS Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package eu.databata;

import org.hsqldb.cmdline.SqlFile;

import eu.databata.engine.util.DBHistoryLogger;
import eu.databata.engine.util.PropagationUtils;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.SqlExecutionCallback;

/**
 * @author Maksim Boiko <mailto:max.boiko@gmail.com>
 */
public class StandardSqlExecutionCallback implements SqlExecutionCallback {
  private static final Logger LOG = Logger.getLogger(StandardSqlExecutionCallback.class);
  private final DBHistoryLogger historyLogger;
  private SqlFile sqlFile;

  public StandardSqlExecutionCallback(DBHistoryLogger historyLogger) {
    this.historyLogger = historyLogger;
  }

  public void setSqlFile(SqlFile sqlFile) {
    this.sqlFile = sqlFile;
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
    PropagationUtils.handleDataAccessException(exception,
                                               sql,
                                               historyLogger.getStandardExceptionHandler(),
                                               sqlFile,
                                               historyLogger.getJdbcTemplate().getDataSource().getConnection());
  }
}
