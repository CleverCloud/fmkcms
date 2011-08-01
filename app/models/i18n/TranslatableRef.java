package models.i18n;

import java.lang.Class;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mongo.MongoEntity;

/**
 * @author keruspe
 */
public abstract class TranslatableRef<T extends Translatable, R extends TranslatableRef> extends MongoEntity {

   /**
    * Get all available locales for this Translatable
    *
    * @return The list of Locale
    */
   public <T extends Translatable> List<Locale> getAvailableLocales() {
      ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();//getGenericSuperClass();
      Class c = (Class) Arrays.asList(pt.getActualTypeArguments()).get(0);
      List<T> items = (List<T>) MongoEntity.getDs().find(c, "reference", this).asList();
      List<Locale> locales = new ArrayList<Locale>();

      if (items != null && !items.isEmpty()) {
         for (T item : items) {
            locales.add(item.language);
         }
      }
      return locales;
   }

   public Map<Locale, T> getAvailableLocalesMap() {
      ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();//getGenericSuperClass();
      Class c = (Class) Arrays.asList(pt.getActualTypeArguments()).get(0);
      List<T> items = (List<T>) MongoEntity.getDs().find(c, "reference", this).asList();
      Map<Locale, T> locales = new HashMap<Locale, T>();

      if (items != null && !items.isEmpty()) {
         for (T item : items) {
            locales.put(item.language, item);
         }
      }
      return locales;
   }
}
