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

/**
 * Dummy lock that does nothing. To be used simulation mode.
 * @author Tanel Käär (Tanel.Kaar@nortal.com)
 */
public class DummyPropagatorLock extends PropagatorLock {

  public DummyPropagatorLock(PropagationDAO propagationDAO) {
    super(propagationDAO);
  }

  @Override
  public boolean lock() {
    return true;
  }
  
  @Override
  public void unlock() {
    // do nothing
  }
}
