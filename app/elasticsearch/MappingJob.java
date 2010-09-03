package elasticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.cluster.action.index.MappingUpdatedAction.MappingUpdatedRequest;
import org.elasticsearch.common.compress.CompressedString;
import org.elasticsearch.common.xcontent.builder.XContentBuilder;
import play.Play;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.vfs.VirtualFile;

/**
 *
 * @author waxzce
 */
@OnApplicationStart
@Every("5d")
public class MappingJob extends Job {

    @Override
    public void doJob() throws Exception {
        Client c = new ElasticSearchClient();
        AdminClient admin = c.admin();

        VirtualFile vf = Play.getVirtualFile("conf/es_mapping/page.json");

        PutMappingRequest pmr = new PutMappingRequest();
        pmr.indices(new String[]{Play.configuration.getProperty("elasticsearch.indexname")}).type("page").source(vf.contentAsString());

        admin.indices().putMapping(pmr);
        c.close();
    }
}
