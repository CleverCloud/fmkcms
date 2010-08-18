package models.blog;

import java.util.List;
import java.util.ListIterator;
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

    public PostData removeComment(String author, String content, Boolean removeAll) {
        // We want to got through it the reverse way to delete most recent comment first (duplicates)
        // TODO: .size() or .size() - 1 for use with hasPrevious and deal withthe last element
        ListIterator<Comment> iterator = this.comments.listIterator(this.comments.size());
        Comment current = null;
        while (iterator.hasPrevious()) {
            current = iterator.previous();
            // Continue if it's not the one we're looking for
            if (! (current.author.equalsIgnoreCase(author) && current.content.equalsIgnoreCase(content)))
                continue;

            current.delete();

            // Quit if we only want to remove one occurence of the comment
            if (! removeAll)
                break;
        }

        return this.save();
    }

    public PostData removeComment(String author, String content) {
        return this.removeComment(author, content, false);
    }

}
