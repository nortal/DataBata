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

import eu.databata.engine.model.PropagationHistory;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PropagationHisotryRowMapper implements RowMapper<PropagationHistory> {

  @Override
  public PropagationHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
    PropagationHistory history = new PropagationHistory();
    history.setModuleName(rs.getString("module_name"));
    history.setCode(rs.getString("code"));
    history.setChangeTime(rs.getTimestamp("change_time"));
    return history;
  }

}
