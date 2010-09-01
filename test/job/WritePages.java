/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import java.io.File;
import play.Play;
import play.jobs.Job;

/**
 *
 * @author waxzce
 */
public class WritePages extends Job {

    @Override
    public void doJob() throws Exception {
        File f = Play.getFile("/test/data/pages.json");
        f.delete();
        f.createNewFile();

        /*FileWriter fw = new FileWriter(f);
        Gson gson = new Gson();

        fw.write(gson.toJson(Page.findAll()));
        fw.flush();*/
    }
    
}
