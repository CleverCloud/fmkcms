package models.blog;

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
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class Post extends Model {

    public Date postedAt;
    
    @Required
    public String title;

    @Lob
    @Required
    public String content;

    @Required
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

    private Post(User author, Locale language, String title, String content) {
        this.author = author;
        this.language = language;
        this.title = title;
        this.content = content;
    }

    public Post addComment(String email, String pseudo, String password, String content) {
        Comment comment = null;
        if (User.findByEmail(email) != null || User.findByPseudo(pseudo) != null) {
            // If email is referrenced or pseudo is referrenced for another email, just do nothing.
            User user = User.connect(email, password);
            if (user == null) {
                Logger.error("Failed connection for existing user, aborting comment posting.", new Object[0]);
                return this;
            } else
                comment = new Comment(user, content).save();
        } else
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

    public Post setAsDefaultLanguage() {
        Post defaultPost = Post.getDefaultPost(this.postReference);
        if (defaultPost != null) {
            defaultPost.isDefaultLanguage = false;
            defaultPost.save();
        }
        this.isDefaultLanguage = true;
        return this.save();
    }

    public void setIsDefaultLanguage(Boolean isDefaultLanguage) {
        if (isDefaultLanguage)
            this.setAsDefaultLanguage();
        else
            Logger.error(this.title + " is the default language, if you want to change that, please use setAsDefaultLanguage on the new default.", new Object[0]);
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

    static Post getPostByLocale(PostRef postRef, Locale language) {
        return Post.find("byPostRefAndLanguage", postRef, language).first();
    }

    static List<Post> getPostsByPostRef(PostRef postRef) {
        return Post.find("byPostRef", postRef).fetch();
    }

    static Post editOrCreate(PostRef postRef, User author, Locale language, String title, String content) {
        Post post = Post.getPostByLocale(postRef, language);
        if (post == null)
            post = new Post(author, language, title, content);
        else {
            post.content = content;
            post.title = title;
            post.author = author;
        }
        
        if(Post.getDefaultPost(postRef) == null)
            post.isDefaultLanguage = true;

        return post.save();
    }

    static Post getDefaultPost(PostRef postRef) {
        return Post.find("byPostRefAndDefaultTranslation", postRef, true).first();
    }

    @PrePersist
    public void dateManagement() {
        if (this.postedAt == null) {
            this.postedAt = new Date();
            if (this.isDefaultLanguage) {
                // We are creating the first Post for the PostRef
                this.postReference.author = this.author;
                this.postReference.postedAt = this.postedAt;
                this.postReference.save();
            }
        }
    }

}
