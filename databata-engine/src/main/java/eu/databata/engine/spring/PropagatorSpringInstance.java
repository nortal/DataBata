package eu.databata.engine.spring;

import eu.databata.Propagator;
import eu.databata.PropagatorFileHandler;

public class PropagatorSpringInstance extends Propagator {

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
    return ".*(_header)\\.sql";
  }

  @Override
  protected String getPackageRegexp() {
    return ".*(?<!(_header))\\.sql";
  }

  @Override
  protected String getViewRegexp() {
    return ".*\\.sql";
  }

  @Override
  protected String getTriggerRegexp() {
    return ".*\\.sql";
  }
  
}
