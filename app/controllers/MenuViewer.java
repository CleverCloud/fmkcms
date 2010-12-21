package controllers;

import models.menu.Menu;
import play.mvc.Controller;

/**
 *
 * @author keruspe
 */
public class MenuViewer extends Controller {

   public static void display(String name) {
      Menu menu = Menu.findOrCreateByName(name);
      render(menu);
   }

}
