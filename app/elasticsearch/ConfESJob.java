package elasticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.cluster.action.index.MappingUpdatedAction.MappingUpdatedRequest;
import org.elasticsearch.common.compress.CompressedString;
import org.elasticsearch.common.xcontent.builder.XContentBuilder;
import play.Logger;
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
public class ConfESJob extends Job {

    static Client c;
    static AdminClient admin;

    @Override
    public void doJob() throws Exception {
        c = new ElasticSearchClient();
        admin = c.admin();

        IndicesStatusRequest isr = new IndicesStatusRequest(Play.configuration.getProperty("elasticsearch.indexname"));
        admin.indices().status(isr, new StatusResponse());

        //      c.close();

    }

    private class StatusResponse implements ActionListener<IndicesStatusResponse> {

        public StatusResponse() {
        }

        public void onFailure(Throwable thrwbl) {
            System.out.println("StatusResponseonFailure");


            String indexname = Play.configuration.getProperty("elasticsearch.indexname");
            CreateIndexRequest cir = new CreateIndexRequest(indexname);

            admin.indices().create(cir, (ActionListener) new MappingResponse());

        }

        public void onResponse(IndicesStatusResponse rspns) {
            System.out.println("StatusResponseonResponse");

            MappingResponse mr = new MappingResponse();
            mr.onResponse(null);
        }
    }

    private class MappingResponse implements ActionListener<CreateIndexRequest> {

        public MappingResponse() {
        }

        public void onResponse(CreateIndexRequest rspns) {
            System.out.println("mapping reponse");
            addMapping("page", "page.json");
        }

        public void onFailure(Throwable thrwbl) {
            System.out.println("mapping fail");
            onResponse(null);
            Logger.error("Elastic Search Failure : create %s index faillure server %s:%s", new String[]{Play.configuration.getProperty("elasticsearch.indexname"), Play.configuration.getProperty("elasticsearch.host"), Play.configuration.getProperty("elasticsearch.port")});
        }

        private void addMapping(String nametype, String filename) {
            String indexname = Play.configuration.getProperty("elasticsearch.indexname");
            VirtualFile vf = Play.getVirtualFile("conf/es_mapping/" + filename);
            System.out.println(vf.contentAsString());
            PutMappingRequest pmr = new PutMappingRequest();
            pmr.indices(new String[]{indexname}).type(nametype).source(vf.contentAsString());
            admin.indices().putMapping(pmr);
        }
    }
}
