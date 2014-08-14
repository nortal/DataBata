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
package eu.databata.engine.dao;

import eu.databata.engine.model.PropagationSqlLog;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PropagationSqlLogRowMapper implements RowMapper<PropagationSqlLog> {

  @Override
  public PropagationSqlLog mapRow(ResultSet rs, int rowNum) throws SQLException {
    PropagationSqlLog history = new PropagationSqlLog();
    history.setModuleName(rs.getString("module_name"));
    history.setDbChangeCode(rs.getString("db_change_code"));
    history.setSqlText(rs.getString("sql_text"));
    history.setRowsUpdated(rs.getLong("rows_updated"));
    history.setErrorCode(rs.getInt("error_code"));
    history.setErrorText(rs.getString("error_text"));
    history.setUpdateTime(rs.getTimestamp("update_time"));
    history.setExecutionTime(rs.getBigDecimal("execution_time"));
    return history;
  }

}
