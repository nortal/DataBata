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

import eu.databata.engine.util.PropagatorLock;

/**
 * Precondition that checks whether propagation is locked by another instance or not.
 * 
 * @author Maksim Boiko
 */
public class PropagatorLockExecutionPrecondition implements PropagatorExecutionPrecondition {
  private final PropagatorLock propagatorLock;

  public PropagatorLockExecutionPrecondition(PropagatorLock propagatorLock) {
    this.propagatorLock = propagatorLock;
  }

  @Override
  public boolean canExecute() {
    return propagatorLock.lock();
  }

}
