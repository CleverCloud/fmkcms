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

    protected static Datastore getDs() {
        if (morphia == null) {
            morphia = new Morphia();
        }
        return morphia.createDatastore(Play.configuration.getProperty("fmkcms.db"));
    }

    public <T> T save() {
        getDs().save(this);
        return (T) getDs().get(this);
    }
    public void delete() {
        getDs().delete(this);
    }

    public static <T> T find(String field, String value) {
        T classGetter = null;
        return (T) getDs().find(classGetter.getClass(), field, value);
    }

}
