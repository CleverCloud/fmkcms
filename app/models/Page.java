package models;

import com.google.code.morphia.annotations.Entity;
import controllers.UseCRUDFieldProvider;
import crud.BooleanField;
import crud.PageRefField;
import java.util.List;
import java.util.Locale;
import mongo.MongoEntity;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;

/**
 *
 * @author waxzce
 * @author keruspe
 */
// TODO: Remove defaultPage from PageRef when only one
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
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean isDefaultLanguage = false;

    @Required
    @UseCRUDFieldProvider(PageRefField.class)
    public PageRef pageReference;

    @Required
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean published = false;

    //
    // Constructor
    //
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

        if (page.isDefaultLanguage) {
            Logger.error("Cannot remove translation for default language for: " + page.title + ". Please change default language first, by using setAsDefaultLanguage() on another translation.", new Object[0]);
        }

        page.delete();
        return this;
    }

    public Page setAsDefaultLanguage() {
        Page defaultPage = Page.getDefaultPage(this.pageReference);
        if (defaultPage != null) {
            if (defaultPage.id.equals(this.id)) {
                return this;
            }
            defaultPage.isDefaultLanguage = false;
            defaultPage.save();
        }

        if (this.isDefaultLanguage) // Or we'll create a loop from the setter
        {
            return this;
        }

        this.isDefaultLanguage = Boolean.TRUE;
        return this.save();
    }

    //
    // Setters
    //
    public void setPageReference(PageRef pageReference) {
        if (pageReference != null && this.isDefaultLanguage != null && this.isDefaultLanguage) {
            this.setAsDefaultLanguage();
        }

        this.pageReference = pageReference;
    }

    public void setIsDefaultLanguage(Boolean isDefaultLanguage) {
        this.isDefaultLanguage = isDefaultLanguage;
        if (this.isDefaultLanguage) {
            this.setAsDefaultLanguage();
        }
        // TODO: prevent from removing default
        //Logger.error(this.title + " is the default language, if you want to change that, please use setAsDefaultLanguage on the new default.", new Object[0]);
    }

    //
    // Accessing stuff
    //
    public static Page getByUrlId(String urlId) {
        //PageRef pageRef = PageRef.find("byUrlId", urlId).first();
        //return (pageRef == null) ? null : pageRef.getPage(I18nController.getBrowserLanguages());
        return null;
    }

    public static Page getPageByLocale(PageRef pageRef, Locale language) {
        //return Page.find("byPageReferenceAndLanguage", pageRef, language).first();
        return null;
    }

    public static List<Page> getPagesByPageRef(PageRef pageRef) {
        //return Page.find("byPageReference", pageRef).fetch();
        return null;
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

        if (Page.getDefaultPage(pageRef) == null) {
            page.isDefaultLanguage = true;
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
