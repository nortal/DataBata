package eu.databata.engine.exeptions;

import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class SybaseSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(SybaseSQLExceptionHandler.class);

  public boolean isHandled(SQLException e, String sql) {
    switch (e.getErrorCode()) {
    case -141:
      LOG.warn("Table not found. Ignoring error.");
      return true;
    case -110:
      LOG.warn("Item already exists. Ignoring error.");
      return true;
    default:
      return false;
    }
  }
}
