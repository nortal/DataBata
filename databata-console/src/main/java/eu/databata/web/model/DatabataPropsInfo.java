package eu.databata.web.model;

/**
 * @author Maksim Boiko
 */
public class DatabataPropsInfo {
  private String user;
  private String host;
  private String port;
  private String sid;
  private String service;

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getSid() {
    return sid;
  }

  public void setSid(String sid) {
    this.sid = sid;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

}
