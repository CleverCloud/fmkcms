package models.blog;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class Post extends Model {

    public Date postedAt;
    public Locale defaultLanguage;

    @ManyToOne
    public User author;

    @OneToMany
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
