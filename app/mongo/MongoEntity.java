package mongo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;
import com.mongodb.Mongo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import play.Play;

/**
 * @author keruspe
 */
@SuppressWarnings("unchecked")
public abstract class MongoEntity implements Serializable {

   @Id
   public ObjectId id;
   @Transient
   private static Datastore datastore;

   /**
    * Get the Datastore used to store the data
    *
    * @return The Datastore
    */
   public static Datastore getDs() {
      if (MongoEntity.datastore == null) {
         try {
            Mongo mongo = new Mongo(Play.configuration.getProperty("fmkcms.db.host", "127.0.0.1"));
            MongoEntity.datastore = (new Morphia()).createDatastore(mongo, Play.configuration.getProperty("fmkcms.db", "fmkcms"));
         } catch (Exception e) {
            play.Logger.error(e, "");
         }
      }

      return MongoEntity.datastore;
   }

   /**
    * Save the Entity
    *
    * @return self
    */
   public <T extends MongoEntity> T save() {
      MongoEntity.getDs().save(this);
      MongoEntity.getDs().ensureIndexes(this.getClass());
      return (T) MongoEntity.getDs().get(this);
   }

   public <T extends MongoEntity> T create() {
      MongoEntity.getDs().save(this);
      return (T) MongoEntity.getDs().get(this.getClass(), this.id);
   }

   /**
    * Refresh the Entity (fill missing field with values from BDD)
    *
    * @return self, refreshed
    */
   public <T extends MongoEntity> T refresh() {
      if (this.id == null) {
         return (T) this;
      } else {
         T entity = (T) MongoEntity.getDs().get(this.getClass(), this.id);
         for (Field field : this.getClass().getFields()) {
            try {
               if (field.get(this) != null) {
                  field.set(entity, field.get(this));
               } else {
                  field.set(this, field.get(entity));
               }
            } catch (IllegalArgumentException ex) {
               Logger.getLogger(MongoEntity.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
               Logger.getLogger(MongoEntity.class.getName()).log(Level.SEVERE, null, ex);
            }
         }

         return (T) this;
      }
   }

   /**
    * Delete the Entity
    */
   public void delete() {
      MongoEntity.getDs().delete(this);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final MongoEntity other = (MongoEntity) obj;
      if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
         return false;
      }
      return true;
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 89 * hash + (this.id != null ? this.id.hashCode() : 0);
      return hash;
   }
}
