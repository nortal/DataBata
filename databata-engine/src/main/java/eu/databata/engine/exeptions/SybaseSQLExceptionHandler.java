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
 * @author Maksim Boiko  {@literal<mailto:max.boiko@gmail.com>}
 */
public class SybaseSQLExceptionHandler implements SQLExceptionHandler {
  private static final Logger LOG = Logger.getLogger(SybaseSQLExceptionHandler.class);

  public boolean isHandled(SQLException e, String sql, SqlFile sqlFile, Connection newConnection) {
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
