package eu.databata.engine.model;

import java.util.Date;

/**
 * @author Maksim Boiko
 */
public class PropagationLock {
  private String token;
  private Date lockTime;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Date getLockTime() {
    return lockTime;
  }

  public void setLockTime(Date lockTime) {
    this.lockTime = lockTime;
  }

}
