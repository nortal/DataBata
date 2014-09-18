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
package eu.databata.engine.exeptions;

import java.sql.Connection;

import org.hsqldb.cmdline.SqlFile;

import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * @author Igor Bossenko <mailto:igor@webmedia.ee>
 */
public class MicrosoftSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(MicrosoftSQLExceptionHandler.class);

  public boolean isHandled(SQLException e, String sql, SqlFile file, Connection newConnection) {
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
