package models;

/**
 *
 * @author waxzce
 */
import javax.persistence.*;
import mongo.MongoEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all")
public class Tag extends MongoEntity<Tag> implements Comparable<Tag> {

    @Column(unique=true)
    public String name;

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

    public static Tag findOrCreateByName(String name) {
        /*name = name.trim();
        Tag tag = Tag.find("byName", name).first();
        if (tag == null) {
            tag = new Tag(name);
            tag.save();
        }
        return tag;*/
        return null;
    }
}
