package models.blog;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import models.Tag;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class PostRef extends Model {

    public Date postedAt;

    @ManyToOne
    @Required
    public User author;

    @ManyToMany
    public Set<Tag> tags;

    public PostRef addTranslation(User author, Locale language, String title, String content) {
        Post.editOrCreate(this, author, language, title, content);
        return this;
    }

    public PostRef removeTranslation(Locale language) {
        Post post = Post.getPostByLocale(this, language);
        if (post.isDefaultLanguage) {
            Logger.error("Cannot remove translation for default language for: " + post.title + ". Please change default language first, by using setAsDefaultLanguage() on another translation.", new Object[0]);
        }
        post.delete();
        return this;
    }

    public Post getPost(List<Locale> languages) {
        Post post = null;

        for (Locale language : languages) {
            // Try exact Locale
            post = Post.getPostByLocale(this, language);
            if (post != null)
                return post;

            // Try exact language but don't double check
            if (!language.getCountry().equals("")) {
                post = Post.getPostByLocale(this, new Locale(language.getLanguage()));
                if (post != null)
                    return post;
            }
        }

        for (Locale language : languages) {
            // Try from another country
            for (Post current : Post.getPostsByPostRef(this)) {
                if (current.language.getLanguage().equals(language.getLanguage()))
                    return current;
            }
        }

        // Return default
        return this.getDefaultData();
    }

    public Post getDefaultData() {
        return Post.getDefaultPost(this);
    }

    public PostRef previous() {
        return PostRef.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public PostRef next() {
        return PostRef.find("postedAt > ? order by postedAt asc", postedAt).first();
    }

    public PostRef tagItWith(String name) {
        this.tags.add(Tag.findOrCreateByName(name));
        return this.save();
    }

    public static List<PostRef> findTaggedWith(String... tags) {
        return Post.find(
                "select distinct p from Post p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.postedAt having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
    }

    @PrePersist
    public void tagsManagement() {
        if (tags != null) {
            Set<Tag> newTags = new TreeSet<Tag>();
            for (Tag tag : this.tags) {
                newTags.add(Tag.findOrCreateByName(tag.name));
            }
            this.tags = newTags;
        }
    }
}
