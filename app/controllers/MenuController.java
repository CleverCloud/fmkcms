package controllers;

import java.util.ArrayList;
import java.util.List;
import models.menu.Menu;
import models.menu.MenuItem;
import models.menu.items.MenuItem_ControllerChain;
import models.menu.items.MenuItem_LinkToPage;
import models.menu.items.MenuItem_OutgoingURL;
import models.menu.items.MenuItem_Title;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
@With(Secure.class)
public class MenuController extends Controller {

   public static void edit(String action, String name) {
      if (action.equals("delete"))
         Menu.delete(name);
      else {
         Menu menu = Menu.findOrCreateByName(name);
         render(action, menu);
      }
   }

   public static void newMenu() {
      MenuController.edit("create", null);
   }

   public static void doEdit(String action) {
      Menu menu = Menu.findOrCreateByName(params.get("menu.name"));
   }

   public static void addItem(String name) {
      List<String> types = new ArrayList<String>();
      types.add("ControllerChain");
      types.add("LinkToPage");
      types.add("OutgoingURL");
      types.add("Title");
      render(name, types);
   }

   public static void doAddItem(String name) {
      Menu menu = Menu.findOrCreateByName(name);
      MenuItem item;
      String type = params.get("item.type");
      if (type.equals("ControllerChain"))
         item = new MenuItem_ControllerChain(params.get("item.value"));
      else if (type.equals("LinkToPage"))
         item = new MenuItem_LinkToPage(params.get("item.value"));
      else if (type.equals("OutgoingURL"))
         item = new MenuItem_OutgoingURL(params.get("item.value"));
      else if (type.equals("Title"))
         item = new MenuItem_Title(params.get("item.value"));
      else
         return;
      item.save();
      menu.addItem(item);
   }

}
