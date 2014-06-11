package eu.databata.web.rest;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.engine.model.PropagationObject;
import eu.databata.web.util.JsonUtil;
import java.util.List;
import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BataConsoleResource {

  @Resource
  private PropagationDAO propagationDAO;

  @Path("objects")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getObjects() {
    List<PropagationObject> propagationObjects = propagationDAO.getPropagationObjects();
    return JsonUtil.toJson(propagationObjects);
  }
  
//  history: {
//    method: 'GET',
//    url: '/databata/api/history',
//    isArray: true
//  },
//  logs: {
//    method: 'GET',
//    url: '/databata/api/logs',
//    isArray: true
//  },

}
