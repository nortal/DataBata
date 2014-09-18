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

/**
 * Factory for initializing exeprion handlers using database code got from driver.
 * 
 * @author Maksim Boiko <mailto:max.boiko@gmail.com>
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
    } else if ("PG".equals(databaseCode)) {
      return new PostgreSQLExceptionHandler();
    }

    return null;
  }
}
