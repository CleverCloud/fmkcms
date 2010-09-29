package models.user;

import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
public abstract class User extends MongoEntity {

    @Required
    public String email;
    @Required
    public String userName;
    public String webSite;

    @Override
    public String toString() {
        return userName;
    }

}
