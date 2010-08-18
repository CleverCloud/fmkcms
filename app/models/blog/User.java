package models.blog;

import javax.persistence.Entity;
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class User extends Model {

    @Required
    @Email
    public String email;

    @Required
    public String password;

    @Required
    public boolean isAdmin = false;

    public String fullname;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }

    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }

}
