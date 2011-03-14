package job;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import converter.MenuItemConverter;
import java.io.File;
import java.io.FileWriter;
import models.menu.Menu;
import models.menu.MenuItem;
import play.Play;
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

      Gson gson = new GsonBuilder().registerTypeAdapter(MenuItem.class, new MenuItemConverter()).serializeNulls().create();

      File f = new File(Play.applicationPath + File.separator + "data" + File.separator + "menus" + File.separator + JavaExtensions.slugify(menu.name, true) + ".json");
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
