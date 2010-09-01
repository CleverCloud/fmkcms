package models.blog;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
@Entity
public class Comment extends MongoEntity {

    @Required
    public String email;

    public String pseudo;
    public User user;

    @Required
    public Date postedAt;

    @Required
    @Lob
    public String content;

    public Comment(String email, String pseudo, String content) {
        this.email = email;
        this.pseudo = pseudo;
        this.content = content;
        this.postedAt = new Date();
    }

    public Comment(User user, String content) {
        this.email = user.getEmail();
        this.pseudo = user.getPseudo();
        this.user = user;
        this.content = content;
        this.postedAt = new Date();
    }

    @PrePersist
    public void dateManagement() {
        if (this.postedAt == null)
            this.postedAt = new Date();
    }

}
