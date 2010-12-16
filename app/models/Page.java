package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;
import elasticsearch.IndexJob;
import elasticsearch.Searchable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Lob;
import mongo.MongoEntity;
import org.bson.types.ObjectId;
import org.elasticsearch.search.SearchHit;
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
   @Reference
   public PageRef pageReference;

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
      page.pageReference = other.pageReference;
      page.published = other.published;
      return page;
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

   //
   // Accessing stuff
   //
   public static List<Page> getPagesByUrlId(String urlId) {
      Page page = Page.getPageByUrlId(urlId);
      return (page == null) ? new ArrayList<Page>() : MongoEntity.getDs().find(Page.class, "pageReference", page.pageReference).asList();
   }

   public static Page getPageByUrlId(String urlId) {
      return MongoEntity.getDs().find(Page.class, "urlId", urlId).get();
   }

   public static Page getPageByLocale(String urlId, Locale locale) {
      Page page = Page.getPageByUrlId(urlId);
      return (page == null) ? null : MongoEntity.getDs().find(Page.class, "pageReference", page.pageReference).filter("language =", locale).get();
   }

   public static Page getFirstPageByPageRef(PageRef pageRef) {
      return MongoEntity.getDs().find(Page.class, "pageReference", pageRef).get();
   }

   public static List<Page> getPagesByPageRef(PageRef pageRef) {
      return MongoEntity.getDs().find(Page.class, "pageReference", pageRef).asList();
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
      return content;
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
}
