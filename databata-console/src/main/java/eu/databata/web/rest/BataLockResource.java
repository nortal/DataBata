package eu.databata.web.rest;

import javax.ws.rs.DELETE;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.web.model.DatabataPropsInfo;
import eu.databata.web.util.JsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Path("/lock")
@Component
public class BataLockResource {

  @Resource
  private PropagationDAO propagationDAO;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getLock() {
    return JsonUtil.toJson(propagationDAO.getLockInfo());
  }

  @DELETE
  public void resetLock() {
    propagationDAO.deleteLock();
  }
}
