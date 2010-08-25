package models;

import controllers.UseCRUDFieldProvider;
import crud.BooleanField;
import java.util.List;
import java.util.Locale;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 * @author keruspe
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
    public Locale language;

    @Required
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean isDefaultLanguage = false;

    @Required
    @ManyToOne
    public PageRef pageReference;
    
    @Required
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean published = false;

    private Page(String title, String content, Locale language) {
        this.title = title;
        this.content = content;
        this.language = language;
    }

    public Page publish() {
        this.published = true;
        return this.save();
    }

    public Page unPublish() {
        this.published = false;
        return this.save();
    }

    public Page setAsDefaultLanguage() {
        Page defaultPage = Page.getDefaultPage(this.pageReference);
        if (defaultPage != null) {
            if (defaultPage.id.equals(this.id))
                return this;
            defaultPage.isDefaultLanguage = false;
            defaultPage.save();
        }

        if (! this.isDefaultLanguage) // Or we'll create a loop from the setter
            this.isDefaultLanguage = Boolean.TRUE;

        if (this.id == null) // We're creating it
            return this;

        return this.save();
    }

    public void setIsDefaultLanguage(Boolean isDefaultLanguage) {
        if (isDefaultLanguage) {
            this.isDefaultLanguage = Boolean.TRUE;
            this.setAsDefaultLanguage();
        }
        else if (this.isDefaultLanguage != null && this.isDefaultLanguage)
            Logger.error(this.title + " is the default language, if you want to change that, please use setAsDefaultLanguage on the new default.", new Object[0]);
        else
            this.isDefaultLanguage = isDefaultLanguage;
    }

    public static Page getPageByLocale(PageRef pageRef, Locale language) {
        return Page.find("byPageReferenceAndLanguage", pageRef, language).first();
    }

    public static List<Page> getPagesByPageRef(PageRef pageRef) {
        return Page.find("byPageReference", pageRef).fetch();
    }

    public static Page editOrCreate(PageRef pageRef, String title, String content, Locale language) {
        Page page = Page.getPageByLocale(pageRef, language);
        if (page == null) {
            page = new Page(title, content, language);
            page.pageReference = pageRef;
        }
        else {
            page.title = title;
            page.content = content;
        }

        if(Page.getDefaultPage(pageRef) == null)
            page.isDefaultLanguage = true;

        return page.save();
    }

    public static Page getDefaultPage(PageRef pageRef) {
        return Page.find("byPageReferenceAndIsDefaultLanguage", pageRef, true).first();
    }

    @PrePersist
    public void prePersistManagement() {
        if (this.pageReference == null)
            this.pageReference = new PageRef().save();

        Page defaultPage = Page.getDefaultPage(this.pageReference);
        if (defaultPage == null) // We are creating the first Page for the PageRef
            this.isDefaultLanguage = Boolean.TRUE;
    }
    
}
