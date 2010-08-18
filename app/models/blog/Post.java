package models.blog;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
        this.translations.remove(language);
        return this.save();
    }

    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }

}
