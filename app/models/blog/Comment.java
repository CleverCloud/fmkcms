package models.blog;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Lob;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class Comment extends Model {

    // TODO: Decide wether we support/require authentication here
    @Required
    public String author;

    @Required
    public Date postedAt;

    @Required
    @Lob
    public String content;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
        this.postedAt = new Date();
    }

}
