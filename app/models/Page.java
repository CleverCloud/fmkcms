/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
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
    public String urlid;
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    public static Page getByUrlid(String urlid) {
        return Page.find("urlid = ?", urlid).first();
    }

    public Page tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    public static List<Page> findTaggedWith(String tag) {
        return Page.find(
                "select distinct p from Page p join p.tags as t where t.name = ?", tag).fetch();
    }
}
