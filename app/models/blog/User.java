package models.blog;

import javax.persistence.Entity;
import play.data.validation.Email;
import play.data.validation.MinSize;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Crypto;

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
    @Password
    @MinSize(6) // Look in setPassword for this value too
    public String password;

    @Required
    public boolean isAdmin = false;

    public String fullname;
    public String pseudo;
    public String webSite;

    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = Crypto.passwordHash(password);
        this.fullname = fullname;
    }

    public static User connect(String emailOrPseudo, String password) {
        User user = User.find("byEmailAndPassword", emailOrPseudo, Crypto.passwordHash(password)).first();
        if (user != null)
            return user;
        return User.find("byPseudoAndPassword", emailOrPseudo, Crypto.passwordHash(password)).first();
    }

    public static User findByEmail(String email) {
        return User.find("byEmail", email).first();
    }

    public static User findByPseudo(String pseudo) {
        return User.find("byPseudo", pseudo).first();
    }

    @Override
    public String toString() {
        if (this.fullname == null || this.fullname.equals("")) {
            return this.email;
        }
        return this.fullname + " <" + this.email + ">";
    }

    public void setPassword(String password) {
        if (password.length() < 6)
            this.password = password; // Let @MinSize handle the error
        else
            this.password = Crypto.passwordHash(password); // Encrypt the password
    }

}
