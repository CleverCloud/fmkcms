/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elasticsearch;

import com.google.gson.Gson;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
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
        Settings s = ImmutableSettings.settingsBuilder().put("cluster.name", "fmkcms").build();

        Client c = new TransportClient(s).addTransportAddress(new InetSocketTransportAddress("localhost", 9301));
        System.out.println(s.get("cluster.name"));
        String t = gson.toJson(indexable);
        System.out.println(t);

        IndexResponse response = c.prepareIndex("fmkcms", indexname, id).setSource(t).execute().actionGet();
        c.close();
        return response.toString();
    }
}
