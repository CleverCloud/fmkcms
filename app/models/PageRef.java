package models;

import controllers.UseCRUDFieldProvider;
import crud.TagsField;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.IndexedEmbedded;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
public class PageRef extends Model {

    @IndexedEmbedded
    @ManyToMany(cascade = CascadeType.PERSIST)
    @Boost(1.0f)
    @UseCRUDFieldProvider(TagsField.class)
    public Set<Tag> tags;

    public Page tagItWith(String name) {
        this.tags.add(Tag.findOrCreateByName(name));
        return this.save();
    }
    
    public static List<Page> findTaggedWith(String tag) {
        return Page.find(
                "select distinct p from Page p join p.tags as t where t.name = ?", tag).fetch();
    }

    @PrePersist
    public void tagsManagement() {
        if (tags != null) {
            Set<Tag> newTags = new TreeSet<Tag>();
            for (Tag tag : this.tags) {
                newTags.add(Tag.findOrCreateByName(tag.name));
            }
            this.tags = newTags;
        }
    }

}
