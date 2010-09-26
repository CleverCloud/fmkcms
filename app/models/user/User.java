package models.user;

import java.util.Locale;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
public abstract class User extends MongoEntity {

    @Required
    public String email;
    public String userName;
    public String firstName;
    public String lastName;
    public Locale language;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Boolean needLtGt = Boolean.FALSE;
        if (this.firstName != null) {
            sb.append(this.firstName).append(" ");
            needLtGt = Boolean.TRUE;
        }
        if (this.lastName != null) {
            sb.append(this.lastName).append(" ");
            needLtGt = Boolean.TRUE;
        }
        if (needLtGt)
            sb.append("<");
        sb.append(this.email);
        if(needLtGt)
            sb.append(">");
        return sb.toString();
    }

}
