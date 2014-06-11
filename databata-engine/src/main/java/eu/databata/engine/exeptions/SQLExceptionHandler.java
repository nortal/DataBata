package eu.databata.engine.exeptions;

import java.sql.SQLException;

/**
 * We want to skip SQL exceptions with certain error codes and continue invocation of the propagator.
 * 
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public interface SQLExceptionHandler {
  boolean isHandled(SQLException e, String sql);
}
