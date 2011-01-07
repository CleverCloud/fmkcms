package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import models.i18n.TranslatableRef;
import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends TranslatableRef<Page, PageRef> {

    @Reference
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

    public static List<PageRef> findTaggedWith(Tag ... tags) {
        List<PageRef> pageRefs = new ArrayList<PageRef>();
        for (Tag tag : tags)
            pageRefs.addAll(MongoEntity.getDs().find(PageRef.class).field("tags").hasThisElement(tag).asList());
        return pageRefs;
    }

    public static List<PageRef> getPageRefsWithPagination(Integer pageNumber, Integer pageItemsNumber){
        return MongoEntity.getDs().find(PageRef.class).offset(pageItemsNumber * pageNumber).limit(pageItemsNumber).asList();
    }
}
