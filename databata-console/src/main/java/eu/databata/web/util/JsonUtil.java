package eu.databata.web.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 
 * @author Maksim Boiko
 *
 */
public class JsonUtil {
  private static Gson GSON = null;
  
  static {
    GsonBuilder builder = new GsonBuilder();
    builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    GSON = builder.create();
  }

  public static String toJson(Object toSerialize) {
    return GSON.toJson(toSerialize);
  }

  public static <T> T fromJson(String toDeserialize, Class<T> clazz) {
    return GSON.fromJson(toDeserialize, clazz);
  }

}
