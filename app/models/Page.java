/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 */
@Entity
@Indexed(index = "fmkpage")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, include = "all")
public class Page extends Model {

    @Required
    @Field
    @Boost(3.0f)
    public String title;

    @Required
    @Lob
    @Field
    @MaxSize(60000)
    @Boost(0.5f)
    public String content;

    @Required
    @Boost(3.5f)
    public String urlId;

    @IndexedEmbedded
    @ManyToMany(cascade = CascadeType.PERSIST)
    @Boost(1.0f)
    public Set<Tag> tags;

    @Required
    public Locale lang;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    public Map<Locale, Page> otherLanguages;
    
    @Required
    public Boolean published = false;

    public static Page getByUrlId(String urlId) {
        Page p = Page.find("urlId = ?", urlId).first();
        return p;
    }

    public Page tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this.save();
    }

    public Page publish() {
        this.published = true;
        return this.save();
    }

    public Page unPublish() {
        this.published = false;
        return this.save();
    }

    public Page addTranslation(Page translated) {
        if (this.lang.equals(translated.lang))
            return this;
        
        this.otherLanguages.put(translated.lang, translated);
        translated.otherLanguages.put(this.lang, this);
        return this.save();
    }

    public Page getTranslation(Locale lang) {
        if (this.lang.equals(lang))
            return this;
        
        Page returnPage = this.otherLanguages.get(lang);
        if (returnPage != null)
            return returnPage;

        for (Locale current : this.otherLanguages.keySet()) {
            if (current.getLanguage().equals(lang.getLanguage())) {
                return this.otherLanguages.get(current);
            }
        }
        return null;
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

    public void setLang(Locale lang) {
        for (Page current : this.otherLanguages.values()) {
            current.otherLanguages.remove(this.lang);
            current.otherLanguages.put(lang, this);
            current.save();
        }
        this.lang = lang;
        this.save();
    }
}
