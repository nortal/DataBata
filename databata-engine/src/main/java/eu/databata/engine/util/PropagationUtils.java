package eu.databata.engine.util;

import eu.databata.engine.exeptions.SQLExceptionHandler;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

/**
 * Utilities for database propagator.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 */
public final class PropagationUtils {
  private static final Logger LOG = Logger.getLogger(PropagationUtils.class);
  private static final String INPUTFILES_ENCODING = "UTF-8";

  /**
   * This method is suitable for reading files not larger than 2 GB.
   */
  private static String readFile(File file, String encoding) {
    String result = null;
    try {
      FileInputStream inputStream = new FileInputStream(file);
      try {
        byte[] bytes = new byte[(int) inputStream.getChannel().size()];
        inputStream.read(bytes);
        result = Charset.forName(encoding).decode(ByteBuffer.wrap(bytes)).toString();
      } finally {
        inputStream.close();
      }
    } catch (Exception e) {
      LOG.warn("Failed to read file: " + file.getName());
      throw new RuntimeException(e);
    }
    return dos2Unix(result);
  }

  /**
   * This method is suitable for reading files not larger than 2 GB.
   */
  public static String readFile(File file) {
    return readFile(file, INPUTFILES_ENCODING);
  }

  /**
   * This method is suitable for reading files not larger than 2 GB.
   */
  public static String readFile(InputStream inputStream) {
    BufferedReader br = null;
    StringBuilder result = new StringBuilder();

    try {
      br = new BufferedReader(new InputStreamReader(inputStream));
      String line = br.readLine();
      while (line != null) {
        result.append(line);
        line = br.readLine();
      }
    } catch (Exception e) {
      LOG.error("Failed to read file.");
      throw new RuntimeException(e);
    } finally {
      try {
        if (br != null)
          br.close();
        if (inputStream != null)
          inputStream.close();
      } catch (IOException e) {
        LOG.error("Failed to read file.");
        throw new RuntimeException(e);
      }
    }
    return result.toString();
  }

  public static String removeExtension(String s) {
    return s.substring(0, s.lastIndexOf('.'));
  }

  public static String removeExtension(File file) {
    String s = file.getName();
    return s.substring(0, s.lastIndexOf('.'));
  }

  // TODO: to remove? Max
  protected static String removeEndingSemicolon(String contents) {
    String s = contents.trim();
    if (s.endsWith(";")) {
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  // TODO: to remove? Max
  protected static String removeEndingBackwardSlash(String contents) {
    String s = contents.trim();
    if (s.endsWith("/")) {
      s = s.substring(0, s.length() - 1);
    }
    return s.trim();
  }

  // TODO: to remove? Max
  protected static List<String> scanFile(File file) throws FileNotFoundException {
    List<String> res = new ArrayList<String>();
    Scanner sc = new Scanner(new BufferedInputStream(new FileInputStream(file)), INPUTFILES_ENCODING);
    try {
      sc.useDelimiter(Pattern.compile(";[ \\t]*$", Pattern.MULTILINE));
      while (sc.hasNext()) {
        String s = sc.next().trim();

        // Cut off comments
        Scanner strSc = new Scanner(s);
        strSc.useDelimiter(Pattern.compile("$", Pattern.MULTILINE));
        StringBuilder sb = new StringBuilder();
        while (strSc.hasNext()) {
          String sp = strSc.next().trim();
          if (!sp.startsWith("--")) {
            sb.append(sp).append(System.getProperty("line.separator"));
          }
        }
        s = sb.toString().trim();

        if (!StringUtils.isBlank(s)) {
          res.add(dos2Unix(s));
        }
        strSc.close();
      }
    } finally {
      sc.close();
    }
    return res;
  }

  public static String dos2Unix(String s) {
    return StringUtils.remove(s, '\r');
  }

  // SQL exception handling
  public static void handleDataAccessException(DataAccessException e, String sql, SQLExceptionHandler handler) {
    if (!(e.getCause() instanceof SQLException) || !handler.isHandled((SQLException) e.getCause(), sql)) {
      throw e;
    }
  }

  // SQL exception handling
  public static void handleDataAccessException(SQLException e, String sql, SQLExceptionHandler handler)
      throws SQLException {
    if (!handler.isHandled(e, sql)) {
      throw e;
    }
  }

  /**
   * Needed to fix problems in OSGI findEntries method on Windows
   * 
   * @return
   */
  public static String changePathSlashes(String path) {
    return path.replaceAll("\\\\", "/");
  }

  public static String getPathLastFolder(String path) {
    String pathWithoutLastChar = path.substring(0, path.length());
    pathWithoutLastChar = pathWithoutLastChar.substring(pathWithoutLastChar.lastIndexOf("/") + 1);
    return pathWithoutLastChar;
  }

  public static String getDatabaseCode(String databaseName) {
    if (StringUtils.containsIgnoreCase(databaseName, "oracle")) {
      return "ORA";
    } else if (StringUtils.containsIgnoreCase(databaseName, "microsoft")) {
      return "MSS";
    } else if (StringUtils.containsIgnoreCase(databaseName, "anywhere")) {
      return "SA";
    }

    return null;
  }
}
