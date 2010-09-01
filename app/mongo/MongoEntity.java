package mongo;

import org.bson.types.ObjectId;

/**
 *
 * @author keruspe
 */
public interface MongoEntity<T> {

    public T save();
    public void delete();
    public T findById(ObjectId id);

}
