package mongo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;
import org.bson.types.ObjectId;
import play.Play;

/**
 *
 * @author keruspe
 */
@SuppressWarnings("unchecked")
public abstract class MongoEntity {

    @Id
    public ObjectId id;

    @Transient
    private static Datastore datastore;

    public static Datastore getDs() {
        if (MongoEntity.datastore == null)
            MongoEntity.datastore = (new Morphia()).createDatastore(Play.configuration.getProperty("fmkcms.db", "fmkcms"));
        return MongoEntity.datastore;
    }

    public <T extends MongoEntity> T save() {
        MongoEntity.getDs().save(this);
        return (T) MongoEntity.getDs().get(this);
    }

    public <T extends MongoEntity> T create() {
       Object iden = MongoEntity.getDs().save(this).getId();
       return (T) MongoEntity.getDs().get(this.getClass(), iden);
    }
    
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
