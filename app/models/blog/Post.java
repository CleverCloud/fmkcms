package models.blog;

import controllers.UseCRUDFieldProvider;
import crud.BooleanField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import models.Tag;
import mongo.MongoEntity;
import play.Logger;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
// TODO: Why does hibernate still complains about duplicate comments ?
// TODO: Remove defaultPost from PostRef when only one
@Entity
public class Post extends MongoEntity {

    public Date postedAt;
    
    @Required
    public String title;

    @Lob
    @Required
    public String content;

    @ManyToOne
    public PostRef postReference;

    @Required
    public Locale language;

    @ManyToOne
    @Required
    public User author;

    @Required
    public Boolean isDefaultLanguage = false;

    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    public List<Comment> comments;

    //
    // Constructor
    //
    private Post(User author, Locale language, String title, String content) {
        this.author = author;
        this.language = language;
        this.title = title;
        this.content = content;
    }

    //
    // Comments handling
    //
    public Post addComment(String email, String pseudo, String password, String content) {
        Comment comment = null;
        /*if (User.findByEmail(email) != null || User.findByPseudo(pseudo) != null) {
            // If email is referrenced or pseudo is referrenced for another email, just do nothing.
            User user = User.connect(email, password);
            if (user == null) {
                Logger.error("Failed connection for existing user, aborting comment posting.", new Object[0]);
                return this;
            } else
                comment = new Comment(user, content).save();
        } else*/
            comment = new Comment(email, pseudo, content).save();
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
        this.postReference.tags.add(Tag.findOrCreateByName(name));
        this.postReference.save();
        return this;
    }

    public static List<Post> findTaggedWith(String ... tags) {
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

        Post concurrent = Post.getPostByLocale(this.postReference, language);
        if (concurrent != null) {
            concurrent.author = author;
            concurrent.title = title;
            concurrent.content = content;
            concurrent.save();
        } else
            Post.editOrCreate(this.postReference, author, language, title, content);

        return this;
    }

    public Post removeTranslation(Locale language) {
        if (this.language.equals(language)) {
            Logger.error("Cannot self remove, please remove from another translation (the default one ?).", new Object[0]);
            return this;
        }
        
        Post post = Post.getPostByLocale(this.postReference, language);

        if (post.isDefaultLanguage)
            Logger.error("Cannot remove translation for default language for: " + post.title + ". Please change default language first, by using setAsDefaultLanguage() on another translation.", new Object[0]);

        post.delete();
        return this;
    }

    public Post setAsDefaultLanguage() {
        Post defaultPost = Post.getDefaultPost(this.postReference);
        if (defaultPost != null) {
            if (defaultPost.id.equals(this.id))
                return this;
            defaultPost.isDefaultLanguage = false;
            defaultPost.save();
        }

        if (this.isDefaultLanguage) // Or we'll create a loop from the setter
            return this;

        this.isDefaultLanguage = Boolean.TRUE;
        return this.save();
    }

    //
    // Setters
    //
    public void setIsDefaultLanguage(Boolean isDefaultLanguage) {
        this.isDefaultLanguage = isDefaultLanguage;
        if (this.isDefaultLanguage)
            this.setAsDefaultLanguage();
        // TODO: prevent from removing default
        //Logger.error(this.title + " is the default language, if you want to change that, please use setAsDefaultLanguage on the new default.", new Object[0]);
    }

    public void setPostReference(PostRef postReference) {
        if (postReference != null && this.isDefaultLanguage != null && this.isDefaultLanguage)
                this.setAsDefaultLanguage();

        this.postReference = postReference;
    }

    //
    // Accessing stuff
    //
    public static Post getPostByLocale(PostRef postRef, Locale language) {
        //return Post.find("byPostReferenceAndLanguage", postRef, language).first();
        return null;
    }

    public static List<Post> getPostsByPostRef(PostRef postRef) {
        //return Post.find("byPostReference", postRef).fetch();
        return null;
    }

    public static Post getDefaultPost(PostRef postRef) {
        //return Post.find("byPostReferenceAndIsDefaultLanguage", postRef, true).first();
        return null;
    }

    //
    // Managing stuff
    //
    public static Post editOrCreate(PostRef postRef, User author, Locale language, String title, String content) {
        Post post = Post.getPostByLocale(postRef, language);
        if (post == null) {
            post = new Post(author, language, title, content);
            post.postReference = postRef;
        }
        else {
            post.content = content;
            post.title = title;
            post.author = author;
        }
        
        if(Post.getDefaultPost(postRef) == null)
            post.isDefaultLanguage = true;

        return post.save();
    }

    //
    // Hooks
    //
    @PrePersist
    @PreUpdate
    public void prePersistManagement() throws Exception {
        if (this.postReference == null)
            this.postReference = new PostRef().save();

        if (this.postedAt == null)
            this.postedAt = new Date();

        Post post = Post.getDefaultPost(this.postReference);
        if (post == null || (this.id != null && this.id == post.id)) { // We are creating the first Post for the PostRef
            this.isDefaultLanguage = Boolean.TRUE;
            if (this.postReference.author == null || this.postReference.postedAt == null) {
                this.postReference.author = this.author;
                this.postReference.postedAt = this.postedAt;
                this.postReference.save();
            }
        } else {
            post = Post.getPostByLocale(this.postReference, this.language);
            if (post != null && (this.id ==null || this.id != post.id))
                throw new Exception();
        }
    }

}
