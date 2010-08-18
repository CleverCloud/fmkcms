package models.blog;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    public Map<Locale, PostData> translations;

    public Post(User author, Locale language, String title, String content) {
        this.author = author;
        this.defaultLanguage = language;
        this.translations.put(language, new PostData(author, title, content));
        this.postedAt = new Date();
    }

    public Post addTranslation(User author, Locale language, String title, String content) {
        this.translations.put(language, new PostData(author, title, content));
        return this.save();
    }

    public Post removeTranslation(Locale language) {
        if (language.equals(this.defaultLanguage)) {
            Logger.error("Cannot remove translation for default language for: " + this.getDefaultData().title + ". Please change defaultLanguage first.", new Object[0]);
            return this;
        }
        this.translations.remove(language);
        return this.save();
    }

    public PostData getData(Locale language) {
        // Try exact Locale
        PostData data = this.translations.get(language);
        if (data != null)
            return data;

        // Try exact language
        data = this.translations.get(new Locale(language.getLanguage()));
        if (data != null)
            return data;

        // Try from another country
        for (Locale current : this.translations.keySet()) {
            if (current.getLanguage().equals(language.getLanguage())) {
                return this.translations.get(current);
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

}
