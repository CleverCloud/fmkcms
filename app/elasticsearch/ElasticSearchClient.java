/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elasticsearch;

import java.io.File;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import play.Play;

/**
 *
 * @author waxzce
 */
public class ElasticSearchClient extends TransportClient {

    public ElasticSearchClient() {
        super(
                ImmutableSettings.settingsBuilder().loadFromClasspath(
                Play.getVirtualFile(
                "conf" + System.getProperties().getProperty("file.separator") + "elasticsearch.json").getRealFile().getAbsolutePath()).build());
        this.addTransportAddress(
                new InetSocketTransportAddress(
                Play.configuration.getProperty("elasticsearch.host"),
                Integer.parseInt(Play.configuration.getProperty("elasticsearch.port"))));
    }
}
