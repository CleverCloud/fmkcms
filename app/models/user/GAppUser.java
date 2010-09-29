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

    public static GAppUser getByOpenId(String openId) {
        return MongoEntity.getDs().find(GAppUser.class, "openId", openId).get();
    }

}
