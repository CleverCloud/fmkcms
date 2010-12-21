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
      Menu menu = Menu.findOrCreateByName(params.get("name"));
   }

   public static void addItem(String name) {
      render(name);
   }

   public static void doAddItem(String name) {
      Menu menu = Menu.findOrCreateByName(name);
   }

}
