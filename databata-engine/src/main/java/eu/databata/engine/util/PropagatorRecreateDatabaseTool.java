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
package eu.databata.engine.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.SqlExecutionCallback;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

/**
 * @author Maksim Boiko <mailto:max.boiko@gmail.com>
 */
public class PropagatorRecreateDatabaseTool {
  private static final Logger LOG = Logger.getLogger(PropagatorRecreateDatabaseTool.class);

  public static void main(String[] args) {
    ClassLoader classLoader = PropagatorRecreateDatabaseTool.class.getClassLoader();
    InputStream resourceAsStream = classLoader.getResourceAsStream("databata.properties");
    Properties propagatorProperties = new Properties();
    try {
      propagatorProperties.load(resourceAsStream);
    } catch (FileNotFoundException e) {
      LOG.error("Sepecified file 'databata.properties' not found");
    } catch (IOException e) {
      LOG.error("Sepecified file 'databata.properties' cannot be loaded");
    }

    SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
    dataSource.setDriverClassName(propagatorProperties.getProperty("db.propagation.driver"));
    dataSource.setUrl(propagatorProperties.getProperty("db.propagation.dba.connection-url"));
    dataSource.setUsername(propagatorProperties.getProperty("db.propagation.dba.user"));
    dataSource.setPassword(propagatorProperties.getProperty("db.propagation.dba.password"));
    dataSource.setSuppressClose(true);

    String databaseName = "undefined";
    try {
      databaseName = dataSource.getConnection().getMetaData().getDatabaseProductName();
    } catch (SQLException e) {
      LOG.error("Cannot get connection by specified url", e);
    }
    String databaseCode = PropagationUtils.getDatabaseCode(databaseName);
    LOG.info("Database with code '" + databaseCode + "' is identified.");

    String submitFileName =  "META-INF/databata/" + databaseCode + "_recreate_database.sql";
    String fileContent = "";
    try {
      fileContent = getFileContent(classLoader, submitFileName);
    } catch (IOException e) {
      LOG.info("File with name '" + submitFileName
          + "' cannot be read from classpath. Trying to load default submit file.");
    }
    if (fileContent == null || "".equals(fileContent)) {
      String defaultSubmitFileName = "META-INF/databata/" + databaseCode + "_recreate_database.default.sql";
      try {
        fileContent = getFileContent(classLoader, defaultSubmitFileName);
      } catch (IOException e) {
        LOG.info("File with name '" + defaultSubmitFileName
            + "' cannot be read from classpath. Trying to load default submit file.");
      }
    }

    if (fileContent == null) {
      LOG.info("File content is empty. Stopping process.");
      return;
    }

    fileContent = replacePlaceholders(fileContent, propagatorProperties);

    SqlFile sqlFile = null;
    try {
      sqlFile = new SqlFile(fileContent, null, submitFileName, new SqlExecutionCallback() {

        @Override
        public void handleExecuteSuccess(String sql, int arg1, double arg2) {
          LOG.info("Sql is sucessfully executed \n ======== \n" + sql + "\n ======== \n");
        }

        @Override
        public void handleException(SQLException arg0, String sql) throws SQLException {
          LOG.info("Sql returned error \n ======== \n" + sql + "\n ======== \n");
        }
      }, null);
    } catch (IOException e) {
      LOG.error("Error when initializing SqlTool", e);
    }
    try {
      sqlFile.setConnection(dataSource.getConnection());
    } catch (SQLException e) {
      LOG.error("Error is occured when setting connection", e);
    }

    try {
      sqlFile.execute();
    } catch (SqlToolError e) {
      LOG.error("Error when creating user", e);
    } catch (SQLException e) {
      LOG.error("Error when creating user", e);
    }
  }

  private static String getFileContent(ClassLoader contextClassLoader, String fileName) throws IOException {
    LOG.info("Loading file '" + fileName + "'");
    InputStream submitFileStream = contextClassLoader.getResourceAsStream(fileName);
    if (submitFileStream == null) {
      LOG.info("File with name '" + fileName + "' cannot be read from classpath. Trying to load default submit file.");
      return null;
    }
    InputStreamReader streamReader = new InputStreamReader(submitFileStream);
    BufferedReader bufferedReader = new BufferedReader(streamReader);

    String result = "";
    while (bufferedReader.ready()) {
      result += bufferedReader.readLine();
      result += "\n";
    }

    return result;
  }

  private static String replacePlaceholders(String script, Properties properties) {
    String result = "";
    try {
      result = script.replaceAll("#\\{db.propagation.user\\}", properties.getProperty("db.propagation.user"));
      result = result.replaceAll("#\\{db.propagation.dba.user\\}", properties.getProperty("db.propagation.dba.user"));
      result = result.replaceAll("#\\{db.propagation.password\\}", properties.getProperty("db.propagation.password"));
    } catch (Exception e) {
      LOG.error("Error occured when replacing placeholders", e);
    }
    return result;
  }
}
