package models.blog;

import models.user.User;
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

    @Reference
    public User user;

    @Required
    public Date postedAt;

    @Required
    @Lob
    public String content;

}
