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
import eu.databata.PropagatorExecutionPrecondition;
import eu.databata.PropagatorFileHandler;
import eu.databata.PropagatorLockExecutionPrecondition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.Resource;

public class PropagatorSpringInstance extends Propagator {

  
  public void setChanges(Resource changesDir) throws IOException {
    this.changesDir = changesDir.getFile();
  }
  
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
  
  @Override
  protected List<PropagatorExecutionPrecondition> getPreconditions() {
    List<PropagatorExecutionPrecondition> preconditions = new ArrayList<PropagatorExecutionPrecondition>();
    preconditions.add(new PropagatorLockExecutionPrecondition(propagatorLock));
    return preconditions;
  }

  public void setFunctionsDir(Resource functionsDir) throws IOException {
    this.functionsDirectory = functionsDir.getFile();
  }
  
  public void setProceduresDir(Resource proceduresDir) throws IOException {
    this.proceduresDirectory = proceduresDir.getFile();
  }
  
  public void setPackageDir(Resource packageDir) throws IOException {
    this.packagesDirectory = packageDir.getFile();
  }
  
  public void setHeadersDir(Resource headersDir) throws IOException {
    this.packagesHeaderDirectory = headersDir.getFile();
  }
  
  public void setViewDir(Resource viewDir) throws IOException {
    this.viewsDirectory = viewDir.getFile();
  }
  
  public void setTriggerDir(Resource triggerDir) throws IOException {
    this.triggersDirectory = triggerDir.getFile();
  }

}
