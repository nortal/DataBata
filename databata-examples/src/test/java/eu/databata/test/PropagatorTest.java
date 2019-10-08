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
package eu.databata.test;

import eu.databata.Propagator;
import eu.databata.engine.dao.PropagationDAO;
import eu.databata.engine.model.PropagationObject;
import eu.databata.engine.model.PropagationObject.ObjectType;
import eu.databata.engine.spring.PropagatorSpringInstance;
import eu.databata.engine.version.VersionProvider;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang.ObjectUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * To run PropagatorTest tests run ///// 'gradle test -PrunPropagatorTest=true' ///// 
 * Note: * all tests should run in ascending alphabetical order to success 
 *       * all the tests names should start with 'test' string
 * 
 * @author Julia Vesnuhhova
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:propagator-test-beans.xml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PropagatorTest extends AbstractJUnit4SpringContextTests {
  private static final String MODULE_NAME = "DATABATA_JUNIT_TEST";
  private static Integer testsQuantity;
  private static Integer testNumber;

  @Resource
  private PropagatorSpringInstance propagator;
  @Resource
  private VersionProvider versionProvider;
  private PropagationDAO propagationDAO;

  private String viewSqlCurrentMd5Hash = null;

  @BeforeClass
  public static void setUpOnce() {
    testNumber = 0;
    testsQuantity = 0;
    Method[] methods = PropagatorTest.class.getMethods();
    for (Method method : methods) {
      if (method.getName().startsWith("test")) {
        testsQuantity++;
      }
    }
  }

  @Before
  public void setUp() {
    testNumber++;
    propagationDAO = getPropagationDAO();
  }

  @After
  public void cleanUp() {
    propagationDAO.deleteLock();
    if (ObjectUtils.equals(testNumber, testsQuantity)) {
      propagationDAO.removeAllPropagationObjectEntries(MODULE_NAME);
    }
  }

  @AfterClass
  public static void cleanUpAll() {
    testNumber = null;
    testsQuantity = null;
  }

  private PropagationDAO getPropagationDAO() {
    PropagationDAO propagationDAO = new PropagationDAO();
    propagationDAO.setJdbcTemplate((JdbcTemplate) applicationContext.getBean("jdbcTemplate"));
    propagationDAO.setChangeHistoryTable(Propagator.DEFAULT_PROPAGATOR_HISTORY_TABLE);
    propagationDAO.setLockTable(Propagator.DEFAULT_PROPAGATOR_LOCK_TABLE);
    propagationDAO.setPropagationObjectsTable(Propagator.DEFAULT_PROPAGATOR_OBJECT_TABLE);
    propagationDAO.setHistoryLogTable(Propagator.DEFAULT_PROPAGATOR_SQL_LOG_TABLE);
    propagationDAO.setDatabaseCode(Propagator.DATABASE_CODE_ORACLE);
    return propagationDAO;
  }

  @Test
  public void testA_test_propagation() {
    // Clear old test data if exists
    propagationDAO.removeAllPropagationObjectEntries(MODULE_NAME);

    List<PropagationObject> propagationObjects = propagationDAO.getPropagationObjects(MODULE_NAME);
    Assert.assertTrue(propagationObjects.isEmpty());

    // Run propagator
    propagator.init();

    propagationObjects = propagationDAO.getPropagationObjects(MODULE_NAME);
    Assert.assertEquals(4, propagationObjects.size());

    String objVersion = versionProvider.getVersion();
    int validPropagatedObjects = 0;
    for (PropagationObject obj : propagationObjects) {
      switch (obj.getObjectType()) {
      case PACKAGE:
        Assert.assertEquals(obj.getObjectName(), "pkg_test_junit");
        Assert.assertEquals(obj.getVersion(), objVersion);
        validPropagatedObjects++;
        break;
      case PACKAGE_HEADER:
        Assert.assertEquals(obj.getObjectName(), "pkg_test_junit_header");
        Assert.assertEquals(obj.getVersion(), objVersion);
        validPropagatedObjects++;
        break;
      case TRIGGER:
        Assert.assertEquals(obj.getObjectName(), "thi_test_junit_tr");
        Assert.assertEquals(obj.getVersion(), objVersion);
        validPropagatedObjects++;
        break;
      case VIEW:
        Assert.assertEquals(obj.getObjectName(), "vsq_test_junit");
        Assert.assertEquals(obj.getVersion(), objVersion);
        viewSqlCurrentMd5Hash = obj.getMd5Hash();
        validPropagatedObjects++;
        break;
      default:
        break;
      }
    }
    // Check that all objects are correctly propagated
    Assert.assertEquals(4, validPropagatedObjects);

    //// Change view sql and application data for next test (test2)
    String newContent =
        "create or replace view vsq_test_junit as select 'test2' AS test_name, tjr.id AS id from test_junit_tr tjr;";
    String oldVersion = "4.27.1.1";
    String newVersion = "4.27.1.2";
    changeViewSqlDataForNextTest(newContent, oldVersion, newVersion);
  }

  @Test
  public void testB_test_version_and_md5hash_update() {
    // Propagate
    propagator.init();

    List<PropagationObject> propagationObjects = propagationDAO.getPropagationObjects(MODULE_NAME);
    Assert.assertEquals(4, propagationObjects.size());

    for (PropagationObject obj : propagationObjects) {
      if (obj.getObjectType() == ObjectType.VIEW) {
        // Check that view sql MD5 HASH was updated
        Assert.assertNotEquals(viewSqlCurrentMd5Hash, obj.getMd5Hash());
        viewSqlCurrentMd5Hash = obj.getMd5Hash();

        // Check that view sql version was updated
        Assert.assertEquals("4.27.1.2", obj.getVersion());
        break;
      }
    }

    //// Change view sql and application data for next test (test3)
    String newContent =
        "create or replace view vsq_test_junit as select 'test3' AS test_name, tjr.id AS id from test_junit_tr tjr;";
    String oldVersion = "4.27.1.2";
    String newVersion = "feature-version.1";
    changeViewSqlDataForNextTest(newContent, oldVersion, newVersion);
  }

  @Test
  public void testC_test_md5hash_update_but_version_remains() {
    // Propagate
    propagator.init();

    List<PropagationObject> propagationObjects = propagationDAO.getPropagationObjects(MODULE_NAME);
    Assert.assertEquals(4, propagationObjects.size());

    for (PropagationObject obj : propagationObjects) {
      if (obj.getObjectType() == ObjectType.VIEW) {
        // Check that view sql MD5 HASH was updated
        Assert.assertNotEquals(viewSqlCurrentMd5Hash, obj.getMd5Hash());

        // Check that view sql version was NOT updated, as it don't match default pattern ^\d+\.\d+\.\d+\.\d+
        Assert.assertEquals("4.27.1.2", obj.getVersion());
        break;
      }
    }
  }

  private void changeViewSqlDataForNextTest(String newContent, String oldVersion, String newVersion) {
    // Change content of the view sql
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    File viewSqlFile = new File(contextClassLoader.getResource("WEB-INF/db/views/vsq_test_junit.sql").getPath());
    changeViewSqlContent(viewSqlFile, newContent);

    // Change version
    File testBeansFile = new File(contextClassLoader.getResource("propagator-test-beans.xml").getPath());
    changeVersion(testBeansFile, oldVersion, newVersion);
  }

  private void changeVersion(File testBeansFile, String oldVersion, String newVersion) {
    BufferedReader reader = null;
    BufferedWriter bw = null;
    String oldContent = "";
    try {
      reader = new BufferedReader(new FileReader(testBeansFile));
      String line = reader.readLine();
      while (line != null) {
        oldContent = oldContent + line + System.lineSeparator();
        line = reader.readLine();
      }
      String newContent = oldContent.replaceAll(oldVersion, newVersion);

      bw = new BufferedWriter(new FileWriter(testBeansFile));
      bw.write(newContent);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        reader.close();
        bw.flush();
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void changeViewSqlContent(File viewSqlFile, String newContent) {
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(viewSqlFile));
      bw.write(newContent);
      bw.flush();
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
