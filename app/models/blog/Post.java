package models.blog;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import controllers.I18nController;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import javax.persistence.Lob;
import models.Tag;
import mongo.MongoEntity;
import org.bson.types.ObjectId;
import play.Logger;
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
    private Post(PostRef postReference, User author, Locale language, String title, String content) {
        this.postReference = postReference;
        this.author = author;
        this.language = language;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }

    //
    // Comments handling
    //
    public Post addComment(String email, String pseudo, String password, String content) {
        Comment comment = new Comment();
        comment.email = email;
        comment.pseudo = pseudo;
        comment.content = content;
        comment.postedAt = new Date();
        this.comments.add(comment);
        return this.save();
    }

    public Post removeComment(String email, String content, Boolean removeAll) {
        // We want to get through it the reverse way to delete most recent comment first (duplicates)
        ListIterator<Comment> iterator = this.comments.listIterator(this.comments.size());
        Comment current = null;
        while (iterator.hasPrevious()) {
            current = iterator.previous();
            // Continue if it's not the one we're looking for
            if (! (current.email.equalsIgnoreCase(email) && current.content.equalsIgnoreCase(content)))
                continue;

            iterator.remove();
            current.delete();

            // Quit if we only want to remove one occurence of the comment
            if (! removeAll)
                break;
        }

        return this.save();
    }

    public Post removeComment(String email, String content) {
        return this.removeComment(email, content, false);
    }

    public List<Comment> getComments(String email) {
        List<Comment> returnList = new ArrayList<Comment>();
        for (Comment current : this.comments) {
            if (current.email.equalsIgnoreCase(email))
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
        // TODO: Reimplement Tag searching
/*        List<PostRef> postRefs = PostRef.find(
                "select distinct p from PostRef p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.postedAt having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
        
        List<Post> posts = new ArrayList<Post>();
        List<Locale> locales = I18nController.getBrowserLanguages();
        for (PostRef postRef : postRefs) {
            posts.add(postRef.getPost(locales));
        }

        return posts;*/
        return null;
    }

    //
    // I18n handling
    //
    public Post addTranslation(User author, Locale language, String title, String content) {
        if (this.language.equals(language)) {
            this.author = author;
            this.title = title;
            this.content = content;
            return this.save();
        }

        Post concurrent = Post.getPostByLocale(this.postReference.id, language);
        if (concurrent != null) {
            concurrent.author = author;
            concurrent.title = title;
            concurrent.content = content;
            return concurrent.save();
        }

        return new Post(this.postReference, author, language, title, content).save();
    }

    public Post removeTranslation(Locale language) {
        if (this.language.equals(language)) {
            Logger.error("Cannot self remove, please remove from another translation.", new Object[0]);
            return this;
        }

        Post.getPostByLocale(this.postReference.id, language).delete();

        return this;
    }

    //
    // Accessing stuff
    //
    public static Post getPostByLocale(ObjectId postRefId, Locale language) {
        return MongoEntity.getDs().find(Post.class, "postReference.id", postRefId).filter("language =", language).get();
    }

    public static List<Post> getPostsByPostRef(ObjectId postRefId) {
        return MongoEntity.getDs().find(Post.class, "postReference.id", postRefId).asList();
    }

    public static Post getFirstPostByPostRef(PostRef postRef) {
        return MongoEntity.getDs().find(Post.class, "postReference.id", postRef.id).get();
    }

    public static Post getPost(ObjectId postRefId) {
        List<Post> posts = Post.getPostsByPostRef(postRefId);

        switch (posts.size()) {
            case 0:
                return null;
            case 1:
                return posts.get(0);
            default:
                List<Locale> locales = I18nController.getBrowserLanguages();
                for (Locale locale : locales) {
                    // Try exact Locale
                    for (Post candidat : posts) {
                        if (candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage())))
                            return candidat;
                    }
                }

                return posts.get(0); // pick up first for now
        }
    }

}
