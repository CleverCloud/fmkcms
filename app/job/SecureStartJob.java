/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import com.google.gson.Gson;
import java.io.FileReader;
import java.util.ArrayList;
import models.secure.SecurityLevels;
import org.joda.time.tz.UTCProvider;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

/**
 *
 * @author waxzce
 */
@OnApplicationStart
public class SecureStartJob extends Job {

    @Override
    public void doJob() throws Exception {
        Gson gson = new Gson();
        String separator = System.getProperties().getProperty("file.separator");
        String path = Play.applicationPath + separator + "conf" + separator + "SecurityLevels.json";
        SecurityLevels sl = gson.fromJson(new FileReader(path), SecurityLevels.class);
        
    }
}
