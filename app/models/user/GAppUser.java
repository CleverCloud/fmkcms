package models.user;

import com.google.code.morphia.annotations.Entity;
import java.util.Locale;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
@Entity
public class GAppUser extends User {

   public String openId;
   public String firstName;
   public String lastName;
   public Locale language;

   /**
    * Retrieve a user from its openId
    * @param openId The openId of the User (String)
    * @return The GAppUser
    */
   public static GAppUser getByOpenId(String openId) {
      return MongoEntity.getDs().find(GAppUser.class, "openId", openId).get();
   }
}
