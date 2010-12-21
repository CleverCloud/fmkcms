package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_ControllerChain extends MenuItem {

   public String controllerChain;

   public MenuItem_ControllerChain(String controllerChain, Menu menu) {
      super(menu);
      this.controllerChain = controllerChain;
   }

   public MenuItem_ControllerChain(String controllerChain) {
      this.controllerChain = controllerChain;
   }

   @Override
   public String getLink() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

}
