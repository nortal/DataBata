package eu.databata.engine.osgi;

import org.apache.log4j.Logger;

import eu.databata.Propagator;
import eu.databata.PropagatorExecutionPrecondition;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Precondition that checks whether dependant modules in OSGI container are finished their propagation.
 * 
 * @author Maksim Boiko
 */
public class PropagatorOsgiExecutionPrecondition implements PropagatorExecutionPrecondition {
  private final static Logger LOG = Logger.getLogger(PropagatorOsgiExecutionPrecondition.class);
  
  private final BundleContext bundleContext;
  private final List<String> dependsOn;

  public PropagatorOsgiExecutionPrecondition(BundleContext bundleContext, List<String> dependsOn) {
    this.bundleContext = bundleContext;
    this.dependsOn = dependsOn;
  }

  @Override
  public boolean canExecute() {
    Map<String, Propagator> propagators = getPropagators();
    for(String dependantModule : dependsOn) {
      Propagator dependantPropagator = propagators.get(dependantModule);
      if(dependantPropagator == null) {
        LOG.info("Dependant module " + dependantModule + " is not installed.");
        return false;
      }
      
      if(!dependantPropagator.isFinished()) {
        LOG.info("Dependant module " + dependantModule + " is not finished propagation.");
        return false;
      }
    }
    return true;
  }

  private  Map<String, Propagator> getPropagators() {
    try {
      Collection<ServiceReference<Propagator>> references = bundleContext.getServiceReferences(Propagator.class, null);
      if (references == null) {
        throw new RuntimeException(Propagator.class.getName() + " not found");
      }
      Map<String, Propagator> result = new HashMap<String, Propagator>();
      for (ServiceReference<Propagator> reference : references) {
        Propagator propagator = bundleContext.getService(reference);
        result.put(propagator.getModuleName(), propagator);
      }
      return result;
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
