package eu.databata.engine.osgi;

import eu.databata.engine.spring.PropagatorSpringFileHandler;

import eu.databata.Propagator;
import eu.databata.PropagatorFileHandler;


public class PropagatorOsgiInstance extends Propagator {

  @Override
  protected PropagatorFileHandler getFileHandler() {
    return new PropagatorSpringFileHandler();
  }

  @Override
  protected boolean canPropagateHeadersSeparately() {
    return false;
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
}
