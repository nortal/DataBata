package eu.databata.engine.version;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public interface VersionProvider {
  String getVersion();
  void setVersion(String version);
}
