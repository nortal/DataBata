package eu.databata.web.rest;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.web.util.JsonUtil;
import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

@Path("/")
@Component
public class BataConsoleResource {

  @Resource
  private PropagationDAO propagationDAO;

  @Path("objects")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getObjects() {
    return JsonUtil.toJson(propagationDAO.getPropagationObjects());
  }

  @Path("history")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getHistory() {
    return JsonUtil.toJson(propagationDAO.getHistory());
  }
  
  @Path("logs")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getLogs() {
    return JsonUtil.toJson(propagationDAO.getHistoryLog());
  }

}
