package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Transient;
import elasticsearch.IndexJob;
import elasticsearch.Searchable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Lob;
import models.i18n.Translatable;
import models.i18n.TranslatableRef;
import mongo.MongoEntity;
import org.bson.types.ObjectId;
import org.elasticsearch.search.SearchHit;
import org.jsoup.Jsoup;
import play.data.validation.Required;
import play.mvc.Router;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@Entity
@SuppressWarnings("unchecked")
public class Page extends Translatable<Page, PageRef> implements Searchable {

   @Required
   public String title;

   @Required
   @Lob
   public String content;

   @Required
   public Boolean published = false;

   @Transient
   private float score;

   //
   // Constructor
   //
   public Page() {
   }

   /* Make Play! views happy ... */
   public static Page call(Page other) {
      if (other == null) {
         return null;
      }
      Page page = new Page();
      page.urlId = other.urlId;
      page.title = other.title;
      page.content = other.content;
      page.language = other.language;
      page.reference = other.reference;
      page.published = other.published;
      return page;
   }

   //
   // Tags handling
   //
   public Page tagItWith(String name) {
      if (name != null && !name.isEmpty()) {
         this.reference.tags.add(Tag.findOrCreateByName(name));
         this.reference.save();
      }
      return this;
   }

   //
   // Accessing stuff
   //
   public static List<Page> getPagesByUrlId(String urlId) {
      Page page = Page.getPageByUrlId(urlId);
      return (page == null) ? new ArrayList<Page>() : MongoEntity.getDs().find(Page.class, "reference", page.reference).asList();
   }

   public static Page getPageByUrlId(String urlId) {
      return MongoEntity.getDs().find(Page.class, "urlId", urlId).get();
   }

   public static Page getPageByLocale(String urlId, Locale locale) {
      Page page = Page.getPageByUrlId(urlId);
      return (page == null) ? null : MongoEntity.getDs().find(Page.class, "reference", page.reference).filter("language =", locale).get();
   }

   public static Page getFirstPageByPageRef(PageRef pageRef) {
      return MongoEntity.getDs().find(Page.class, "reference", pageRef).get();
   }

   public static List<Page> getPagesByPageRef(PageRef pageRef) {
      return MongoEntity.getDs().find(Page.class, "reference", pageRef).asList();
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
      new IndexJob(this, Page.class.getCanonicalName(), this.id.toStringMongod()).now();
      return this;
   }

   public String getPrintTitle() {
      return title;
   }

   public String getPrintDesc() {
      return Jsoup.parse(this.content).body().text();
   }

   public String getPrintURL() {
      Map<String, Object> argmap = new HashMap<String, Object>();
      argmap.put("urlId", this.urlId);
      return Router.getFullUrl("PageViewer.page", argmap);
   }

   public static Searchable getFrom(SearchHit sh) {
      return MongoEntity.getDs().get(Page.class, new ObjectId(sh.id()));
   }

   public float getScore() {
      return this.score;
   }

   public void setScore(float score) {
      this.score = score;
   }

   public ObjectId getEntityId() {
      return this.id;
   }
}
