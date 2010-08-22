package models.blog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
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
public class PostData extends Model {

    @Required
    public String title;

    @Lob
    @Required
    public String content;

  /*  @ManyToOne
    @Required
    public User author;
*/
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
    public List<Comment> comments;

    public PostData(User author, String title, String content) {
  //      this.author = author;
        this.title = title;
        this.content = content;
    }

    public PostData addComment(String email, String pseudo, String password, String content) {
        Comment comment = null;
        if (User.findByEmail(email) != null || User.findByPseudo(pseudo) != null) {
            // If email is referrenced or pseudo is referrenced for another email, just do nothing.
            User user = User.connect(email, password);
            if (user == null) {
                Logger.error("Failed connection for existing user, aborting comment posting.", new Object[0]);
                return this;
            } else
                comment = new Comment(user, content).save();
        } else
            comment = new Comment(email, pseudo, content).save();
        this.comments.add(comment);
        return this.save();
    }

    public PostData removeComment(String email, String content, Boolean removeAll) {
        // We want to get through it the reverse way to delete most recent comment first (duplicates)
        ListIterator<Comment> iterator = this.comments.listIterator(this.comments.size());
        Comment current = null;
        while (iterator.hasPrevious()) {
            current = iterator.previous();
            // Continue if it's not the one we're looking for
            if (! (current.email.equalsIgnoreCase(email) && current.content.equalsIgnoreCase(content)))
                continue;

            iterator.remove();
            current.delete();

            // Quit if we only want to remove one occurence of the comment
            if (! removeAll)
                break;
        }

        return this.save();
    }

    public PostData removeComment(String email, String content) {
        return this.removeComment(email, content, false);
    }

    public List<Comment> getComments(String email) {
        List<Comment> returnList = new ArrayList<Comment>();
        for (Comment current : this.comments) {
            if (current.email.equalsIgnoreCase(email))
                returnList.add(current);
        }
        return returnList;
    }

}
