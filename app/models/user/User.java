package models.user;

import mongo.MongoEntity;
import play.data.validation.Required;
import play.data.validation.URL;

/**
 *
 * @author keruspe
 */
public abstract class User extends MongoEntity {

    @Required
    public String email;

    @Required
    public String userName;

    @URL
    public String webSite;

    @Override
    public String toString() {
        if (this.webSite == null || this.webSite.isEmpty())
            return userName;
        return "<a href=\"" + this.webSite + "\" >" + this.userName + "</a>";
    }

}
