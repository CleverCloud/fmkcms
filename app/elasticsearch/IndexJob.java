package elasticsearch;

import com.google.gson.ExclusionStrategy;
import com.google.gson.GsonBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import play.Logger;
import play.Play;
import play.jobs.Job;

/**
 *
 * @author waxzce
 */
public class IndexJob extends Job<String> {

   private Object indexable;

   private String indexname;

   private String id;

   private final ExclusionStrategy[] strategies;

   public IndexJob(Object indexable, String indexname, String id) {
      this.indexable = indexable;
      this.indexname = indexname;
      this.id = id;
      this.strategies = new ExclusionStrategy[]{};
   }

   public IndexJob(Object indexable, String indexname, String id, ExclusionStrategy... strategies) {
      this.indexable = indexable;
      this.indexname = indexname;
      this.id = id;
      this.strategies = strategies;
   }

   @Override
   public String doJobWithResult() throws Exception {

      Client client = new ElasticSearchClient();
      String source = new GsonBuilder().setExclusionStrategies(strategies).create().toJson(indexable);

      IndexResponse response = client.prepareIndex(Play.configuration.getProperty("elasticsearch.indexname"), indexname, id).setSource(source).execute().actionGet();
      client.close();
      Logger.info(response.type());
      return response.toString();
   }
}
