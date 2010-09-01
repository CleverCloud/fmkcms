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
public abstract class MongoEntity<T> {

    @Id
    public ObjectId id;

    @Transient
    private Morphia morphia;

    protected Datastore getDs() {
        if (this.morphia == null) {
            this.morphia = new Morphia();
        }
        return this.morphia.createDatastore(Play.configuration.getProperty("fmkcms.db"));
    }

    public T save() {
        this.getDs().save(this);
        return (T) this.getDs().get(this);
    }
    public void delete() {
        this.getDs().delete(this);
    }

}
