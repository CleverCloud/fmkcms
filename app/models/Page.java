package models;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import elasticsearch.IndexJob;
import elasticsearch.Searchable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.persistence.Lob;
import mongo.MongoEntity;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Router;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@Entity
@SuppressWarnings("unchecked")
public class Page extends MongoEntity implements Searchable {

    @Required
    public String urlId;

    @Required
    public String title;

    @Required
    @Lob
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

    private Page(PageRef pageReference, String urlId, String title, String content, Locale language, Boolean published) {
        this.pageReference = pageReference;
        this.urlId = urlId;
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

    public static List<Page> findTaggedWith(String ... tags) {
        // TODO: waxzce, gogo elastic search !
        List<Page> p = MongoEntity.getDs().find(Page.class).field("tags").hasAnyOf(Arrays.asList(tags)).asList();
        return p;
    }

    //
    // I18n handling
    //
    public Page removeTranslation(Locale language) {
        if (this.language.equals(language)) {
            Logger.error("Cannot self remove, please remove from another translation.", new Object[0]);
            return this;
        }

        Page.getPageByLocale(this.urlId, language).delete();

        return this;
    }

    //
    // Accessing stuff
    //
    public static List<Page> getPagesByUrlId(String urlId) {
        Page page = Page.getPageByUrlId(urlId);
        return (page == null) ? new ArrayList<Page>() : MongoEntity.getDs().find(Page.class, "pageReference._id", page.pageReference.id).asList();
    }

    public static Page getPageByUrlId(String urlId) {
        return MongoEntity.getDs().find(Page.class, "urlId", urlId).get();
    }

    public static Page getPageByLocale(String urlId, Locale locale) {
        Page page = Page.getPageByUrlId(urlId);
        return (page == null) ? null : MongoEntity.getDs().find(Page.class, "pageReference._id", page.pageReference.id).filter("language =", locale).get();
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
        argmap.put("urlId", this.urlId);
        return Router.getFullUrl("PageController.page", argmap);
    }

}
