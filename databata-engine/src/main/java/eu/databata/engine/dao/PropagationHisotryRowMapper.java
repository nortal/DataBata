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
