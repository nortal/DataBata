package eu.databata.engine.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.cmdline.SqlExecutionCallback;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Maksim Boiko (max@webmedia.ee)
 */
public class PropagatorTableExport {

  public static void main(String[] args) {
    JdbcTemplate jdbcTemplate = initializeExport();
    String delimiter = askDelimiter();
    String tableNamePattern = askTableName();

    exportTablesData(jdbcTemplate, delimiter, tableNamePattern);

    System.out.println("Table export finished.");
  }

  private static void exportTablesData(JdbcTemplate jdbcTemplate, String delimiter, String tableNamePattern) {
    try {
      System.out.println("Trying to load tables from metadata");
      Connection connection = jdbcTemplate.getDataSource().getConnection();
      ResultSet tables = connection.getMetaData().getTables(null, null, tableNamePattern, new String[] { "TABLE" });
      while (tables.next()) {
        System.out.println("Exporting data from " + tables.getString("TABLE_NAME"));
        exportData(jdbcTemplate, tables.getString("TABLE_NAME"), delimiter);
      }
    } catch (SQLException e) {
      System.out.println("\nError when trying to get table names from DB.");
    }
  }

  private static JdbcTemplate initializeExport() {
    ClassPathXmlApplicationContext applicationContext =
        new ClassPathXmlApplicationContext("WEB-INF/propagator-db-beans.xml");
    JdbcTemplate jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");
    System.out.println("\nDB table export utility is activated.");
    return jdbcTemplate;
  }

  private static void exportData(JdbcTemplate jdbcTemplate, String tableName, String delimiter) {
    String exportCommand = "* *DSV_COL_DELIM = " + delimiter + "\n\\x " + tableName + "\n";
    System.out.println("Exporting table using command:\n" + exportCommand);
    System.out.println("Result file with name '" + tableName
        + "'.dsv will be located inside current process execution folder.\n");
    Connection connection = null;
    try {
      SqlFile file = new SqlFile(exportCommand, null, tableName, new SqlExecutionCallback() {
        @Override
        public void handleExecuteSuccess(String sql, int i, double d) {
          System.out.println("Sql execution success -> " + sql);
        }

        @Override
        public void handleException(SQLException sqlexception, String sql) throws SQLException {
          System.out.println("Sql execution error -> " + sql);
        }
      }, null);

      connection = jdbcTemplate.getDataSource().getConnection();
      file.setConnection(connection);
      file.execute();
    } catch (IOException e) {
      System.out.println("Export finished with error\n" + e);
    } catch (SqlToolError e) {
      System.out.println("Export finished with error\n" + e);
    } catch (SQLException e) {
      System.out.println("Export finished with error\n" + e);
    } finally {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private static String askTableName() {
    System.out.println("Insert table name:");
    Scanner input = new Scanner(System.in);
    String name = input.nextLine();
    input.close();
    return name;
  }

  private static String askDelimiter() {
    System.out.println("Insert data delimiter [|]:");
    Scanner delimiterInput = new Scanner(System.in);
    String delimiter = delimiterInput.nextLine();
    if (StringUtils.isEmpty(delimiter)) {
      delimiter = "|";
    }
    delimiterInput.close();
    return delimiter;
  }
}