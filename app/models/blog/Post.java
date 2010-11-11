package models.blog;

import models.user.User;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import javax.persistence.Lob;
import models.Tag;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
@Entity
public class Post extends MongoEntity {

    public Date postedAt;

    @Required
    public String title;

    @Lob
    @Required
    public String content;

    @Reference
    @Required
    public PostRef postReference;

    @Required
    public Locale language;

    @Reference
    @Required
    public User author;

    public List<Comment> comments;

    //
    // Constructor
    //
    public Post() {}

    /* Make Play! views happy ... */
    public static Post call(Post other) {
        if (other == null)
            return null;
        Post post = new Post();
        post.title = other.title;
        post.content = other.content;
        post.language = other.language;
        post.postReference = other.postReference;
        post.postedAt = other.postedAt;
        post.author = other.author;
        post.comments = other.comments;
        return post;
    }

    //
    // Comments handling
    //
    public Post addComment(Comment comment) {
        if (this.comments == null)
            this.comments = new ArrayList<Comment>();
        this.comments.add(comment);
        return this.save();
    }

    public Post removeComment(String userName, String content, Boolean removeAll) {
        // We want to get through it the reverse way to delete most recent comment first (duplicates)
        ListIterator<Comment> iterator = this.comments.listIterator(this.comments.size());
        Comment current = null;
        while (iterator.hasPrevious()) {
            current = iterator.previous();
            // Continue if it's not the one we're looking for
            if (!(current.user.userName.equalsIgnoreCase(userName) && current.content.equalsIgnoreCase(content)))
                continue;
            iterator.remove();
            current.delete();

            // Quit if we only want to remove one occurence of the comment
            if (!removeAll)
                break;
        }

        return this.save();
    }

    public Post removeComment(String userName, String content) {
        return this.removeComment(userName, content, false);
    }

    public List<Comment> getComments(String userName) {
        List<Comment> returnList = new ArrayList<Comment>();
        for (Comment current : this.comments) {
            if (current.user.userName.equalsIgnoreCase(userName))
                returnList.add(current);
        }
        return returnList;
    }

    //
    // Tags handling
    //
    public Post tagItWith(String name) {
        if (name != null && !name.isEmpty()) {
            this.postReference.tags.add(Tag.findOrCreateByName(name));
            this.postReference.save();
        }
        return this;
    }

    public static List<Post> findTaggedWith(String ... tags) {
        // TODO: waxzce, gogo elastic search !
        return null;
    }

    //
    // Accessing stuff
    //
    public static Post getPostByLocale(String title, Locale language) {
        Post post = Post.getPostByTitle(title);
        return (post == null) ? null : MongoEntity.getDs().find(Post.class, "postReference", post.postReference).filter("language =", language).get();
    }

    public static List<Post> getPostsByPostRef(PostRef postReference) {
        return MongoEntity.getDs().find(Post.class, "postReference", postReference).asList();
    }

    public static List<Post> getPostsByTitle(String title) {
        Post post = Post.getPostByTitle(title);
        return (post == null) ? new ArrayList<Post>() : MongoEntity.getDs().find(Post.class, "postReference", post.postReference).asList();
    }

    public static Post getPostByTitle(String title) {
        return MongoEntity.getDs().find(Post.class, "title", title).get();
    }

    public static List<Post> getLatestPostsByLocale(Locale locale, Integer number, Integer page) {
        if (number == null)
            number = 10;
        if (page == null)
            page = 1;
        List<Post> posts = MongoEntity.getDs().find(Post.class, "language", locale).order("-postedAt").offset((page - 1) * number).limit(number).asList();
        return (posts == null) ? new ArrayList<Post>() : posts;
    }

    public static Object getFirstPostByPostRef(PostRef postRef) {
        return MongoEntity.getDs().find(Post.class, "postReference", postRef).get();
    }

}
