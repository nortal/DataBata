package eu.databata.engine.exeptions;

import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * @author Igor Bossenko <mailto:igor@webmedia.ee>
 */
public class MicrosoftSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(MicrosoftSQLExceptionHandler.class);

  public boolean isHandled(SQLException e, String sql) {
    String lowerCaseSql = sql.toLowerCase();
    switch (e.getErrorCode()) {
    case 3701:
      if (lowerCaseSql.contains("drop")) {
        // droping not existing object
        LOG.warn("Cannot %S_MSG the %S_MSG '%.*ls', because it does not exist or you do not have permission. Ignoring error.");
        return true;
      }
      return false;
    case 2627:
      if (lowerCaseSql.contains("values")) {
  	    // insert existing row 
        LOG.warn("Cannot insert duplicate key in object. Ignoring error.");
        return true;
      }
      return false;
    case 2714:
	  // create existing object
      LOG.warn("There is already an object named. Ignoring error.");
      return true;
    default:
      return false;
    }
  }
}
