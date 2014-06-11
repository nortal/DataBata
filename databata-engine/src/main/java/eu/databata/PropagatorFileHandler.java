package eu.databata;

import java.io.File;
import java.util.Map;

/**
 * 
 * @author Maksim Boiko
 *
 */
public interface PropagatorFileHandler {
  /**
   * File that contains order information.
   */
  public static final String ORDER_FILE = "order.txt";
  
  /**
   * Locates all triggers, views, packages files inside given directory using given regular expression.
   * 
   * @return Array of files found by given expression.
   */
  File[] findSupplementFiles(File directory, String fileSearchRegexp, String dbCode);

  /**
   * Locates directory with changed scripts.
   * 
   * @return Map of found directories where key is usually directory name.
   */
  Map<String, File> findChanges(File directory);
  
  File[] findSqls(File directory, String fileSearchRegexp);
}
