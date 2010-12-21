package controllers;

import models.menu.Menu;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
@With(Secure.class)
public class MenuController extends Controller {

   public static void editMenu(String action, String name) {
      Menu menu = Menu.findOrCreateByName(name);
      render(action, menu);
   }

   public static void newMenu() {
      MenuController.editMenu("create", null);
   }

   public static void edit(String name) {
      MenuController.editMenu("edit", name);
   }

   public static void delete(String name) {
      Menu.delete(name);
   }

   public static void doEditMenu(String action) {
      String name = params.get("name");
      Menu menu = Menu.findOrCreateByName(name);
      /* TODO: edit menus */
      menu.save();
   }

}
