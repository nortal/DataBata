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
