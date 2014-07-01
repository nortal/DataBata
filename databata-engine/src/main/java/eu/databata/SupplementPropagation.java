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
package eu.databata;

import eu.databata.engine.version.VersionProvider;
import eu.databata.engine.version.VersionUtil;

import eu.databata.engine.util.PropagationUtils;

import eu.databata.engine.model.PropagationObject;
import eu.databata.engine.model.PropagationObject.ObjectType;

import eu.databata.engine.dao.PropagationDAO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * An object of this class provides the propagation of one type of supplement data, i.e. packages, package headers,
 * views, or triggers.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 * @author Maksim Boiko
 */
public class SupplementPropagation {
  private static final Logger LOG = Logger.getLogger(SupplementPropagation.class);

  private File directory;
  private Map<String, PropagationObject> propagatedObjectsHashes = new HashMap<String, PropagationObject>(); // hash ->
  // propagation
  // object
  private SQLPropagationTool sqlExecutor;
  private PropagationDAO propagationDAO;
  private ObjectType objectType;
  private String moduleName;
  private String fileSearchRegexp;
  private VersionProvider versionProvider;
  private boolean simulationMode;
  private File[] propagatedFiles;
  private PropagatorFileHandler propagatorFileLocator;

  public SupplementPropagation(File directory,
                               ObjectType objectType,
                               String moduleName,
                               SQLPropagationTool sqlExecutor,
                               PropagationDAO propagationDAO,
                               String fileSearchRegexp) {
    this.directory = directory;
    this.objectType = objectType;
    this.moduleName = moduleName;
    this.sqlExecutor = sqlExecutor;
    this.propagationDAO = propagationDAO;
    this.fileSearchRegexp = fileSearchRegexp;
  }

  public void setSimulationMode(boolean simulationMode) {
    this.simulationMode = simulationMode;
  }

  public void setVersionProvider(VersionProvider versionProvider) {
    this.versionProvider = versionProvider;
  }

  public void setPropagatorFileHandler(PropagatorFileHandler propagatorFileLocator) {
    this.propagatorFileLocator = propagatorFileLocator;
  }

