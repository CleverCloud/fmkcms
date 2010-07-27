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
import java.util.Set;
import java.util.TreeSet;
import models.Tag;

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
                Set<Tag> st = new TreeSet<Tag>();
                for (Iterator<Tag> it1 = page.tags.iterator(); it1.hasNext();) {
                    Tag tag = it1.next();
                    st.add(Tag.findOrCreateByName(tag.name));
                }
                page.tags = st;
                page.save();
            }
        }
    }
}
