package eu.databata.engine.version;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 *
 */
public class StandardVersionProvider implements VersionProvider {
  private String version;

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public void setVersion(String version) {
    this.version = version;
  }
}
