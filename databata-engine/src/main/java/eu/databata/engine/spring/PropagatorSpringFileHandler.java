package eu.databata.engine.spring;

import eu.databata.engine.util.PropagationUtils;

import eu.databata.Propagator;
import eu.databata.PropagatorFileHandler;

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
  public Map<String, File> findChanges(File directory) {
    Map<String, File> changes = new TreeMap<String, File>();
    for (File file : directory.listFiles()) {
      if (file.isDirectory()) {
        File orderFile = new File(file, ORDER_FILE);
        if (orderFile.isFile()) {
          changes.put(PropagationUtils.readFile(orderFile) + file.getName(), file);
        } else {
          changes.put(file.getName(), file);
        }
      }
    }
    
    return changes;
  }
  
  @Override
  public File[] findSqls(File directory, String fileSearchRegexp) {
    return directory.listFiles(new FilesByRegexpFilter(fileSearchRegexp));
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
