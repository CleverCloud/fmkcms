package elasticsearch;

import com.google.gson.Gson;
import java.util.List;
import models.Page;
import mongo.MongoEntity;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.xcontent.QueryStringQueryBuilder;
import org.elasticsearch.index.query.xcontent.TermQueryBuilder;
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

        SearchResponse response = c.prepareSearch(Play.configuration.getProperty("elasticsearch.indexname"))
        .setSearchType(SearchType.DEFAULT)
        .setQuery(qsqb.buildAsBytes())
        .execute()
        .actionGet();



        return new Gson().toJson(response);
    }


}
