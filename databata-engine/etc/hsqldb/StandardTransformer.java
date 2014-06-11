package org.hsqldb.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

/**
 * Transforms some known grammar constructions from a abstract database dialect to dialect of database driver which is
 * used to make database propagation. Reads transformation definitions from ${DBMS}_transformations.properties file and
 * performs search and replace in given string, where DBMS variable stands for ORA, SA, MSS, etc. according to database
 * driver used.
 * 
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class StandardTransformer {
  private static final Logger LOG = Logger.getLogger(StandardTransformer.class);
  private LinkedHashMap<String, String> transformationRegistry = new LinkedHashMap<String, String>();

  public StandardTransformer(String dbmsTo) {
    Validate.notNull(dbmsTo, "Please define DBMS code of database to convert to.");
    readTransformations(dbmsTo);
  }

  private void readTransformations(String dbmsTo) {
    String transformationFileName = dbmsTo + "_transformations.properties";
    LOG.debug("Reading transformation file '" + transformationFileName + "'");
    InputStream transformationFileStream = this.getClass().getClassLoader().getResourceAsStream(transformationFileName);
    String errorMessage =
        "Transformations file cannot be loaded, make sure you have " + transformationFileName + " in classpath";
    if (transformationFileStream == null) {
      LOG.error(errorMessage);
      return;
    }
    try {
      readFromFile(transformationFileStream);
    } catch (IOException e) {
      LOG.error(errorMessage);
    }
  }

  private void readFromFile(InputStream inputStream) throws IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    while (reader.ready()) {
      String keyValueLine = reader.readLine();
      if (StringUtils.isEmpty(keyValueLine) || keyValueLine.startsWith("#")) {
        continue;
      }
      String key = getKey(keyValueLine);
      LOG.debug("Scanning rule: " + key);
      String value = getValue(keyValueLine);
      String matchExpression = getMatchExpression(value);
      String replacement = getReplacement(value);
      LOG.debug("Match expression " + matchExpression);
      LOG.debug("Replacement expression" + replacement);
      transformationRegistry.put(matchExpression, replacement);
    }
  }

  public String transform(String originalScript) {
    Validate.notNull(transformationRegistry,
                     "Make sure you have correctly configured propagation and have all needed files in classpath");
    String transformedScript = originalScript;
    for (Iterator<Map.Entry<String, String>> i = transformationRegistry.entrySet().iterator(); i.hasNext();) {
      Map.Entry<String, String> replacementEntry = i.next();

      String matchExpression = replacementEntry.getKey();
      String replacement = replacementEntry.getValue();
      Pattern pattern = Pattern.compile(matchExpression, Pattern.CASE_INSENSITIVE);
      transformedScript = pattern.matcher(transformedScript).replaceAll(replacement);
//      transformedScript = transformedScript.replaceAll(matchExpression, replacement);
    }
    boolean isTransformationPerformed = !transformedScript.equals(originalScript);
    if (isTransformationPerformed) {
      LOG.debug("Transformed script " + transformedScript);
    }

    return transformedScript;
  }

  private String getKey(String line) {
    return line.substring(0, line.indexOf("="));
  }

  private String getValue(String line) {
    return line.substring(line.indexOf("=") + 1);
  }

  private String getMatchExpression(String line) {
    return line.substring(0, line.indexOf("->"));
  }

  private String getReplacement(String line) {
    return line.substring(line.indexOf("->") + 2);
  }
}
