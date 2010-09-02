package models;

import com.google.code.morphia.annotations.Entity;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends MongoEntity {

    // TODO: Decide whether we put that in Page
    @Required
    public String urlId;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;

    //
    // Accessing stuff
    //
    public Page getPage(List<Locale> languages) {
        Page page = null;

        for (Locale language : languages) {
            // Try exact Locale
            page = Page.getPageByLocale(this, language);
            if (page != null)
                return page;

            // Try exact language but don't double check
            if (!language.getCountry().equals("")) {
                page = null; //Page.getPageByLocale(this, new Locale(language.getLanguage()));
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

    //
    // Hooks
    //
    /*@PrePersist
    public void tagsManagement() {
        if (tags != null) {
            Set<Tag> newTags = new TreeSet<Tag>();
            for (Tag tag : this.tags) {
                newTags.add(Tag.findOrCreateByName(tag.name));
            }
            this.tags = newTags;
        }
    }*/

}
