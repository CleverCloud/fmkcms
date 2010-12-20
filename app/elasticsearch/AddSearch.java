package elasticsearch;

import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;
import models.Page;
import mongo.MongoEntity;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 *
 * @author waxzce
 *
 * this is a functionality disabled for now but it can be fine to add search in all page
 *
 */
@OnApplicationStart
public class AddSearch extends Job {

   @Override
   public void doJob() throws Exception {
      ApplicationClasses appClasses = Play.classes;
      List<ApplicationClass> classes = appClasses.all();

      List<Class> toIndex = new LinkedList<Class>();

      for (ApplicationClass acl : classes) {
         acl.javaClass.getInterfaces();
         for (Class inter : acl.javaClass.getInterfaces()) {
            if (inter.equals(Searchable.class)) {
               toIndex.add(acl.javaClass);
            }
         }
      }

      System.out.println(toIndex);

      Gson gson = new Gson();
      Client c = new ElasticSearchClient();

      for(Class cl : toIndex) {
         for(Object src : MongoEntity.getDs().createQuery(cl).asList()) {
            String t = gson.toJson(src);
            Searchable casted = (Searchable) cl.cast(src);
            IndexJob indexJob = new IndexJob(casted, cl.getCanonicalName(), casted.getEntityId().toStringMongod());
            indexJob.doJobWithResult();
         }
      }

      c.close();
   }
}
