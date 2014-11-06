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
package eu.databata.engine.util;

import eu.databata.engine.dao.PropagationDAO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

/**
 * Contains logic for locking and unlocking propagator execution.
 * 
 * @author Maksim Boiko <mailto:max.boiko@gmail.com>
 *
 */
public class PropagatorLock {
  private static final Logger log = Logger.getLogger(PropagatorLock.class);
  private PropagationDAO propagationDAO;
  
  public PropagatorLock(PropagationDAO propagationDAO) {
    this.propagationDAO = propagationDAO;
  }
  
  public boolean lock() {
    log.info("Checking lock state...");
    String token = null;
    try {
      token = propagationDAO.getLockToken();
    } catch (BadSqlGrammarException e) {
      log.info("No lock table found. Creating.");
      propagationDAO.createLockTable();
    } catch (EmptyResultDataAccessException e) {
      log.info("Inserting empty record for lock table.");
      propagationDAO.insertLockRecord();
    }
    int rowsChanged = 0;
    String myToken = null;
//    do {
//      if (token != null) {
//        log.info("Database is locked by another propagator. Waiting.");
//        waitPropagation();
//      }
      myToken = generateToken();
      rowsChanged = propagationDAO.updateLock(myToken);
      token = propagationDAO.getLockToken();
      boolean lockAquired =  myToken.equals(token) && rowsChanged == 1;
      if(!lockAquired) {
        log.info("Still locked.");
      }
      
      return lockAquired;
//    return true;
  }
  
  public void unlock() {
    propagationDAO.deleteLock();
  }
  
//  private void waitPropagation() {
//    String token = null;
//    do {
//      try {
//        Thread.sleep(5000);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      log.info("Checking lock state...");
//      token = propagationDAO.getLockToken();
//      if (token != null) {
//        log.info("Still locked.");
//      }
//    } while (token != null);
//    log.info("Database is unlocked!");
//  }

  private String generateToken() {
    return StringUtils.abbreviate("" + Math.random(), 200);
  }
}
