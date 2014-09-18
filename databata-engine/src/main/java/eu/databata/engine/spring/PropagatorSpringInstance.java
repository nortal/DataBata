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
  protected String getFunctionRegexp() {
    return ".*\\.sql";
  }
  
  @Override
  protected String getProcedureRegexp() {
    return ".*\\.sql";
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
