package org.hsqldb.cmdline;

import java.sql.SQLException;

/**
 * @author Maksim Boiko <mailto:max.boiko@gmail.com>
 */
public interface SqlExecutionCallback {
  /**
   * Fires when SQL statement is executed successfully, provides SQL statement which was executed, number of rows
   * affected and execution time.
   * 
   * @param sql SQL statement propagated to database.
   * @param updateCount Number of rows affected by propagated SQL statement.
   * @param executionTime Amount of time that propagated SQL statement took.
   */
  void handleExecuteSuccess(String sql, int updateCount, double executionTime);

  /**
   * Fires when some exception is thrown by SQL tool (SQL or syntax error).
   * 
   * @param sql SQL statement propagated to database.
   * @throws SQLException Throws given exception through if it is not recoverable.
   */
  void handleException(SQLException exception, String sql) throws SQLException;
}
