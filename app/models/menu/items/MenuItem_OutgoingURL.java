package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_OutgoingURL extends MenuItem {

   public String url;

   public MenuItem_OutgoingURL(String url, Menu menu) {
      super(menu);
      this.url = url;
   }

   public MenuItem_OutgoingURL(String url) {
      this.url = url;
   }

   @Override
   public String getLink() {
      return this.url;
   }

   @Override
   public String getDisplayStr() {
      return this.url;
   }

}
