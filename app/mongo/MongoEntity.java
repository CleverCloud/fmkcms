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
public abstract class MongoEntity {

    @Id
    public ObjectId id;

    @Transient
    private static Morphia morphia;

    public static Datastore getDs() {
        if (MongoEntity.morphia == null)
            MongoEntity.morphia = new Morphia();
        return MongoEntity.morphia.createDatastore(Play.configuration.getProperty("fmkcms.db"));
    }

    public <T extends MongoEntity> T save() {
        MongoEntity.getDs().save(this);
        return (T) MongoEntity.getDs().get(this);
    }
    
    public void delete() {
        MongoEntity.getDs().delete(this);
    }

}
