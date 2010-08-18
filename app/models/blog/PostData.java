package models.blog;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    //TODO: get/remove comment
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    public List<Comment> comments;

    public PostData(User author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
    }

    public PostData addComment(String author, String content) {
        Comment comment = new Comment(author, content).save();
        this.comments.add(comment);
        return this.save();
    }

}
