/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 */
@Entity
public class Page extends Model {

    @Required
    public String title;
    @Required
    @Lob
    @MaxSize(60000)
    public String content;
    @Required
    public String urlId;
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    public static Page getByUrlId(String urlId) {
        return Page.find("urlId = ?", urlId).first();
    }

    public Page tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    public static List<Page> findTaggedWith(String tag) {
        return Page.find(
                "select distinct p from Page p join p.tags as t where t.name = ?", tag).fetch();
    }

    @PrePersist
    public void tagsManagement() {
        Set<Tag> tst = new TreeSet<Tag>();
        for (Iterator<Tag> it = tags.iterator(); it.hasNext();) {
            Tag tag = it.next();
            tst.add(Tag.findOrCreateByName(tag.name));
        }
        this.tags = tst;
    }
}
