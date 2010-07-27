/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import com.google.gson.Gson;
import java.io.FileReader;
import java.util.List;
import models.Page;
import org.yaml.snakeyaml.Yaml;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Iterator;

/**
 *
 * @author waxzce
 */
@OnApplicationStart
public class InsertPages extends Job {

    @Override
    public void doJob() throws Exception {
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<Page>>() {
        }.getType();
        List<Page> lp = gson.fromJson(new FileReader(Play.applicationPath + "/test/data/pages.json"), collectionType);
        if (lp != null) {
            for (Iterator<Page> it = lp.iterator(); it.hasNext();) {
                Page page = it.next();
                page.id = null;
                page.save();
            }
        }
    }
}
