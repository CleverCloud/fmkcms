/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.i18n;

import com.clevercloud.utils.locale.LocaleSet;
import controllers.I18nController;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 * @author waxzce
 */
public class TranslatableManager<T extends Translatable, R extends TranslatableRef<T, R>> {

   private Class t;
   private Class tr;

   public TranslatableManager(Class<T> t, Class<R> tr) {
      this.t = t;
      this.tr = tr;
   }

   public T getByMongodId(String id) {
      return (T) MongoEntity.getDs().find(t, "id", new ObjectId(id)).get();
   }

   public R getRefByMongodId(String id) {
      return (R) MongoEntity.getDs().find(tr, "id", new ObjectId(id)).get();
   }

   public List<T> getPage(Integer pageNumber, Integer pageItemsNumber) {
      if (pageNumber == null) {
         pageNumber = 0;
      }
      return MongoEntity.getDs().find(t).offset(pageItemsNumber * pageNumber).limit(pageItemsNumber).asList();
   }

   public List<R> getRefPage(Integer pageNumber, Integer pageItemsNumber) {
      if (pageNumber == null) {
         pageNumber = 0;
      }
      return MongoEntity.getDs().find(tr).offset(pageItemsNumber * pageNumber).limit(pageItemsNumber).asList();
   }

   public Long number() {
      return MongoEntity.getDs().getCount(t);
   }

   public Long refNumber() {
      return MongoEntity.getDs().getCount(tr);
   }

   public T getBestForLocales(R ref, List<Locale> l) {
      Map<Locale, T> m = ref.getAvailableLocalesMap();
      LocaleSet ls = new LocaleSet();
      ls.addAll(m.keySet());
      return m.get(ls.getTheBestLocale(l));
   }

   public T getBestForLocales(R ref) {
      return getBestForLocales(ref, I18nController.getLanguages());
   }
}
