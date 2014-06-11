package eu.databata.engine.exeptions;

import java.sql.SQLException;
import java.util.Scanner;
import org.apache.log4j.Logger;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class OracleSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(OracleSQLExceptionHandler.class);
  
  public boolean isHandled(SQLException e, String sql) {
    String lowerCaseSql = sql.toLowerCase();
    Scanner words = new Scanner(lowerCaseSql);
    String word = null;
    switch (e.getErrorCode()) {
    case 955:
      LOG.warn("Object with the same name exists. Ignoring error.");
      return true;
    case 1:
      if (lowerCaseSql.contains("values")) {
        LOG.warn("Unique constraint violated. Ignoring error.");
        return true;
      }
      return false;
    case 957:
      if (lowerCaseSql.contains("rename column")) {
        LOG.warn("Column already renamed. Ignoring error.");
        return true;
      }
      return false;
    case 942:
      if (words.hasNext()) {
        word = words.next();
      }
      if ("drop".equals(word)) {
        LOG.warn("Can't drop. Table or view does not exist. Ignoring error.");
        return true;
      }
      return false;
    case 904:
      if (lowerCaseSql.contains("drop")) {
        LOG.warn("Can't drop column. The column name entered is either missing or invalid. Ignoring error.");
        return true;
      } else if (lowerCaseSql.contains("set unused column")) {
        LOG.warn("Can't set column to be unused. The column name entered is either missing or invalid. Ignoring error.");
        return true;
      }
      return false;
    case 1442:
      LOG.warn("Column to be modified to NOT NULL is already NOT NULL. Ignoring error.");
      return true;
    case 1451:
      LOG.warn("Column to be modified to NULL is already NULL. Ignoring error.");
      return true;
    case 2260:
      LOG.warn("Table can have only one primary key. Ignoring error.");
      return true;
    case 1430:
      LOG.warn("Column being added already exists in table. Ignoring.");
      return true;
    case 2275:
      LOG.warn("Such a referential constraint already exists in the table. Ignoring.");
      return true;
    case 2443:
      LOG.warn("Cannot drop constraint - nonexistent constraint. Ignoring.");
      return true;
    case 2261:
      LOG.warn("Such unique or primary key already exists in the table. Ignoring.");
      return true;
    case 2264:
      LOG.warn("Name already used by an existing constraint. Ignoring.");
      return true;
    case 12006:
      LOG.warn("Materialized view with the same name already exists. Ignoring.");
      return true;
    case 12000:
      LOG.warn("Materialized view log for the same table already exists. Ignoring.");
      return true;
    case 2430:
      LOG.warn("Cannot enable constraint - no such constraint. Ignoring.");
      return true;
    case 2431:
      LOG.warn("Cannot disable constraint - no such constraint. Ignoring.");
      return true;
    case 4080:
      if (lowerCaseSql.contains("drop")) {
        LOG.warn("Trigger does not exist. Cannot drop. Ignoring error.");
        return true;
      }
      return false;
    case 4043:
      if (lowerCaseSql.contains("drop")) {
        LOG.warn("Object does not exist. Cannot drop. Ignoring error.");
        return true;
      }
      return false;
    case 1418:
      if (lowerCaseSql.contains("drop")) {
        LOG.warn("Cannot drop index. Specified index does not exist. Ignoring.");
        return true;
      }
      return false;
    case 2289:
      if (lowerCaseSql.contains("drop")) {
        LOG.warn("Cannot drop sequence. Specified sequence does not exist. Ignoring.");
        return true;
      }
      return false;
    case 1434:
      LOG.warn("Private synonym to be dropped does not exist. Ignoring.");
      return true;
    default:
      return false;
    }
  }
}
