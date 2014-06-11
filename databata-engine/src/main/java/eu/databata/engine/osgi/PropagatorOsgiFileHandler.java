package eu.databata.engine.osgi;

import eu.databata.engine.util.PropagationUtils;

import eu.databata.PropagatorFileHandler;
import eu.databata.SupplementPropagation;

import java.util.TreeMap;

import java.util.HashMap;
import java.util.Iterator;

import java.util.Map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
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

    File[] fileToPropagate = new File[files.length];
    int fileIndex = 0;
    for (URL resource : files) {
      LOG.info("Supplement file: " + resource.getFile());
      fileToPropagate[fileIndex++] = makeFile(resource);
    }

    return null;
  }

  @Override
  public Map<String, File> findChanges(File directory) {
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
        changes.put(orderFilePrefix + file.getName(), makeFile(url));
      }
    }
    return null;
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
      br = new BufferedReader(new InputStreamReader(url.openStream()));

      String tmpDir = System.getProperty("java.io.tmpdir");
      LOG.info("Url " + url.getPath());
      File file = new File(tmpDir + url.getPath());
      String inputLine;

      if (!file.exists()) {
        file.getParentFile().mkdirs();
        file.createNewFile();
      }

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);

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
