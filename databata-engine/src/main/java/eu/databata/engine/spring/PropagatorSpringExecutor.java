package eu.databata.engine.spring;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Maksim Boiko (max@webmedia.ee)
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
  }
}