package models.blog;

import com.google.code.morphia.annotations.Entity;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import models.Tag;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
@Entity
public class PostRef extends MongoEntity {

    public Date postedAt;
    public User author;
    public Set<Tag> tags;

    //
    // Accessing stuff
    //
    public PostRef previous() {
        return MongoEntity.getDs().find(PostRef.class, "postedAt <", this.postedAt).order("-postedAt").get();
    }

    public PostRef next() {
        return MongoEntity.getDs().find(PostRef.class, "postedAt >", this.postedAt).order("postedAt").get();
    }

    public Post getPost(List<Locale> locales) {
        List<Post> posts = MongoEntity.getDs().find(Post.class, "postReference._id", this.id).asList();

        switch (posts.size()) {
            case 0:
                return null;
            case 1:
                return posts.get(0);
            default:
                for (Locale locale : locales) {
                    // Try exact Locale
                    for (Post candidat : posts) {
                        if ((candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage())))) {
                            return candidat;
                        }
                    }
                }
        }
        return posts.get(0);
    }
    
}
