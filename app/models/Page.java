package models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
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
    public PageRef pageReference;

    @Required
    public Boolean published = false;

    @Embedded
    public Set<Tag> tags;

    //
    // Constructor
    //
    public Page() {}

    private Page(String title, String content, Locale language) {
        this.title = title;
        this.content = content;
        this.language = language;
    }

    //
    // Tags handling
    //
    public Page tagItWith(String name) {
        this.pageReference.tags.add(Tag.findOrCreateByName(name));
        this.pageReference.save();
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

        Page concurrent = Page.getPageByLocale(this.pageReference, language);
        if (concurrent != null) {
            concurrent.title = title;
            concurrent.content = content;
            concurrent.save();
        } else {
            Page.editOrCreate(this.pageReference, title, content, language);
        }

        return this;
    }

    public Page removeTranslation(Locale language) {
        if (this.language.equals(language)) {
            Logger.error("Cannot self remove, please remove from another translation (the default one ?).", new Object[0]);
            return this;
        }

        Page page = Page.getPageByLocale(this.pageReference, language);

        page.delete();
        return this;
    }

    public Page setAsDefaultLanguage() {
        Page defaultPage = Page.getDefaultPage(this.pageReference);
        if (defaultPage != null) {
            if (defaultPage.id.equals(this.id)) {
                return this;
            }
            defaultPage.save();
        }

        return this.save();
    }

    //
    // Test stuff
    //

    @Override
    public Page save() {
        super.save();
        new IndexJob(this, "page", this.id.toString()).now();
        return this;
    }

    //
    // Accessing stuff
    //
    public static Page getByUrlId(String urlId) {
        PageRef pageRef = MongoEntity.getDs().find(PageRef.class, "urlId", urlId).get();
        return (pageRef == null) ? null : pageRef.getPage(I18nController.getBrowserLanguages());
    }

    public static Page getPageByLocale(PageRef pageRef, Locale language) {
        // TODO: FIXME pliz :D
        return MongoEntity.getDs().find(Page.class, "pageReference", pageRef).filter("language =", language).get();
    }

    public static List<Page> getPagesByPageRef(PageRef pageRef) {
        return MongoEntity.getDs().find(Page.class, "pageReference", pageRef).asList();
    }

    public static Page getDefaultPage(PageRef pageRef) {
        //return Page.find("byPageReferenceAndIsDefaultLanguage", pageRef, true).first();
        return null;
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

    public static Page editOrCreate(PageRef pageRef, String title, String content, Locale language) {
        Page page = Page.getPageByLocale(pageRef, language);
        if (page == null) {
            page = new Page(title, content, language);
            page.pageReference = pageRef;
        } else {
            page.title = title;
            page.content = content;
        }

        return page.save();
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
