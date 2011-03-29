package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.Arrays;
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

   /**
    * Get all tags as a String ("tag1, tag2, ...")
    * @return The list of Tags as a String
    */
   public String getTagsAsString() {
      String tagsString = new String();
      if (this.tags == null) {
         return tagsString;
      }
      for (Tag tag : new TreeSet<Tag>(this.tags)) {
         tagsString += (tagsString.isEmpty() ? "" : ", ") + tag.toString();
      }
      return tagsString;
   }

   /**
    * Find all PageRefs tagged with given Tags
    * @param tags Tags to match
    * @return The list of PageRefs
    */
   public static List<PageRef> findTaggedWith(Tag... tags) {
      List<PageRef> pageRefs = new ArrayList<PageRef>();
      for (Tag tag : tags) {
         pageRefs.addAll(MongoEntity.getDs().find(PageRef.class).field("tags").hasThisElement(tag).asList());
      }
      return pageRefs;
   }

   /**
    * Find all PageRefs not tagged with given Tags
    * @param tags Tags not to match
    * @return The list of PageRefs
    */
   public static List<PageRef> findNotTaggedWith(Tag... tags) {
      return MongoEntity.getDs().find(PageRef.class).field("tags").hasNoneOf(Arrays.asList(tags)).asList();
   }

   /**
    * Get PageRefs with pagination
    * @param pageNumber The number of the page
    * @param pageItemsNumber The number of item by page
    * @return The list of PageRefs
    */
   public static List<PageRef> getPageRefsWithPagination(Integer pageNumber, Integer pageItemsNumber) {
      return MongoEntity.getDs().find(PageRef.class).offset(pageItemsNumber * pageNumber).limit(pageItemsNumber).asList();
   }
}
