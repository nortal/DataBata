/**
 * Copyright 2014 Nortal AS Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package eu.databata.engine.spring;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Maksim Boiko (max.boiko@gmail.com)
 */
public class PropagatorSpringExecutor {
  private static final Logger LOG = Logger.getLogger(PropagatorSpringExecutor.class);

  public static void main(String[] args) {
    if (args.length == 0) {
      new ClassPathXmlApplicationContext("WEB-INF/propagator-beans.xml");
    } else if (args.length == 1) {
      new ClassPathXmlApplicationContext(args[0]);
    }

    LOG.info("\n\nSUCCESS: Propagation finished with no errors.");
    System.out.println("Conf " + args.length + "; " + (args.length > 0 ? args[0] : "sss"));
  }
}