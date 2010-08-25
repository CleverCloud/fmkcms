package models;

import controllers.UseCRUDFieldProvider;
import crud.BooleanField;
import java.util.Locale;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.Boost;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
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

    @Required
    public Locale lang;

    @Required
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean isDefaultLanguage = false;

    @ManyToOne
    public PageRef pageReference;
    
    @Required
    @UseCRUDFieldProvider(BooleanField.class)
    public Boolean published = false;

    public static Page getByUrlId(String urlId) {
        if (urlId == null)
            return null;
        
        Page p = Page.find("urlId = ?", urlId).first();
        return p;
    }

    public Page publish() {
        this.published = true;
        return this.save();
    }

    public Page unPublish() {
        this.published = false;
        return this.save();
    }

    /*public Page addTranslation(Page translated) {
        if (this.lang.equals(translated.lang))
            return this;
        
        this.otherLanguages.put(translated.lang, translated);
        translated.otherLanguages.put(this.lang, this);
        return this.save();
    }

    public Page getTranslation(Locale lang) {
        if (this.lang.equals(lang))
            return this;

        // Try exact Locale
        Page returnPage = this.otherLanguages.get(lang);
        if (returnPage != null)
            return returnPage;

        // Try exact language
        returnPage = this.otherLanguages.get(new Locale(lang.getLanguage()));
        if (returnPage != null)
            return returnPage;

        // Try from another country
        for (Locale current : this.otherLanguages.keySet()) {
            if (current.getLanguage().equals(lang.getLanguage())) {
                return this.otherLanguages.get(current);
            }
        }
        return null;
    }
     
     public void setLang(Locale lang) {
        for (Page current : this.otherLanguages.values()) {
            current.otherLanguages.remove(this.lang);
            current.otherLanguages.put(lang, this);
        }
        this.lang = lang;
        this.save();
    }*/
    
}
