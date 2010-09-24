package models.blog;

import java.util.Locale;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
public abstract class User extends MongoEntity {

    public String userName;
    public String firstName;
    public String lastName;
    public String email;
    public Locale language;

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " <" + this.email + ">";
    }

}
