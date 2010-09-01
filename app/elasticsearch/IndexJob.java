/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elasticsearch;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.InputStreamStreamInput;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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

    public IndexJob(Object indexable, String indexname, String id) {
        this.indexable = indexable;
        this.indexname = indexname;
        this.id = id;
    }

    @Override
    public String doJobWithResult() throws Exception {
        Gson gson = new Gson();
        String separator = System.getProperties().getProperty("file.separator");
        
        File conffile = Play.getVirtualFile("conf" + separator + "elasticsearch.json").getRealFile();
        //Settings s = ImmutableSettings.readSettingsFromStream(new InputStreamStreamInput(new FileInputStream(conffile)));
        Settings s = ImmutableSettings.settingsBuilder().loadFromClasspath(conffile.getAbsolutePath()).build();

        Client c = new TransportClient(s).addTransportAddress(new InetSocketTransportAddress(Play.configuration.getProperty("elasticsearch.host"), Integer.parseInt(Play.configuration.getProperty("elasticsearch.port"))));
        String t = gson.toJson(indexable);

        IndexResponse response = c.prepareIndex("fmkcms", indexname, id).setSource(t).execute().actionGet();
        c.close();
        return response.toString();
    }
}
