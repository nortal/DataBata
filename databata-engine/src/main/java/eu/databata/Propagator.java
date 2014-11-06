/**
 * Copyright 2014 Nortal AS Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package eu.databata;

import java.util.Date;

import org.springframework.transaction.TransactionDefinition;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.engine.exeptions.SQLExceptionFactory;
import eu.databata.engine.exeptions.SQLExceptionHandler;
import eu.databata.engine.model.PropagationObject;
import eu.databata.engine.model.PropagationObject.ObjectType;
import eu.databata.engine.util.DBHistoryLogger;
import eu.databata.engine.util.PropagationUtils;
import eu.databata.engine.util.PropagatorLock;
import eu.databata.engine.version.VersionProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.hsqldb.cmdline.StandardTransformer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Main database propagation class. After setting public properties, call the init method.
 * 
 * @author Aleksei Lissitsin <aleksei.lissitsin@webmedia.ee>
 * @author Maksim Boiko
 */
public abstract class Propagator implements InitializingBean {
  private static final Logger LOG = Logger.getLogger(Propagator.class);

  private static final String GO_SQL_PATTERN = "go.*\\.sql";
  private static final String TEST_SQL_PATTERN = "test.*\\.sql";

  protected DBHistoryLogger historyLogger;
  protected PropagatorLock propagatorLock;
  private boolean reloadFired = false;
  private Map<String, File> changes = new TreeMap<String, File>();

  private JdbcTemplate jdbcTemplate;
  private TransactionTemplate transactionTemplate;
  private boolean disableDbPropagation;
  private boolean useTestData;
  private boolean simulationMode;
  private boolean enableAutomaticTransformation;
  private String changeHistoryTable;
  private String lockTable;
  private String propagationObjectsTable;
  private String historyLogTable;
  private PropagationDAO propagationDAO;
  private boolean finished;

  private String revalidationStatement;
  private String environmentSql;

  private String databaseName;
  private String environmentCode;
  private String moduleName;
  protected List<String> dependsOn = new ArrayList<String>(0);

  private SQLPropagationTool sqlExecutor;
  private VersionProvider versionProvider;

  private File packagesDirectory;
  private File packagesHeaderDirectory;
  private File viewsDirectory;
  private File triggersDirectory;
  private File functionsDirectory;
  private File proceduresDirectory;
  private File changesDir;

  private SupplementPropagation packageHeaders = null;
  private SupplementPropagation packages = null;
  private SupplementPropagation views = null;
  private SupplementPropagation functions = null;
  private SupplementPropagation procedures = null;
  private SupplementPropagation triggers = null;

  public Propagator() {
  }

