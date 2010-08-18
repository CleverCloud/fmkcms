package models.blog;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Entity;
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

    @OneToMany
    @Required
    public Map<Locale, PostData> translations;

    public Post(User author) {
        this.author = author;
        this.postedAt = new Date();
    }

    public Post previous() {
        return Post.find("postedAt < ? order by postedAt desc", postedAt).first();
    }

    public Post next() {
        return Post.find("postedAt > ? order by postedAt asc", postedAt).first();
    }

}