  public void propagate() {
    if (!canPropagate()) {
      LOG.info("Propagation of " + objectType.name()
          + " objects is not activated. Define corresponding property: 'viewDir', 'packageDir', 'triggerDir'");

      return;
    }
    Set<String> localObjectsHashes = new HashSet<String>();
    Set<String> modifiedObjectsNames = new HashSet<String>();
    List<PropagationObject> propagationObjectsToUpdate = new ArrayList<PropagationObject>();

    long start = System.currentTimeMillis();

    for (File file : propagatedFiles) {
      if (LOG.isDebugEnabled()) {
        LOG.info("Propagating file <" + file.getName() + ">, " + this.objectType.name());
      }
      String md5 = null;
      try {
        md5 = getHash(new FileInputStream(file));
      } catch (FileNotFoundException e) {
        new RuntimeException(e);
      }
      localObjectsHashes.add(md5);
      if (!propagatedObjectsHashes.containsKey(md5)) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("New hash is calculated for <" + file.getName() + "> : " + md5);
        }
        String modifiedObjectName = PropagationUtils.removeExtension(file);
        PropagationObject propagationObject =
            new PropagationObject(moduleName, modifiedObjectName, file, this.objectType, md5);
        if (versionProvider != null) {
          propagationObject.setVersion(versionProvider.getVersion());
        }
        modifiedObjectsNames.add(modifiedObjectName);
        propagationObjectsToUpdate.add(propagationObject);
      } else {
        propagatedObjectsHashes.remove(md5);
      }
    }
    LOG.info("Hash calculation for <" + objectType.name() + "> took " + (System.currentTimeMillis() - start) + " ms.");

    for (PropagationObject propagationObject : propagationObjectsToUpdate) {
      LOG.info("PROPAGATION OBJECT TO UPDATE " + propagationObject.getObjectName() + "; "
          + propagationObject.getObjectType().name());
    }
    propagateObjects(propagationObjectsToUpdate);
    dropObjects(modifiedObjectsNames);
  }

  /**
   * Searches for *.sql files in given directory, also searches for *.sql files in subdirectory with the name of
   * databaseCode variable set ('ORA', 'MSS', ...). Returns union of described searches results.
   */
  public void collectPropagatedFiles() {
    if (!canPropagate()) {
      return;
    }
    LOG.info("Supplement directory path: " + directory.getPath());

    this.propagatedFiles =
        propagatorFileLocator.findSupplementFiles(this.directory, fileSearchRegexp, sqlExecutor.getDatabaseCode());
  }

  protected void propagateObjects(List<PropagationObject> propagationObjects) {
    for (PropagationObject propagationObject : propagationObjects) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Propagating <" + propagationObject.getObjectName() + ">");
      }
      if (!canUpdateWithSearch(propagationObject.getObjectName())) {
        LOG.warn("\n!!! Object with name " + propagationObject.getObjectName()
            + " will NOT be updated (you try to install smaller version)\n");
        continue;
      }
      sqlExecutor.setCurrentDbChange(propagationObject.getPropagatedFile().getName());
      sqlExecutor.executeFile(null, propagationObject.getPropagatedFile());
      if (!simulationMode) {
        updateMD5Entry(propagationObject);
      }
    }
  }

  private void updateMD5Entry(PropagationObject propagationObject) {
    if (propagationDAO.hasPropagationObjectEntry(propagationObject.getObjectName(), propagationObject.getModuleName())) {
      propagationDAO.updatePropagationObjectEntry(propagationObject);
    } else {
      propagationDAO.insertPropagationObjectEntry(propagationObject);
    }
  }

  protected void dropObjects(Set<String> modifiedObjectsNames) {
    for (Map.Entry<String, PropagationObject> entry : propagatedObjectsHashes.entrySet()) {
      if (modifiedObjectsNames.contains(entry.getValue().getObjectName())) {
        // do not drop modified objects.
        continue;
      }
      if (!canUpdate(entry.getValue().getVersion())) {
        LOG.warn("\n!!! Object with name " + entry.getValue().getObjectName()
            + " will NOT be updated (you try to install smaller version)\n");
        continue;
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("Dropping <" + entry.getValue().getObjectName() + ">");
      }
      sqlExecutor.dropObject(objectType.getSqlName() + " " + entry.getValue().getObjectName());
      propagationDAO.removePropagationObjectEntry(entry.getValue().getObjectName(), moduleName);
    }
  }

  private boolean canUpdateWithSearch(String objectName) {
    for (PropagationObject propagationObject : propagatedObjectsHashes.values()) {
      if (propagationObject.getObjectName().equals(objectName)) {
        return canUpdate(propagationObject.getVersion());
      }
    }

    return true;
  }

  private boolean canUpdate(String version) {
    if (versionProvider == null || StringUtils.isEmpty(versionProvider.getVersion())) {
      return true;
    }
    if (StringUtils.isEmpty(version)) {
      return true;
    }

    return VersionUtil.isEqualOrGreater(versionProvider.getVersion(), version);
  }

  private String getHash(InputStream in) {
    MessageDigest md5;
    try {
      md5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException("No MD5 algorithm found!", e);
    }
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int read;
    try {
      while ((read = in.read(buffer)) != -1) {
        os.write(buffer, 0, read);
      }
      os.flush();
      md5.update(os.toByteArray());
    } catch (IOException e) {
      throw new IllegalStateException("Error reading file.", e);
    } finally {
      try {
        in.close();
        os.close();
      } catch (IOException e) {
        throw new IllegalStateException("Error closing stream ", e);
      }
    }
    return convertToHex(md5.digest());
  }

  private String convertToHex(byte[] data) {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < data.length; i++) {
      int halfbyte = (data[i] >>> 4) & 0x0F;
      int two_halfs = 0;
      do {
        if ((0 <= halfbyte) && (halfbyte <= 9))
          buf.append((char) ('0' + halfbyte));
        else
          buf.append((char) ('a' + (halfbyte - 10)));
        halfbyte = data[i] & 0x0F;
      } while (two_halfs++ < 1);
    }
    return buf.toString();
  }

  private boolean canPropagate() {
    return directory != null;
  }

  public void addHash(String objectMd5Hash, PropagationObject propagationObject) {
    propagatedObjectsHashes.put(objectMd5Hash, propagationObject);
  }
}
