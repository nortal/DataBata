/**
 * Copyright 2014 Nortal AS Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package eu.databata.engine.spring;

import eu.databata.Propagator;
import eu.databata.PropagatorFileHandler;
import eu.databata.engine.util.PropagationUtils;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

public class PropagatorSpringFileHandler implements PropagatorFileHandler {
  private static final Logger LOG = Logger.getLogger(Propagator.class);

  @Override
  public File[] findSupplementFiles(File directory, String fileSearchRegexp, String dbCode) {
    if (!directory.isDirectory()) {
      return new File[] {};
    }
    FilesByRegexpFilter regexpFileFilter = new FilesByRegexpFilter(fileSearchRegexp);
    File[] directorySqlFiles = directory.listFiles(regexpFileFilter);
    File[] databaseSpecificDirectories = directory.listFiles(new DatabaseSpecificFilenameFilter(dbCode));
    if (databaseSpecificDirectories.length > 0) {
      File[] databaseSpecificFiles = databaseSpecificDirectories[0].listFiles(regexpFileFilter);
      LOG.debug(databaseSpecificFiles.length + " specific files for database with code '" + dbCode
          + "' will be loaded.");
      directorySqlFiles = (File[]) ArrayUtils.addAll(directorySqlFiles, databaseSpecificFiles);
    }

    return directorySqlFiles;
  }

  @Override
  public Map<String, File> findChanges(File directory, String databaseCode) {
    Map<String, File> changes = new TreeMap<String, File>();
    collectChanges(changes, directory, databaseCode, "");

    return changes;
  }

  private void collectChanges(Map<String, File> changes, File directory, String databaseCode, String keyPrefix) {
    for (File file : directory.listFiles(new ChangesDirFilter(databaseCode))) {
      if (file.isDirectory()) {
        File orderFile = new File(file, ORDER_FILE);
        if (orderFile != null && orderFile.isFile()) {
          changes.put(PropagationUtils.readFile(orderFile) + file.getName(), file);
        } else if (file.listFiles(new FilesByRegexpFilter("go.*\\.sql")).length > 0) {
          keyPrefix = keyPrefix.equals("") ? "" : keyPrefix + "/";
          changes.put(keyPrefix + file.getName(), file);
        }
        collectChanges(changes, file, databaseCode, file.getName());
      }
    }
  }

  @Override
  public File[] findSqls(File directory, String fileSearchRegexp) {
    return directory.listFiles(new FilesByRegexpFilter(fileSearchRegexp));
  }

  private class ChangesDirFilter implements FilenameFilter {
    private final String databaseCode;

    private ChangesDirFilter(String databaseCode) {
      this.databaseCode = databaseCode;
    }

    @Override
    public boolean accept(File dir, String name) {
      if (databaseCode == null) {
        return false;
      }
      if (name.matches("^" + databaseCode + "$")) {
        return false;
      }

      if (name.matches("go.*\\.sql")) {
        return false;
      }

      return true;
    }

  }

  private class FilesByRegexpFilter implements FilenameFilter {
    private final String fileSearchRegexp;

    public FilesByRegexpFilter(String fileSearchRegexp) {
      this.fileSearchRegexp = fileSearchRegexp;
    }

    public boolean accept(File file, String name) {
      return name.matches(fileSearchRegexp);
    }
  };

  private class DatabaseSpecificFilenameFilter implements FilenameFilter {
    private final String databaseCode;

    private DatabaseSpecificFilenameFilter(String databaseCode) {
      this.databaseCode = databaseCode;
    }

    public boolean accept(File file, String name) {
      if (databaseCode == null) {
        return false;
      }
      return name.matches("^" + databaseCode + "$");
    }
  }

}
