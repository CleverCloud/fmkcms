package models.blog;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.Date;
import javax.persistence.Lob;
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

    @Reference
    public User user;

    @Required
    public Date postedAt;

    @Required
    @Lob
    public String content;

}
