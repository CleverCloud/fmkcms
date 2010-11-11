package models;

import com.google.code.morphia.annotations.Entity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends MongoEntity {

    public Set<Tag> tags;

    //
    // Accessing stuff
    //
    public static PageRef getPageRef(ObjectId id) {
        return MongoEntity.getDs().find(PageRef.class, "id", id).get();
    }

    public String getTagsAsString() {
        String tagsString = new String();
        if (this.tags == null)
            return tagsString;
        for (Tag tag : new TreeSet<Tag>(this.tags))
            tagsString += (tagsString.isEmpty() ? "" : ", ") + tag.toString();
        return tagsString;
    }

    public List<Locale> getAvailableLocales() {
        List<Page> pages = MongoEntity.getDs().find(Page.class, "pageReference", this).asList();
        List<Locale> locales = new ArrayList<Locale>();

        if (pages != null && !pages.isEmpty()) {
            for (Page page : pages)
                locales.add(page.language);
        }

        return locales;
    }

    public static List<PageRef> findTaggedWith(Tag ... tags) {
        return MongoEntity.getDs().find(PageRef.class).field("tags").hasAnyOf(Arrays.asList(tags)).asList();
    }

}
