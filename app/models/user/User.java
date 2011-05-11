package models.user;

import mongo.MongoEntity;
import play.data.validation.Email;
import play.data.validation.Required;
import play.data.validation.URL;

/**
 *
 * @author keruspe
 */
public abstract class User extends MongoEntity {

   @Required
   @Email
   public String email;
   
   public String userName;
   @URL
   public String webSite;

   @Override
   public String toString() {
      if (this.webSite == null || this.webSite.isEmpty()) {
         return userName;
      }
      return "<a href=\"" + this.webSite + "\" >" + this.userName + "</a>";
   }

   
   public static User getByUsername(String username) {
      return User.getDs().find(User.class, "userName", username).get();
   }
}
