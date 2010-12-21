package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;
import play.mvc.Router;

/**
 *
 * @author keruspe
 */
public class MenuItem_ControllerChain extends MenuItem {

   public String controllerChain;

   public MenuItem_ControllerChain(String controllerChain, Menu menu) {
      super(menu);
      this.controllerChain = controllerChain.trim();
   }

   public MenuItem_ControllerChain(String controllerChain) {
      this.controllerChain = controllerChain.trim();
   }

   @Override
   public String getLink() {
      return Router.reverse(this.controllerChain).url;
   }

   @Override
   public String getDisplayStr() {
      return this.controllerChain;
   }

}
