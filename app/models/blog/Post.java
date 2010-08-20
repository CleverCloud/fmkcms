package models.blog;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import models.Tag;
import net.sf.oval.constraint.NotEmpty;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class Post extends Model {

    @Required
    public Date postedAt;

    @Required
    public Locale defaultLanguage;

    @ManyToOne
    @Required
    public User author;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Required
    @NotEmpty
    // TODO: Handle Map with CRUD
    public Map<Locale, PostData> translations;

    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;

    public Post(User author, Locale language, String title, String content) {
        this.author = author;
        this.defaultLanguage = language;
        PostData defaultTranslation = new PostData(author, title, content).save();
        this.translations = new HashMap<Locale, PostData>();
        this.translations.put(language, defaultTranslation);
        this.postedAt = new Date();
        this.tags = new TreeSet<Tag>();
    }

    public Post addTranslation(User author, Locale language, String title, String content) {
        PostData translation = new PostData(author, title, content).save();
        this.translations.put(language, translation);
        return this.save();
    }

    public Post removeTranslation(Locale language) {
        if (language.equals(this.defaultLanguage)) {
            Logger.error("Cannot remove translation for default language for: " + this.getDefaultData().title + ". Please change defaultLanguage first.", new Object[0]);
            return this;
        }
        this.translations.get(language).delete();
        this.translations.remove(language);
        return this.save();
    }

    public PostData getData(List<Locale> languages) {
        PostData data = null;

        for (Locale language : languages) {
            // Try exact Locale
            data = this.translations.get(language);
            if (data != null)
                return data;

            // Try exact language but don't double check
            if (! language.getCountry().equals("")) {
                data = this.translations.get(new Locale(language.getLanguage()));
                if (data != null)
                    return data;
            }
        }

        for (Locale language : languages) {
            // Try from another country
            for (Locale current : this.translations.keySet()) {
                if (current.getLanguage().equals(language.getLanguage())) {
                    return this.translations.get(current);
                }
            }
        }

        // Return default
        return this.getDefaultData();
    }

    public PostData getDefaultData() {
        return this.translations.get(this.defaultLanguage);
    }

    public void setDefaultLanguage(Locale language) {
        if (this.translations.containsKey(language)) {
            this.defaultLanguage = language;
        } else {
            Logger.error("Cannot change default language for: " + this.getDefaultData().title + ". No translation available for this language.", new Object[0]);
        }
    }

    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }

    public Post tagItWith(String name) {
        this.tags.add(Tag.findOrCreateByName(name));
        return this.save();
    }

    public static List<Post> findTaggedWith(String ... tags) {
        return Post.find(
                "select distinct p from Post p join p.tags as t where t.name in (:tags) group by p.id, p.author, p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }

}
