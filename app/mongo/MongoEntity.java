package mongo;

/**
 *
 * @author keruspe
 */
public interface MongoEntity<T> {

    public T save();
    public void delete();
    public T find();

}
