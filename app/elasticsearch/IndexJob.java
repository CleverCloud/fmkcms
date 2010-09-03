package elasticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import play.jobs.Job;

/**
 *
 * @author waxzce
 */
public class IndexJob extends Job<String> {

    private Object indexable;
    private String indexname;
    private String id;

    public IndexJob(Object indexable, String indexname, String id) {
        this.indexable = indexable;
        this.indexname = indexname;
        this.id = id;
    }

    @Override
    public String doJobWithResult() throws Exception {
        Gson gson = new Gson();
        Client c = new ElasticSearchClient();
        String t = gson.toJson(indexable);
        IndexResponse response = c.prepareIndex("fmkcms", indexname, id).setSource(t).execute().actionGet();
        c.close();
        return response.toString();
    }
    
}
