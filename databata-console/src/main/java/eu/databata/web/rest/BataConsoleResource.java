package eu.databata.web.rest;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import eu.databata.engine.dao.PropagationDAO;
import eu.databata.engine.model.PropagationObject;
import eu.databata.web.model.DatabataPropsInfo;
import eu.databata.web.util.JsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Path("/")
@Component
public class BataConsoleResource {
  private static final Logger LOG = Logger.getLogger(BataConsoleResource.class);

  @Resource
  private PropagationDAO propagationDAO;

  @Path("objects")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getObjects() {
    List<PropagationObject> propagationObjects = propagationDAO.getPropagationObjects();
    return JsonUtil.toJson(propagationObjects);
  }

  @Path("info")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getInfo() {
    Properties props = new Properties();
    InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream("databata.properties");
    if (propertiesStream == null) {
      return "{}";
    }
    try {
      props.load(propertiesStream);
      DatabataPropsInfo info = new DatabataPropsInfo();
      info.setUser(props.getProperty("databata.user"));
      populateInfo(info, props.getProperty("databata.connection-url"));
      return JsonUtil.toJson(info);
    } catch (IOException e) {
      LOG.error("Exception when reading properties", e);
      return "{}";
    } finally {
      try {
        propertiesStream.close();
      } catch (IOException e1) {
        // Fucking exception
      }
    }
  }

  private void populateInfo(DatabataPropsInfo info, String url) {
    System.out.println("url " + url);
    Matcher matcher = Pattern.compile(":@(.*):(\\d+)(:?/?)(.*)").matcher(url);
    if (!matcher.find()) {
      return;
    }
    info.setHost(matcher.group(1));
    info.setPort(matcher.group(2));
    if (":".equals(matcher.group(3))) {
      info.setSid(matcher.group(4));
    } else {
      info.setService(matcher.group(4));
    }
  }
}
