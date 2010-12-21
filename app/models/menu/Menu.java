package models.menu;

import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
public class Menu extends MongoEntity {

   @Required
   public String name;

   @Reference
   public List<MenuItem> items;

   public Menu(String name) {
      this.name = name;
   }

   public static Menu findOrCreateByName(String name) {
      Menu menu = Menu.findByName(name);
      if (menu == null) {
         menu = new Menu(name);
         menu.save();
      }
      return menu;
   }

   public static Menu findByName(String name) {
      if (name.isEmpty())
         return null;
      return MongoEntity.getDs().find(Menu.class, "name", name).get();
   }

   public static void delete(String name) {
      Menu menu = MongoEntity.getDs().find(Menu.class, "name", name).get();
      if (menu != null)
         menu.delete();
   }

   public Menu addItem(MenuItem item) {
      if (this.items == null)
         this.items = new ArrayList<MenuItem>();
      this.items.add(item);
      return this.save();
   }

   public Menu removeItem(MenuItem item) {
      this.items.remove(item);
      return this.save();
   }

}
