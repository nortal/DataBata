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
package eu.databata.engine.osgi;

import eu.databata.PropagatorFileHandler;
import eu.databata.SupplementPropagation;
import eu.databata.engine.util.PropagationUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;

public class PropagatorOsgiFileHandler implements PropagatorFileHandler {
  private static final Logger LOG = Logger.getLogger(SupplementPropagation.class);
  private Bundle bundle;

  @Override
  public File[] findSupplementFiles(File directory, String fileSearchRegexp, String dbCode) {
    String searchPath = PropagationUtils.changePathSlashes(directory.getPath());
    Enumeration<URL> findEntries = bundle.findEntries(searchPath, fileSearchRegexp, true);
    URL[] files = convertURLEnumerationToArray(findEntries);

    List<File> fileToPropagate = new ArrayList<File>();
    for (URL resource : files) {
      if (resource.getFile().replace(searchPath + "/", "").replace(dbCode + "/", "").contains("/")) {
        continue;
      }
      LOG.info("Supplement file: " + resource.getFile());
      fileToPropagate.add(makeFile(resource));
    }

    return fileToPropagate.toArray(new File[] {});
  }

  @Override
  public Map<String, File> findChanges(File directory, String databaseCode) {
    URL[] changesDirs = getChangesDirs(directory);
    LOG.info("Found " + changesDirs.length + " changes");
    Map<String, File> changes = new TreeMap<String, File>();
    for (URL url : changesDirs) {
      // URL url = change.getURL();
      // File file = new File(url.getPath());
      if (!(url.getPath().endsWith(".sql") || url.getPath().endsWith(".txt"))) {
        URL[] orderFiles = getOrderFile(url);
        String orderFilePrefix = "";
        if (orderFiles.length != 0) {
          File orderFile = new File(orderFiles[0].getPath(), ORDER_FILE);
          orderFilePrefix = PropagationUtils.readFile(orderFile);
        }
        File file = new File(url.getPath());
        makeDir(url);
        changes.put(orderFilePrefix + file.getName(), file);
      }
    }
    return changes;
  }

  @Override
  public File[] findSqls(File directory, String fileSearchRegexp) {
    String searchPath = PropagationUtils.changePathSlashes(directory.getPath());
    Enumeration<URL> findEntries = bundle.findEntries(searchPath, "*.*", true);
    URL[] urls = convertURLEnumerationToArray(findEntries);
    Map<String, File> files = new HashMap<String, File>();
    for (URL resource : urls) {
      files.put(resource.getPath(), makeFile(resource));
    }
    for (Iterator<Map.Entry<String, File>> i = files.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String, File> file = i.next();
      if (!file.getKey().substring(file.getKey().lastIndexOf('/') + 1).startsWith("go")) {
        i.remove();
      }
    }

    return files.values().toArray(new File[] {});
  }

  private URL[] getChangesDirs(File directory) {
    String searchPath = PropagationUtils.changePathSlashes(directory.getPath());
    LOG.info("Searching for files in " + searchPath);
    Enumeration<URL> findEntries = bundle.findEntries(searchPath, "*", false);
    return convertURLEnumerationToArray(findEntries);
  }

  private URL[] getOrderFile(URL parentDir) {
    String searchPath = PropagationUtils.changePathSlashes(parentDir.getPath());
    Enumeration<URL> findEntries = bundle.findEntries(searchPath, PropagatorFileHandler.ORDER_FILE, false);
    return convertURLEnumerationToArray(findEntries);
  }

  private File makeFile(URL url) {
    BufferedReader br;
    try {
      br = new BufferedReader(new InputStreamReader(url.openStream(), PropagationUtils.INPUTFILES_ENCODING));

      String tmpDir = System.getProperty("java.io.tmpdir");
      LOG.info("Url " + url.getPath());
      File file = new File(tmpDir + url.getPath());
      String inputLine;

      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }

      BufferedWriter bw =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getAbsoluteFile()),
                                                    PropagationUtils.INPUTFILES_ENCODING));

      while ((inputLine = br.readLine()) != null) {
        bw.write(inputLine + "\n");
      }

      bw.close();
      br.close();

      return file;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File makeDir(URL url) {
    String tmpDir = System.getProperty("java.io.tmpdir");
    LOG.info("Url " + url.getPath());
    File file = new File(tmpDir + url.getPath());

    if (!file.exists()) {
      file.mkdirs();
    }
    return file;
  }

  private URL[] convertURLEnumerationToArray(Enumeration<URL> enm) {
    Set<URL> resources = new HashSet<URL>(4);
    while (enm != null && enm.hasMoreElements()) {
      resources.add(enm.nextElement());
    }
    return (URL[]) resources.toArray(new URL[resources.size()]);
  }

  public void setBundle(Bundle bundle) {
    this.bundle = bundle;
  }
}
