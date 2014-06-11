package eu.databata.engine.model;

import java.util.Date;

public class HistoryLogEntry {
  private String sqlText;
  private int sqlRows;
  private int sqlErrorCode;
  private String sqlErrorText;
  private Date date;
  private String dbChange;
  private double executionTime; // in seconds

  public HistoryLogEntry(String sqlText,
                         int sqlRows,
                         int sqlErrorCode,
                         String sqlErrorText,
                         Date date,
                         String dbChange,
                         double executionTime) {
    this.date = date;
    this.sqlErrorCode = sqlErrorCode;
    this.sqlRows = sqlRows;
    this.sqlText = sqlText;
    this.sqlErrorText = sqlErrorText;
    this.dbChange = dbChange;
    this.executionTime = executionTime;
  }

  public String getSqlText() {
    return sqlText;
  }

  public void setSqlText(String sqlText) {
    this.sqlText = sqlText;
  }

  public int getSqlRows() {
    return sqlRows;
  }

  public void setSqlRows(int sqlRows) {
    this.sqlRows = sqlRows;
  }

  public int getSqlErrorCode() {
    return sqlErrorCode;
  }

  public void setSqlErrorCode(int sqlErrorCode) {
    this.sqlErrorCode = sqlErrorCode;
  }

  public String getSqlErrorText() {
    return sqlErrorText;
  }

  public void setSqlErrorText(String sqlErrorText) {
    this.sqlErrorText = sqlErrorText;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getDbChange() {
    return dbChange;
  }

  public void setDbChange(String dbChange) {
    this.dbChange = dbChange;
  }

  public double getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(double executionTime) {
    this.executionTime = executionTime;
  }

}
