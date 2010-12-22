package models.menu;

import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
public abstract class MenuItem extends MongoEntity {

   public Menu menu;
   public String displayStr;

   public MenuItem(String displayStr) {
      this.displayStr = displayStr;
   }

   public MenuItem(String displayStr, Menu menu) {
      this.displayStr = displayStr;
      this.menu = menu;
   }

   public abstract String getLink();

   public void setMenu(Menu menu) {
      if (menu.isTree(this))
         this.menu = menu;
   }

}
