package models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import controllers.I18nController;
import elasticsearch.IndexJob;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import mongo.MongoEntity;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@Entity
public class Page extends MongoEntity {

    @Required
    public String title;

    @Required
    @MaxSize(60000)
    public String content;

    @Required
    public Locale language;

    @Required
    @Reference
    public PageRef pageReference;

    @Required
    public Boolean published = false;

    @Embedded
    public Set<Tag> tags;

    //
    // Constructor
    //
    public Page() {}

    //
    // Tags handling
    //
    public Page tagItWith(String name) {
        if (name != null && !name.isEmpty()) {
            this.pageReference.tags.add(Tag.findOrCreateByName(name));
            this.pageReference.save();
        }
        return this;
    }

    public static List<Page> findTaggedWith(String... tags) {
        /*List<PageRef> pageRefs = PageRef.find(
        "select distinct p from PageRef p join p.tags as t where t.name in (:tags) group by p.id, p.urlId having count(t.id) = :size").bind("tags", tags).bind("size", tags.length).fetch();

        List<Page> pages = new ArrayList<Page>();
        List<Locale> locales = I18nController.getBrowserLanguages();
        for (PageRef pageRef : pageRefs) {
        pages.add(pageRef.getPage(locales));
        }

        return pages;*/
        return null;
    }

    //
    // I18n handling
    //
    public Page addTranslation(String title, String content, Locale language) {
        if (language.equals(this.language)) {
            this.title = title;
            this.content = content;
            return this.save();
        }

        Page concurrent = Page.getPageByLocale(this.pageReference.urlId, language);
        if (concurrent != null) {
            concurrent.title = title;
            concurrent.content = content;
            concurrent.save();
        } else {
            Page page = new Page();
            page.pageReference = this.pageReference;
            page.title = this.title;
            page.content = this.content;
            page.language = this.language;
            page.save();
        }

        return this;
    }

    public Page removeTranslation(Locale language) {
        if (this.language.equals(language)) {
            Logger.error("Cannot self remove, please remove from another translation.", new Object[0]);
            return this;
        }

        Page.getPageByLocale(this.pageReference.urlId, language).delete();

        return this;
    }

    //
    // Accessing stuff
    //

    public static List<Page> getPagesByUrlId(String urlId) {
        return MongoEntity.getDs().find(Page.class, "pageReference.urlId", urlId).asList();
    }

    public static Page getPageByLocale(String urlId, Locale locale) {
        return MongoEntity.getDs().find(Page.class, "pageReference.urlId", urlId).filter("language =", locale).get();
    }

    //
    // Managing stuff
    //
    public Page publish() {
        this.published = true;
        return this.save();
    }

    public Page unPublish() {
        this.published = false;
        return this.save();
    }
    
    //
    // Hooks
    //
 /*
    public void prePersistManagement() throws Exception {
    if (this.pageReference == null)
    this.pageReference = new PageRef().save();

    Page page = Page.getDefaultPage(this.pageReference);
    if (page == null || (this.id != null && this.id == page.id)) // We are creating the first Page for the PageRef
    this.isDefaultLanguage = Boolean.TRUE;
    else {
    page = Page.getPageByLocale(this.pageReference, this.language);
    if (page != null && (this.id ==null || this.id != page.id))
    throw new Exception();
    }
    }
     */
}
