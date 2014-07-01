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

import java.math.BigDecimal;
import java.util.Date;

public class PropagationSqlLog {
  private String moduleName;
  private String dbChangeCode;
  private String sqlText;
  private Long rowsUpdated;
  private Integer errorCode;
  private String errorText;
  private Date updateTime;
  private BigDecimal executionTime;

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getDbChangeCode() {
    return dbChangeCode;
  }

  public void setDbChangeCode(String dbChangeCode) {
    this.dbChangeCode = dbChangeCode;
  }

  public String getSqlText() {
    return sqlText;
  }

  public void setSqlText(String sqlText) {
    this.sqlText = sqlText;
  }

  public Long getRowsUpdated() {
    return rowsUpdated;
  }

  public void setRowsUpdated(Long rowsUpdated) {
    this.rowsUpdated = rowsUpdated;
  }

  public Integer getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(Integer errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorText() {
    return errorText;
  }

  public void setErrorText(String errorText) {
    this.errorText = errorText;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public BigDecimal getExecutionTime() {
    return executionTime;
  }

  public void setExecutionTime(BigDecimal executionTime) {
    this.executionTime = executionTime;
  }

}
