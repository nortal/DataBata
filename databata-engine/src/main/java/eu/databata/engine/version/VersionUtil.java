package eu.databata.engine.version;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 */
public class VersionUtil {

  public static boolean isEqualOrGreater(String currentVersion, String oldVersion) {
    Version current = parseVersion(currentVersion);
    Version old = parseVersion(oldVersion);

    return new VersionComparator().compare(current, old) >= 0;
  }

  private static Version parseVersion(String version) {
    String[] versionNumbers = version.split("\\.");
    Version parsedVersion = new Version();
    try {
      parsedVersion.setStage(convertToNumber(versionNumbers[0]));
      parsedVersion.setMilestone(convertToNumber(versionNumbers[1]));
      parsedVersion.setSubmilestone(convertToNumber(versionNumbers[2]));
      parsedVersion.setVersion(convertToNumber(versionNumbers[3]));
    } catch (ArrayIndexOutOfBoundsException e) {
      return parsedVersion;
    }
    return parsedVersion;
  }

  private static int convertToNumber(String versionNumber) {
    try {
      if (versionNumber.contains("@")) {
        versionNumber = versionNumber.substring(0, versionNumber.indexOf('@'));
      }
      return Integer.parseInt(versionNumber);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static class Version implements Serializable {
    private int stage;
    private int milestone;
    private int submilestone;
    private int version;

    public int getStage() {
      return stage;
    }

    public void setStage(int stage) {
      this.stage = stage;
    }

    public int getMilestone() {
      return milestone;
    }

    public void setMilestone(int milestone) {
      this.milestone = milestone;
    }

    public int getSubmilestone() {
      return submilestone;
    }

    public void setSubmilestone(int submilestone) {
      this.submilestone = submilestone;
    }

    public int getVersion() {
      return version;
    }

    public void setVersion(int version) {
      this.version = version;
    }
  }

  private static class VersionComparator implements Comparator<Version> {

    @Override
    public int compare(Version first, Version second) {
      if (first.getStage() != second.getStage()) {
        return first.getStage() > second.getStage() ? 1 : -1;
      }
      if (first.getMilestone() != second.getMilestone()) {
        return first.getMilestone() > second.getMilestone() ? 1 : -1;
      }
      if (first.getSubmilestone() != second.getSubmilestone()) {
        return first.getSubmilestone() > second.getSubmilestone() ? 1 : -1;
      }
      if (first.getVersion() != second.getVersion()) {
        return first.getVersion() > second.getVersion() ? 1 : -1;
      }

      return 0;
    }

  }
}
