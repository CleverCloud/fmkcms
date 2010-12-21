package models.menu;

import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
public abstract class MenuItem extends MongoEntity {

   Menu menu;

   public MenuItem() {}

   public MenuItem(Menu menu) {
      this.menu = menu;
   }

   public abstract String getLink();

}
