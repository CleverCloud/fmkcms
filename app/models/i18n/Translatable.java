package models.i18n;

import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mongo.MongoEntity;
import org.elasticsearch.common.Required;

/**
 *
 * @author keruspe
 */
public abstract class Translatable<T extends Translatable, R extends TranslatableRef<T, R>> extends MongoEntity {

   @Required
   public String urlId;
   @Required
   public Locale language;
   @Required
   @Reference
   public R reference;

   public Translatable() {
   }

   /**
    * Get all available locales for this Translatable
    * @return The list of Locale
    */
   public List<Locale> getAvailableLocales() {
      List<T> items = (List<T>) MongoEntity.getDs().find(this.getClass(), "reference", this.reference).asList();
      List<Locale> locales = new ArrayList<Locale>();

      if (items != null && !items.isEmpty()) {
         for (T item : items) {
            locales.add(item.language);
         }
      }
      return locales;
   }

   /**
    * Get all available locales for this Translatable + the corresponding Translatables
    * @return A Map<Locale, Translatable>
    */
   public Map<Locale, T> getAvailableLocalesAndTranslatables() {
      Map<Locale, T> returnMap = new HashMap<Locale, T>();
      List<T> items = (List<T>) MongoEntity.getDs().find(this.getClass(), "reference", this.reference).asList();

      if (items != null && !items.isEmpty()) {
         for (T item : items) {
            returnMap.put(item.language, item);
         }
      }
      return returnMap;
   }

   public Iterable<T> getWithSameRef() {
       return (this.reference == null) ? new LinkedList<T>(): (List<T>) MongoEntity.getDs().find(this.getClass(), "reference", this.reference).fetch();
   }
   
   
}
