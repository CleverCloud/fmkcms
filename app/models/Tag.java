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

import play.db.jpa.*;

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all")
public class Tag extends Model implements Comparable<Tag> {

    public String name;

    private Tag(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }

    public static Tag findOrCreateByName(String name) {
        Tag tag = Tag.find("byName", name).first();
        if (tag == null) {
            tag = new Tag(name);
            tag.save();
        }
        return tag;
    }
}
