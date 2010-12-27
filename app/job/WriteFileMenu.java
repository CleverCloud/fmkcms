/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import models.menu.Menu;
import org.bson.types.ObjectId;
import play.Play;
import play.exceptions.JavaException;
import play.jobs.Job;
import play.templates.JavaExtensions;

/**
 *
 * @author waxzce
 */
public class WriteFileMenu extends Job {

    private Menu menu;

    public WriteFileMenu(Menu menu) {
        this.menu = menu;
    }

    @Override
    public void doJob() throws Exception {

        Gson gson = new GsonBuilder().create();

        File f = Play.getFile("data/menus/" + JavaExtensions.slugify(menu.name, true) + ".json");
        f.mkdirs();
        if (f.exists()) {
            f.delete();
        }
        f.createNewFile();
        FileWriter fw = new FileWriter(f);
        fw.write(gson.toJson(menu));
        fw.close();

    }
}
