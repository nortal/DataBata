package eu.databata;

/**
 * Used to manage execution of propagator. Propagator will wait in cycle before preconditions will be
 * fulfilled.
 * 
 * @author Maksim Boiko
 *
 */
public interface PropagatorExecutionPrecondition {
  boolean canExecute();
}
