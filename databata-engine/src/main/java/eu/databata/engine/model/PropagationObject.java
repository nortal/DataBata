package eu.databata.engine.model;

import java.io.File;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class PropagationObject {
  private String moduleName;
  private String objectName;
  private ObjectType objectType;
  private String md5Hash;
  private File propagatedFile;
  private String version;
  
  public PropagationObject() {
  }
  
  public PropagationObject(String moduleName, String objectName, File propagatedFile, ObjectType objectType, String md5Hash) {
    this.moduleName = moduleName;
    this.objectName = objectName;
    this.propagatedFile = propagatedFile;
    this.objectType = objectType;
    this.md5Hash = md5Hash;
  }
  
  public String getModuleName() {
    return moduleName;
  }
  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }
  public String getObjectName() {
    return objectName;
  }
  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }
  public ObjectType getObjectType() {
    return objectType;
  }
  public void setObjectType(ObjectType objectType) {
    this.objectType = objectType;
  }
  public String getMd5Hash() {
    return md5Hash;
  }
  public void setMd5Hash(String md5Hash) {
    this.md5Hash = md5Hash;
  }
  public File getPropagatedFile() {
    return propagatedFile;
  }
  public void setPropagatedFile(File propagatedFile) {
    this.propagatedFile = propagatedFile;
  }
  public String getVersion() {
    return version;
  }
  public void setVersion(String version) {
    this.version = version;
  }


  public enum ObjectType {
    VIEW,
    TRIGGER,
    PACKAGE_HEADER,
    PACKAGE,
    FUNCTION,
    PROCEDURE;
    
    public String getSqlName() {
      if(this.name().contains("HEADER")) {
        return "PACKAGE";
      }
      
      return this.name();
    }
  }
}
