package models.blog;

import models.user.User;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import models.Tag;
import models.i18n.TranslatableRef;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
@Entity
public class PostRef extends TranslatableRef<Post, PostRef> {

    @Reference
    public User author;

    public Date postedAt;

    @Reference
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

    public static List<PostRef> findTaggedWith(Tag ... tags) {
        List<PostRef> postRefs = new ArrayList<PostRef>();
        for (Tag tag : tags)
            postRefs.addAll(MongoEntity.getDs().find(PostRef.class).field("tags").hasThisElement(tag).asList());
        return postRefs;
    }

}
