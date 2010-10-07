package models.blog;

import models.user.User;
import com.google.code.morphia.annotations.Entity;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
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
    public PostRef previous() {
        return MongoEntity.getDs().find(PostRef.class, "postedAt <", this.postedAt).order("-postedAt").get();
    }

    public PostRef next() {
        return MongoEntity.getDs().find(PostRef.class, "postedAt >", this.postedAt).order("postedAt").get();
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
