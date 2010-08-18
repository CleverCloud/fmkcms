package models.blog;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class PostData extends Model {

    public String title;

    @Lob
    public String content;

    @ManyToOne
    public User author;

    public PostData(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

}
