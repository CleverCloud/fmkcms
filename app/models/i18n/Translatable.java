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
   public TranslatableRef<T> reference;

   public Translatable() {}

}
