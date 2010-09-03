package elasticsearch;

import com.google.gson.Gson;
import java.util.Iterator;
import models.Page;
import mongo.MongoEntity;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 *
 * @author waxzce
 *
 * this is a functionality disable for now but it can be fine to add search in all page
 *
 */
@OnApplicationStart
public class AddSearch extends Job {

    @Override
    public void doJob() throws Exception {
        Gson gson = new Gson();
        Client c = new ElasticSearchClient();

        Iterator<Page> itp = MongoEntity.getDs().createQuery(Page.class).asList().iterator();
        while (itp.hasNext()) {
            Page page = itp.next();
            String t = gson.toJson(page);
            IndexResponse response = c.prepareIndex(Play.configuration.getProperty("elasticsearch.indexname"), "page", page.id.toStringMongod()).setSource(t).execute().actionGet();


        }


        c.close();
    }
}
