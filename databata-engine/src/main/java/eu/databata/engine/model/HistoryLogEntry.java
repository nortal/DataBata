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
