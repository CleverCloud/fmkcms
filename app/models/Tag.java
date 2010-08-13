/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author waxzce
 */
import javax.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import play.db.jpa.*;

@Entity
@Indexed()
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all")
public class Tag extends Model implements Comparable<Tag> {

    @Column(unique=true)
    @Field
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
        name = name.trim();
        Tag tag = Tag.find("byName", name).first();
        if (tag == null) {
            tag = new Tag(name);
            tag.save();
        }
        return tag;
    }
}
