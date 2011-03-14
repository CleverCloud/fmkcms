package models;

/**
 *
 * @author waxzce
 */
import com.google.code.morphia.annotations.Entity;
import mongo.MongoEntity;

@Entity
public class Tag extends MongoEntity implements Comparable<Tag> {

   public String name;

   /**
    * @param name The name of the Tag
    */
   private Tag(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }

   public int compareTo(Tag otherTag) {
      return this.name.compareTo(otherTag.name);
   }

   /**
    * Find a tag by its name or create it if it doesn't exist
    * @param name The name of the Tag
    * @return The Tag
    */
   public static Tag findOrCreateByName(String name) {
      if (name.isEmpty()) {
         return null;
      }
      name = name.trim();
      Tag tag = MongoEntity.getDs().find(Tag.class, "name", name).get();
      if (tag == null) {
         tag = new Tag(name);
         tag.save();
      }
      return tag;
   }
}
