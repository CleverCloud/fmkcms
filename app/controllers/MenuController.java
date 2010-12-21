package controllers;

import java.util.ArrayList;
import java.util.List;
import models.menu.Menu;
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
   }

}
