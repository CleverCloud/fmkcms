package job;

import com.google.gson.Gson;
import java.io.FileReader;
import java.util.List;
import models.Page;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
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
        Type collectionType = new TypeToken<List<Page>>() {}.getType();
        List<Page> lp = gson.fromJson(new FileReader(Play.applicationPath + "/test/data/pages.json"), collectionType);
        if (lp != null) {
            for (Page page : lp) {
                page.id = null;
                Set<Tag> st = new TreeSet<Tag>();
                for (Tag tag : page.pageReference.tags) {
                    st.add(Tag.findOrCreateByName(tag.name));
                }
                page.pageReference.tags = st;
                page.pageReference.save();
                page.save();
            }
        }
    }
    
}