  public void init() {
    LOG.info(this.moduleName + " starting propagation. " + new Date());

    if (disableDbPropagation) {
      LOG.info("Changes propagation is disabled.");
      return;
    }
    // if (!simulationMode && !propagatorLock.lock()) {
    // return;
    // }
    while (!checkPreconditions()) {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
    try {
      collectStructureAndPropagate();
    } finally {
      if (!simulationMode) {
        propagatorLock.unlock();
        finished = true;
      }
    }
    LOG.info(this.moduleName + " finishing propagation." + new Date());
  }

  protected boolean checkPreconditions() {
    for (PropagatorExecutionPrecondition precondition : getPreconditions()) {
      if (!precondition.canExecute()) {
        return false;
      }
    }

    return true;
  }

  protected abstract List<PropagatorExecutionPrecondition> getPreconditions();

  private void collectStructureAndPropagate() {
    try {
      initEnvironment();
      updatePropagatorTables();
      propagateStructure();
      if (reloadFired) {
        reloadFired = false;
        collectStructureAndPropagate();
        return;
      }
      propagateSupplement();
    } finally {
      revalidateDatabase();
    }
  }

  private void updatePropagatorTables() {
    if (simulationMode) {
      return;
    }
    propagationDAO.updateVersion();
  }

  // Main propagation block
  private void propagateStructure() {
    changes = getFileHandler().findChanges(changesDir);

    StringBuilder sb = new StringBuilder("The following change directories were read: \n");
    for (Entry<String, File> entry : changes.entrySet()) {
      sb.append(entry.toString() + "\n");
    }
    LOG.info(sb);

    Set<String> propagatedChanges = new HashSet<String>();
    propagatedChanges = propagationDAO.getPropagatedChages(moduleName);
    for (File change : changes.values()) {
      propagateChange(change, propagatedChanges);
      if (reloadFired) {
        break;
      }
    }
  }

  private void propagateChange(File change, Set<String> propagatedChanges) {
    propagate(change, GO_SQL_PATTERN, propagatedChanges);
    if (useTestData) {
      propagate(change, TEST_SQL_PATTERN, propagatedChanges);
    }
  }

  private void propagate(File changeDirectory, String fileSearchRegexp, Set<String> propagatedChanges) {
    File[] files = getFileHandler().findSqls(changeDirectory, fileSearchRegexp);
    Arrays.sort(files);
    for (File file : files) {
      LOG.info("\n*********************************************\nProcessing file <" + file.getName()
          + ">\n*********************************************");
      String id =
          PropagationUtils.getPathLastFolder(changeDirectory.getName()) + PropagationUtils.removeExtension(file);
      if (!propagatedChanges.contains(id)) {
        propagateSingleEntry(id, file);
        if (reloadFired) {
          break;
        }
      }
    }
  }

  private void propagateSingleEntry(final String id, final File file) {
    if (LOG.isInfoEnabled()) {
      LOG.info("Processing entry with id = " + id);
    }
    try {
      historyLogger.setCurrentDbChange(id);
      if (!simulationMode) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
            try {
              reloadFired = sqlExecutor.executeFile(id, file);
            } catch (Exception e) {
              transactionStatus.setRollbackOnly();
              throw new RuntimeException(e);
            }
          }
        });
      } else {
        reloadFired = sqlExecutor.executeFile(id, file);
      }
    } finally {
      historyLogger.setCurrentDbChange(null);
    }
  }

  // Packages, views, and triggers propagation
  private void propagateSupplement() {
    populateSupplementPropagatorsWithHashes();

    try {
      historyLogger.startCaching();
      if (!simulationMode) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
          @Override
          protected void doInTransactionWithoutResult(TransactionStatus arg0) {
            packageHeaders.propagate();
            functions.propagate();
            procedures.propagate();
            views.propagate();
            packages.propagate();
            triggers.propagate();
          }
        });
      } else {
        packageHeaders.propagate();
        functions.propagate();
        procedures.propagate();
        views.propagate();
        packages.propagate();
        triggers.propagate();
      }
    } finally {
      historyLogger.logCached(moduleName);
    }
  }

  private void populateSupplementPropagatorsWithHashes() {
    List<PropagationObject> propagationObjects = new ArrayList<PropagationObject>();
    if (!simulationMode) {
      propagationObjects = propagationDAO.getPropagationObjects(moduleName);
    }
    for (PropagationObject propagationObject : propagationObjects) {
      switch (propagationObject.getObjectType()) {
      case FUNCTION:
        functions.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      case PROCEDURE:
        procedures.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      case VIEW:
        views.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      case PACKAGE_HEADER:
        packageHeaders.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      case PACKAGE:
        packages.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      case TRIGGER:
        triggers.addHash(propagationObject.getMd5Hash(), propagationObject);
        break;
      default:
        break;
      }
    }
  }

  protected void revalidateDatabase() {
    if (simulationMode) {
      return;
    }
    historyLogger.setCurrentDbChange(null);
    if (revalidationStatement != null) {
      try {
        sqlExecutor.executeSql(revalidationStatement);
      } catch (BadSqlGrammarException e) {
        LOG.warn("Could not revalidate objects. Statement \"" + revalidationStatement
            + "\" causes Bad Sql Grammar error.");
        if (LOG.isDebugEnabled()) {
          LOG.debug(e.getStackTrace());
        }
      }
    }
  }

  // Property setters
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Accepts the collection of change directories. In every directory it will look for an order file with an order key,
   * if it does not exist the directory name will be taken as the order key.
   */
  public void setChanges(URL changesDir) throws IOException {
    this.changesDir = new File(changesDir.getPath());
  }

  public void setFunctionsDir(URL functionsDir) throws IOException {
    this.functionsDirectory = new File(functionsDir.getPath());
  }

  public void setProceduresDir(URL proceduresDir) throws IOException {
    this.proceduresDirectory = new File(proceduresDir.getPath());
  }

  public void setPackageDir(URL packageDir) throws IOException {
    this.packagesDirectory = new File(packageDir.getPath());
  }

  public void setHeadersDir(URL headersDir) throws IOException {
    this.packagesHeaderDirectory = new File(headersDir.getPath());
  }

  public void setViewDir(URL viewDir) throws IOException {
    this.viewsDirectory = new File(viewDir.getPath());
  }

  public void setTriggerDir(URL triggerDir) throws IOException {
    this.triggersDirectory = new File(triggerDir.getPath());
  }

  public void setDisableDbPropagation(final boolean disableDbPropagation) {
    this.disableDbPropagation = disableDbPropagation;
  }

  public void setUseTestData(final boolean developmentMode) {
    this.useTestData = developmentMode;
  }

  public void setEnableAutomaticTransformation(boolean enableAutomaticTransformation) {
    this.enableAutomaticTransformation = enableAutomaticTransformation;
  }

  public void setTransactionManager(final PlatformTransactionManager transactionManager) {
    Validate.notNull(transactionManager);
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
  }

  /**
   * This statement will be called in the end of the propagation.
   */
  public void setRevalidationStatement(String revalidationStatement) {
    this.revalidationStatement = revalidationStatement;
  }

  public void setSimulationMode(boolean simulationMode) {
    this.simulationMode = simulationMode;
  }

  public void setChangeHistoryTable(String changeHistoryTable) {
    this.changeHistoryTable = changeHistoryTable;
  }

  public void setLockTable(String lockTable) {
    this.lockTable = lockTable;
  }

  public void setHistoryLogTable(String historyLogTable) {
    this.historyLogTable = historyLogTable;
  }

  public void setEnvironmentSql(String environmentSql) {
    this.environmentSql = environmentSql;
  }

  public void setPropagationObjectsTable(String propagationObjectsTable) {
    this.propagationObjectsTable = propagationObjectsTable;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public void setVersionProvider(VersionProvider versionProvider) {
    this.versionProvider = versionProvider;
  }

  public void setDependsOn(String dependsOn) {
    this.dependsOn = Arrays.asList(StringUtils.split(dependsOn, ','));
  }

  public String getModuleName() {
    return moduleName;
  }
  
  public boolean isFinished() {
    return finished;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (!simulationMode) {
      Validate.notNull(jdbcTemplate);
    }
    Validate.notNull(moduleName);
    String databaseCode = "ORA";
    if (!simulationMode) {
      Connection connection = jdbcTemplate.getDataSource().getConnection();
      databaseName = connection.getMetaData().getDatabaseProductName();
      connection.close();
      databaseCode = PropagationUtils.getDatabaseCode(databaseName);
    }

    propagationDAO = new PropagationDAO();
    propagationDAO.setJdbcTemplate(jdbcTemplate);
    if (StringUtils.isNotEmpty(changeHistoryTable)) {
      propagationDAO.setChangeHistoryTable(changeHistoryTable);
    }
    if (StringUtils.isNotEmpty(lockTable)) {
      propagationDAO.setLockTable(lockTable);
    }
    if (StringUtils.isNotEmpty(propagationObjectsTable)) {
      propagationDAO.setPropagationObjectsTable(propagationObjectsTable);
    }
    if (StringUtils.isNotEmpty(historyLogTable)) {
      propagationDAO.setHistoryLogTable(historyLogTable);
    }
    propagationDAO.setDatabaseCode(databaseCode);

    propagatorLock = new PropagatorLock(propagationDAO);
    SQLExceptionHandler exceptionHandler = SQLExceptionFactory.newHandler(databaseCode);
    historyLogger =
        new DBHistoryLogger(transactionTemplate, propagationDAO, exceptionHandler, moduleName, simulationMode);

    sqlExecutor = new SQLPropagationTool();
    sqlExecutor.setJdbcTemplate(jdbcTemplate);
    sqlExecutor.setDatabaseCode(databaseCode);
    sqlExecutor.setModuleName(moduleName);
    if (enableAutomaticTransformation) {
      sqlExecutor.setTransformer(new StandardTransformer(databaseCode));
    }
    sqlExecutor.setSimulationMode(simulationMode);
    sqlExecutor.setHistoryLogger(historyLogger);
    sqlExecutor.setPropagationDAO(propagationDAO);

    logPropagatorProperties();

    // Prepare supplement files
    packageHeaders =
        new SupplementPropagation(canPropagateHeadersSeparately() ? packagesHeaderDirectory : packagesDirectory,
                                  ObjectType.PACKAGE_HEADER,
                                  moduleName,
                                  sqlExecutor,
                                  propagationDAO,
                                  getPackageHeaderRegexp());
    packageHeaders.setPropagatorFileHandler(getFileHandler());
    packageHeaders.setSimulationMode(simulationMode);
    packageHeaders.setVersionProvider(versionProvider);
    packageHeaders.collectPropagatedFiles();

    functions =
        new SupplementPropagation(functionsDirectory,
                                  ObjectType.FUNCTION,
                                  moduleName,
                                  sqlExecutor,
                                  propagationDAO,
                                  getFunctionRegexp());
    functions.setPropagatorFileHandler(getFileHandler());
    functions.setSimulationMode(simulationMode);
    functions.setVersionProvider(versionProvider);
    functions.collectPropagatedFiles();

    procedures =
        new SupplementPropagation(proceduresDirectory,
                                  ObjectType.PROCEDURE,
                                  moduleName,
                                  sqlExecutor,
                                  propagationDAO,
                                  getProcedureRegexp());
    procedures.setPropagatorFileHandler(getFileHandler());
    procedures.setSimulationMode(simulationMode);
    procedures.setVersionProvider(versionProvider);
    procedures.collectPropagatedFiles();

    packages =
        new SupplementPropagation(packagesDirectory,
                                  ObjectType.PACKAGE,
                                  moduleName,
                                  sqlExecutor,
                                  propagationDAO,
                                  getPackageRegexp());
    packages.setPropagatorFileHandler(getFileHandler());
    packages.setSimulationMode(simulationMode);
    packages.setVersionProvider(versionProvider);
    packages.collectPropagatedFiles();

    views = new ViewPropagation(viewsDirectory, moduleName, sqlExecutor, propagationDAO, getViewRegexp());
    views.setPropagatorFileHandler(getFileHandler());
    views.setSimulationMode(simulationMode);
    views.setVersionProvider(versionProvider);
    views.collectPropagatedFiles();

    triggers =
        new SupplementPropagation(triggersDirectory,
                                  ObjectType.TRIGGER,
                                  moduleName,
                                  sqlExecutor,
                                  propagationDAO,
                                  getTriggerRegexp());
    triggers.setPropagatorFileHandler(getFileHandler());
    triggers.setSimulationMode(simulationMode);
    triggers.setVersionProvider(versionProvider);
    triggers.collectPropagatedFiles();
  }

  private void initEnvironment() {
    if (StringUtils.isNotEmpty(environmentSql)) {
      try {
        environmentCode = jdbcTemplate.queryForObject(environmentSql, null, String.class);
      } catch (Exception e) {
        environmentCode = "UNKNOWN";
      }
    }
    if (sqlExecutor != null) {
      sqlExecutor.setEnvironmentCode(environmentCode);
    }
  }

  private void logPropagatorProperties() {
    LOG.info("\n\n====================== PROPAGATOR PROPERTIES =================== ");
    LOG.info("Propagator enabled:          " + !disableDbPropagation);
    LOG.info("Simulation mode:             " + simulationMode);
    LOG.info("Test data used:              " + useTestData);
    LOG.info("Autotransformations enabled: " + enableAutomaticTransformation);
    LOG.info("Database name:               " + databaseName);
    LOG.info("Database code:               " + sqlExecutor.getDatabaseCode());
    LOG.info("Module name:                 " + moduleName);
    LOG.info("Lock table:                  " + lockTable);
    LOG.info("Change history table:        " + changeHistoryTable);
    LOG.info("Propagation objects table:   " + propagationObjectsTable);
    LOG.info("Environment code:            " + environmentCode);
    LOG.info("Environament SQL:            " + environmentSql);
    LOG.info("Revalidation statement:      " + revalidationStatement);
    LOG.info("Module version:              " + (versionProvider != null ? versionProvider.getVersion() : ""));
    Connection connection = null;
    try {
      if (!simulationMode) {
        connection = jdbcTemplate.getDataSource().getConnection();
        LOG.info("User name:                   " + connection.getMetaData().getUserName());
      }
    } catch (SQLException e) {
      LOG.info("Connection info is not accessible");
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          LOG.debug("Closing connection failed.");
        }
      }
    }
    LOG.info("\n=================================================================\n");
    // LOG.info("Revalidation statement : " + propagator.getJdbcTemplate().getDataSource().getConnection().get);
  }

  protected abstract PropagatorFileHandler getFileHandler();

  protected abstract boolean canPropagateHeadersSeparately();

  protected abstract String getPackageHeaderRegexp();

  protected abstract String getPackageRegexp();

  protected abstract String getViewRegexp();

  protected abstract String getTriggerRegexp();

  protected abstract String getFunctionRegexp();

  protected abstract String getProcedureRegexp();
}
