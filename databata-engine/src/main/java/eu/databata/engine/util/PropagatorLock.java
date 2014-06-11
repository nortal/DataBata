package eu.databata.engine.util;

import eu.databata.engine.dao.PropagationDAO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;

/**
 * Contains logic for locking and unlocking propagator execution. Lock table have to be provided to constructor
 * for this purpose.
 * 
 * @author Maksim Boiko <mailto:max@webmedia.ee>
 *
 */
public class PropagatorLock {
  private static final Logger log = Logger.getLogger(PropagatorLock.class);
  private PropagationDAO propagationDAO;
  
  public PropagatorLock(PropagationDAO propagationDAO) {
    this.propagationDAO = propagationDAO;
  }
  
  public boolean lock() {
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
    do {
      if (token != null) {
        log.info("Database is locked by another propagator. Waiting.");
        waitPropagation();
      }
      myToken = generateToken();
      rowsChanged = propagationDAO.updateLock(myToken);
      token = propagationDAO.getLockToken();
    } while (!myToken.equals(token) || rowsChanged == 0);
    return true;
  }
  
  public void unlock() {
    propagationDAO.deleteLock();
  }
  
  private void waitPropagation() {
    String token = null;
    do {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      log.info("Checking lock state...");
      token = propagationDAO.getLockToken();
      if (token != null) {
        log.info("Still locked.");
      }
    } while (token != null);
    log.info("Database is unlocked!");
  }

  private String generateToken() {
    return StringUtils.abbreviate("" + Math.random(), 200);
  }
}
