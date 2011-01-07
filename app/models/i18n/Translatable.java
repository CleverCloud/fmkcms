package models.i18n;

import com.google.code.morphia.annotations.Reference;
import java.util.Locale;
import mongo.MongoEntity;
import org.elasticsearch.common.Required;

/**
 *
 * @author keruspe
 */
public abstract class Translatable<T> extends MongoEntity {

   @Required
   public String urlId;

   @Required
   public Locale language;

   @Required
   @Reference
   public T reference;

   public Translatable() {}

   public T getCastedRef() {
      return (T) this.reference;
   }

}
