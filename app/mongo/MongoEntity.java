package mongo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Transient;
import com.google.code.morphia.query.Query;
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
        if (morphia == null) {
            morphia = new Morphia();
        }
        return morphia.createDatastore(Play.configuration.getProperty("fmkcms.db"));
    }

    public <T extends MongoEntity> T save() {
        getDs().save(this);
        return (T) getDs().get(this);
    }
    public void delete() {
        getDs().delete(this);
    }

    public static <T extends MongoEntity> Query<T> find(Class childrenClass, String field, String value) {
        return getDs().find(childrenClass, field, value);
    }

}
