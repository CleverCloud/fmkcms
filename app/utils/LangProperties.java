package utils;

import java.util.Locale;
import java.util.Properties;

/**
 *
 * @author waxzce
 */
public class LangProperties extends Properties {

   public String getProperty(String key, Locale locale) {
      if (this.containsKey(key + "." + locale.toString())) {
         return this.getProperty(key + "." + locale.toString());
      }
      if (this.containsKey(key + "." + locale.getLanguage())) {
         return this.getProperty(key + "." + locale.getLanguage());
      }
      return this.getProperty(key);
   }
}
