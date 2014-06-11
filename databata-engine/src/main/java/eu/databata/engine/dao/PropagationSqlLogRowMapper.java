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
