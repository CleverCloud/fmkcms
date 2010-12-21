package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_Title extends MenuItem {

   public String title;

   public MenuItem_Title(String title, Menu menu) {
      super(menu);
      this.title = title;
   }

   public MenuItem_Title(String title) {
      this.title = title;
   }

   @Override
   public String getLink() {
      return "#";
   }

   @Override
   public String getDisplayStr() {
      return this.title;
   }

}
