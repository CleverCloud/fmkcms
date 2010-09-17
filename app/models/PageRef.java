package models;

import com.google.code.morphia.annotations.Entity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends MongoEntity {

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    //
    // Accessing stuff
    //
    public static PageRef getPageRef(ObjectId id) {
        return MongoEntity.getDs().find(PageRef.class, "id", id).get();
    }

}
