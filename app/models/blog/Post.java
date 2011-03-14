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
   public Post() {
   }

   /* Make Play! views happy ... */
   public static Post call(Post other) {
      if (other == null) {
         return null;
      }
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
   /**
    * Tag this post
    * @param name The name of the Tag
    * @return self
    */
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
   /**
    * Get a Post matching a urlId and a Locale
    * @param urlId The urlid
    * @param language The Locale
    * @return The Post
    */
   public static Post getPostByLocale(String urlId, Locale language) {
      Post post = Post.getPostByUrlId(urlId);
      return (post == null) ? null : MongoEntity.getDs().find(Post.class, "reference", post.reference).filter("language =", language).get();
   }

   /**
    * Get all Posts for a given PostRef
    * @param reference The PostRef
    * @return THe list of Posts
    */
   public static List<Post> getPostsByPostRef(PostRef reference) {
      return MongoEntity.getDs().find(Post.class, "reference", reference).asList();
   }

   /**
    * Get all Posts for a given UrlId (all translations)
    * @param urlId The urlId
    * @return The list of Posts
    */
   public static List<Post> getPostsByUrlId(String urlId) {
      Post post = Post.getPostByUrlId(urlId);
      return (post == null) ? new ArrayList<Post>() : MongoEntity.getDs().find(Post.class, "reference", post.reference).asList();
   }

   /**
    * Get the first Post for a given UrlId
    * @param urlId The urlId
    * @return The Post
    */
   public static Post getPostByUrlId(String urlId) {
      return MongoEntity.getDs().find(Post.class, "urlId", urlId).get();
   }

   /**
    * Get the latest Posts for a given Locale
    * @param locale The Locale
    * @param number The number of posts
    * @param page The page (for pagination)
    * @return The list of posts
    */
   public static List<Post> getLatestPostsByLocale(Locale locale, Integer number, Integer page) {
      if (number == null) {
         number = 10;
      }
      if (page == null) {
         page = 1;
      }
      List<Post> posts = MongoEntity.getDs().find(Post.class, "language", locale).order("-postedAt").offset((page - 1) * number).limit(number).asList();
      return (posts == null) ? new ArrayList<Post>() : posts;
   }

   /**
    * Get the first Post for a given PostRef
    * @param reference The PostRef
    * @return The Post
    */
   public static Post getFirstPostByPostRef(PostRef reference) {
      return MongoEntity.getDs().find(Post.class, "reference", reference).get();
   }
}
