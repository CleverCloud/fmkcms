package models.blog;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class PostData extends Model {

    @Required
    public String title;

    @Lob
    @Required
    public String content;

    @ManyToOne
    @Required
    public User author;

    public PostData(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

}
