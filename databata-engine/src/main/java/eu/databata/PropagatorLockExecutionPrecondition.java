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
