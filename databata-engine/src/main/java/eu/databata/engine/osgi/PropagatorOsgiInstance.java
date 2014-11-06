/**
 * Copyright 2014 Nortal AS Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package eu.databata.engine.osgi;

import eu.databata.Propagator;
import eu.databata.PropagatorExecutionPrecondition;
import eu.databata.PropagatorFileHandler;
import eu.databata.PropagatorLockExecutionPrecondition;
import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

public class PropagatorOsgiInstance extends Propagator implements BundleContextAware {

  private BundleContext bundleContext;

  @Override
  protected PropagatorFileHandler getFileHandler() {
    PropagatorOsgiFileHandler handler = new PropagatorOsgiFileHandler();
    handler.setBundle(bundleContext.getBundle());
    return handler;
  }

  @Override
  protected boolean canPropagateHeadersSeparately() {
    return true;
  }

  @Override
  protected String getPackageHeaderRegexp() {
    return "*.sql";
  }

  @Override
  protected String getPackageRegexp() {
    return "*.sql";
  }

  @Override
  protected String getViewRegexp() {
    return "*.sql";
  }

  @Override
  protected String getTriggerRegexp() {
    return "*.sql";
  }

  @Override
  protected String getFunctionRegexp() {
    return "*.sql";
  }

  @Override
  protected String getProcedureRegexp() {
    return "*.sql";
  }

  @Override
  public void setBundleContext(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }

  @Override
  protected List<PropagatorExecutionPrecondition> getPreconditions() {
    List<PropagatorExecutionPrecondition> preconditions = new ArrayList<PropagatorExecutionPrecondition>();
    preconditions.add(new PropagatorOsgiExecutionPrecondition(bundleContext, dependsOn));
    preconditions.add(new PropagatorLockExecutionPrecondition(propagatorLock));
    return preconditions;
  }
}
