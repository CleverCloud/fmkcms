package models.blog;

import models.user.User;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.persistence.Lob;
import models.Tag;
import models.i18n.Translatable;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
@Entity
public class Post extends Translatable<Post, PostRef> {

    public Date postedAt;
    
    @Required
    public String title;

    @Lob
    @Required
    public String content;

    @Reference
    @Required
    public User author;

    //
    // Constructor
    //
    public Post() {}

    /* Make Play! views happy ... */
    public static Post call(Post other) {
        if (other == null)
            return null;
        Post post = new Post();
        post.urlId = other.urlId;
        post.title = other.title;
        post.content = other.content;
        post.language = other.language;
        post.reference = other.reference;
        post.postedAt = other.postedAt;
        post.author = other.author;
        return post;
    }

    //
    // Tags handling
    //
    public Post tagItWith(String name) {
        if (name != null && !name.isEmpty()) {
            this.reference.tags.add(Tag.findOrCreateByName(name));
            this.reference.save();
        }
        return this;
    }

    //
    // Accessing stuff
    //
    public static Post getPostByLocale(String urlId, Locale language) {
        Post post = Post.getPostByUrlId(urlId);
        return (post == null) ? null : MongoEntity.getDs().find(Post.class, "reference", post.reference).filter("language =", language).get();
    }

    public static List<Post> getPostsByPostRef(PostRef reference) {
        return MongoEntity.getDs().find(Post.class, "reference", reference).asList();
    }

    public static List<Post> getPostsByUrlId(String urlId) {
        Post post = Post.getPostByUrlId(urlId);
        return (post == null) ? new ArrayList<Post>() : MongoEntity.getDs().find(Post.class, "reference", post.reference).asList();
    }

    public static Post getPostByUrlId(String urlId) {
        return MongoEntity.getDs().find(Post.class, "urlId", urlId).get();
    }

    public static List<Post> getLatestPostsByLocale(Locale locale, Integer number, Integer page) {
        if (number == null)
            number = 10;
        if (page == null)
            page = 1;
        List<Post> posts = MongoEntity.getDs().find(Post.class, "language", locale).order("-postedAt").offset((page - 1) * number).limit(number).asList();
        return (posts == null) ? new ArrayList<Post>() : posts;
    }

    public static Object getFirstPostByPostRef(PostRef reference) {
        return MongoEntity.getDs().find(Post.class, "reference", reference).get();
    }

}
