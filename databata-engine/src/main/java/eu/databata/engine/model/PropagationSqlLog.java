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
