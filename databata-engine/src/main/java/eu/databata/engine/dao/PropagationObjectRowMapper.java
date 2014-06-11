package eu.databata.engine.dao;

import eu.databata.engine.model.PropagationObject;
import eu.databata.engine.model.PropagationObject.ObjectType;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Maksim Boiko
 */
public class PropagationObjectRowMapper implements RowMapper<PropagationObject> {

  @Override
  public PropagationObject mapRow(ResultSet rs, int rowNum) throws SQLException {
    PropagationObject propagationObject = new PropagationObject();
    propagationObject.setModuleName(rs.getString("module_name"));
    propagationObject.setObjectName(rs.getString("object_name"));
    propagationObject.setObjectType(ObjectType.valueOf(rs.getString("object_type")));
    propagationObject.setMd5Hash(rs.getString("md5_hash"));
    propagationObject.setVersion(rs.getString("version"));
    return propagationObject;
  }

}
