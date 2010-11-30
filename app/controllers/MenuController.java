package controllers;

import models.Menu;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
@With(Secure.class)
public class MenuController extends Controller {

   public static void deleteMenu(String name) {
      Menu.delete(name);
   }

   public static void editMenu(String name) {
      Menu menu = Menu.findOrCreateByName(name);
      render(menu);
   }

}
