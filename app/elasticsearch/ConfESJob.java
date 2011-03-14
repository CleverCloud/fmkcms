package elasticsearch;

import com.google.gson.Gson;
import java.util.LinkedList;
import java.util.List;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.vfs.VirtualFile;

/**
 * Add mapping for all available Searchable class mappings.
 * The file for the class my.package.MyClass must have for path conf/es_mapping/my.package.MyClass.json
 *
 * @author waxzce
 * @author Julien Durillon
 */
@OnApplicationStart
public class ConfESJob extends Job {

   static Client c;
   static AdminClient admin;

   @Override
   public void doJob() throws Exception {

      ElasticSearchClient client = new ElasticSearchClient();
      admin = client.admin();

      ApplicationClasses appClasses = Play.classes;
      List<ApplicationClass> classes = appClasses.all();

      List<Class> toIndex = new LinkedList<Class>();

      Gson gson = new Gson();

      for (ApplicationClass acl : classes) {
         for (Class inter : acl.javaClass.getInterfaces()) {
            if (inter.equals(Searchable.class)) {
               this.addMapping(acl.javaClass.getCanonicalName());
               break;
            }
         }
      }

      client.close();
   }

   private void addMapping(String typeName) {
      String indexname = Play.configuration.getProperty("elasticsearch.indexname");
      VirtualFile vf = Play.getVirtualFile("conf/es_mapping/" + typeName + ".json");
      if (vf != null && vf.exists()) {
         PutMappingRequest pmr = new PutMappingRequest();
         pmr.indices(new String[]{indexname}).type(typeName).source(vf.contentAsString());
         admin.indices().putMapping(pmr);
      }
   }
}
