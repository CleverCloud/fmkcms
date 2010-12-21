package elasticsearch;

import com.google.gson.Gson;
import java.util.Iterator;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import play.Logger;
import play.Play;
import play.jobs.Job;

/**
 *
 * @author waxzce
 */
public class SearchJob extends Job<String> {

    private String query;

    public SearchJob(String query) {
        this.query = query;
    }

    @Override
    public String doJobWithResult() throws Exception {
        Client c = new ElasticSearchClient();

        QueryStringQueryBuilder qsqb = new QueryStringQueryBuilder(query);

        SearchResponse response = c.prepareSearch(Play.configuration.getProperty("elasticsearch.indexname")).setSearchType(SearchType.DEFAULT).setQuery(qsqb).execute().actionGet();

        Iterator<SearchHit> its = response.hits().iterator();

        while (its.hasNext()) {
            SearchHit searchHit = its.next();
            System.out.println(searchHit.getType());
            try {
                System.out.println(Class.forName(searchHit.getType()).getMethod("getFrom", new Class[]{SearchHit.class}).invoke(Class.forName(searchHit.getType()), new Object[]{searchHit}));
            } catch (ClassNotFoundException ex) {
                Logger.debug("no CRUDFieldProvider found for %s", ex);
            }
        }

        return new Gson().toJson(response);
    }
}
