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
