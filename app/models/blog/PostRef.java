package models.blog;

import com.google.code.morphia.annotations.Entity;
import java.util.Date;
import java.util.Set;
import models.Tag;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
@Entity
public class PostRef extends MongoEntity {

    public Date postedAt;
    public User author;
    public Set<Tag> tags;

    //
    // Accessing stuff
    //
    // TODO: Reimplement previous & next
    public PostRef previous() {
        //return PostRef.find("postedAt < ? order by postedAt desc", postedAt).first();
        return null;
    }

    public PostRef next() {
        //return PostRef.find("postedAt > ? order by postedAt asc", postedAt).first();
        return null;
    }
    
}
