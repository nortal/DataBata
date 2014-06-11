package eu.databata.engine.model;

import java.util.Date;

public class HistoryLogEntry {
  public String sqlText;
  public int sqlRows;
  public int sqlErrorCode;
  public String sqlErrorText;
  public Date date;
  public String dbChange;
  public double executionTime; // in seconds

  public HistoryLogEntry(String sqlText, int sqlRows, int sqlErrorCode, String sqlErrorText, Date date, String dbChange, double executionTime) {
    this.date = date;
    this.sqlErrorCode = sqlErrorCode;
    this.sqlRows = sqlRows;
    this.sqlText = sqlText;
    this.sqlErrorText = sqlErrorText;
    this.dbChange = dbChange;
    this.executionTime = executionTime;
  }
}
