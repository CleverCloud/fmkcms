package models;

import controllers.UseCRUDFieldProvider;
import crud.TagsField;
import javax.persistence.Entity;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.IndexedEmbedded;
import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends Model {

    @Required
    @Boost(3.5f)
    public String urlId;

    @IndexedEmbedded
    @ManyToMany(cascade = CascadeType.PERSIST)
    @Boost(1.0f)
    @UseCRUDFieldProvider(TagsField.class)
    public Set<Tag> tags;

    public Page tagItWith(String name) {
        this.tags.add(Tag.findOrCreateByName(name));
        return this.save();
    }
    
    public static List<PageRef> findTaggedWith(String ... tags) {
        return PageRef.find(
                "select distinct p from PageRef p join p.tags as t where t.name in (:tags) group by p.id, p.urlId having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();
    }
    
    public PageRef addTranslation(String title, String content, Locale language) {
        Page.editOrCreate(this, title, content, language);
        return this;
    }

    public PageRef removeTranslation(Locale language) {
        Page page = Page.getPageByLocale(this, language);
        if (page.isDefaultLanguage)
            Logger.error("Cannot remove translation for default language for: " + page.title + ". Please change default language first, by using setAsDefaultLanguage() on another translation.", new Object[0]);
        page.delete();
        return this;
    }

    public static PageRef getByUrlId(String urlId) {
        return PageRef.find("byUrlId", urlId).first();
    }

    public Page getPage(List<Locale> languages) {
        Page page = null;

        for (Locale language : languages) {
            // Try exact Locale
            page = Page.getPageByLocale(this, language);
            if (page != null)
                return page;

            // Try exact language but don't double check
            if (!language.getCountry().equals("")) {
                page = Page.getPageByLocale(this, new Locale(language.getLanguage()));
                if (page != null)
                    return page;
            }
        }

        for (Locale language : languages) {
            // Try from another country
            for (Page current : Page.getPagesByPageRef(this)) {
                if (current.language.getLanguage().equals(language.getLanguage()))
                    return current;
            }
        }

        // Return default
        return this.getDefaultPage();
    }
    
    public Page getDefaultPage() {
        return Page.getDefaultPage(this);
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
