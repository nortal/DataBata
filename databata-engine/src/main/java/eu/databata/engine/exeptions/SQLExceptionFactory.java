package eu.databata.engine.exeptions;

/**
 * Factory for initializing exeprion handlers using database code got from driver.
 * 
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 * @author Igor Bossenko <mailto:igor@webmedia.ee>
 */
public class SQLExceptionFactory {
  private SQLExceptionFactory() {
  }

  public static SQLExceptionHandler newHandler(String databaseCode) {
    if ("ORA".equals(databaseCode)) {
      return new OracleSQLExceptionHandler();
    } else if ("SA".equals(databaseCode)) {
      return new SybaseSQLExceptionHandler();
    } else if ("MSS".equals(databaseCode)) {
      return new MicrosoftSQLExceptionHandler();
    }

    return null;
  }
}
