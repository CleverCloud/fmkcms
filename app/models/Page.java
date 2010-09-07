package models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import elasticsearch.IndexJob;
import elasticsearch.Searchable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mongo.MongoEntity;
import play.Logger;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.mvc.Router;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@Entity
public class Page extends MongoEntity implements Searchable {

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
    public Page() {
    }

    private Page(PageRef pageReference, String title, String content, Locale language, Boolean published) {
        this.pageReference = pageReference;
        this.title = title;
        this.content = content;
        this.language = language;
        this.published = published;
    }

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
        // TODO: Reimplement Tag searching
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
    public Page addTranslation(String title, String content, Locale language, Boolean published) {
        if (language.equals(this.language)) {
            this.title = title;
            this.content = content;
            this.published = published;
            return this.save();
        }

        Page concurrent = Page.getPageByLocale(this.pageReference.urlId, language);
        if (concurrent != null) {
            concurrent.title = title;
            concurrent.content = content;
            concurrent.published = published;
            return concurrent.save();
        }

        return new Page(this.pageReference, title, content, language, published).save();
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

    public static Page getFirstPageByUrlId(String urlId) {
        return MongoEntity.getDs().find(Page.class, "pageReference.urlId", urlId).get();
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

    @Override
    public Page save() {
        super.save();
        new IndexJob(this, "page", this.id.toStringMongod()).now();
        return this;
    }

    public String getPrintTitle() {
        return title;
    }

    public String getPrintDesc() {
        return content;
    }

    public String getPrintURL() {
        Map<String, Object> argmap = new HashMap<String, Object>();
        argmap.put("urlId", pageReference.urlId);
        return Router.getFullUrl("PageController.page", argmap);
    }
    //
    // Hooks
    //
 /*
    public void prePersistManagement() throws Exception {
    if (this.pageReference == null)
    this.pageReference = new PageRef().save();
     */
}
