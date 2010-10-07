package models;

import com.google.code.morphia.annotations.Entity;
import java.util.Set;
import java.util.TreeSet;
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

    public String getTagsAsString() {
        String tagsString = new String();
        if (this.tags == null)
            return tagsString;
        for (Tag tag : new TreeSet<Tag>(this.tags))
            tagsString += (tagsString.isEmpty() ? "" : ", ") + tag.toString();
        return tagsString;
    }

}
