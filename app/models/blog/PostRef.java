package models.blog;

import controllers.UseCRUDFieldProvider;
import crud.TagsField;
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
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class PostRef extends Model {

    public Date postedAt;

    @ManyToOne
    public User author;

    @ManyToMany
    @UseCRUDFieldProvider(TagsField.class)
    public Set<Tag> tags;

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
        return this.getDefaultPost();
    }

    public Post getDefaultPost() {
        return Post.getDefaultPost(this);
    }

    public PostRef previous() {
        return PostRef.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public PostRef next() {
        return PostRef.find("postedAt > ? order by postedAt asc", postedAt).first();
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
