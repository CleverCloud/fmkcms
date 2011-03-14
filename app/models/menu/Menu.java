package models.menu;

import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import mongo.MongoEntity;
import org.bson.types.ObjectId;
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

   /**
    * @param name The name of the menu (play i18n key)
    */
   public Menu(String name) {
      this.name = name;
   }

   /**
    * Get the menu matching this name, or create it if it doesn't exist
    * @param name The name of the menu (play i18n key)
    * @return The Menu
    */
   public static Menu findOrCreateByName(String name) {
      if (name == null || name.isEmpty()) {
         return null;
      }
      Menu menu = Menu.findByName(name);
      if (menu == null) {
         menu = new Menu(name);
         menu.save();
      }
      return menu;
   }

   /**
    * Get the menu matching this name
    * @param name The name
    * @return The Menu
    */
   public static Menu findByName(String name) {
      if (name == null || name.isEmpty()) {
         return null;
      }
      return MongoEntity.getDs().find(Menu.class, "name", name).get();
   }

   /**
    * Add an item to the menu
    * @param item The item
    * @return self
    */
   public Menu addItem(MenuItem item) {
      if (this.items == null) {
         this.items = new ArrayList<MenuItem>();
      }
      this.items.add(item);
      return this.save();
   }

   /**
    * Remove an item from the menu
    * @param item The item
    * @return self
    */
   public Menu removeItem(MenuItem item) {
      item.delete();
      this.items.remove(item);
      return this.save();
   }

   /**
    * Get all the menus
    * @return The menus
    */
   public static List<Menu> findAll() {
      return MongoEntity.getDs().find(Menu.class).asList();
   }

   /**
    * Get all menu names
    * @return The menu names
    */
   public static Set<String> getAllNames() {
      Set<String> names = new TreeSet<String>();
      for (Menu menu : Menu.findAll()) {
         names.add(menu.name);
      }
      names.add("");
      return names;
   }

   /**
    * Check for recursion in the menus
    * @param item The menu item (with its submenu
    * @param menu The parent menu
    * @return True if no recursion has been found
    */
   public Boolean isTree(MenuItem item, Menu menu) {
      if (item == null) {
         return Boolean.TRUE;
      }
      for (MenuItem i : this.items) {
         if (i.menu != null && (i.menu.equals(menu) || !i.menu.isTree(item, menu))) {
            return Boolean.FALSE;
         }
      }
      return Boolean.TRUE;
   }

   /**
    * Get a menu by its id
    * @param id The id of the menu (MongodString)
    * @return The menu
    */
   public static Menu getByMongodStringId(String id) {

      return MongoEntity.getDs().get(Menu.class, new ObjectId(id));
   }

   /**
    * Get a menu by its id
    * @param id The id of the menu (ObjectId)
    * @return The menu
    */
   public static Menu getById(ObjectId id) {

      return MongoEntity.getDs().get(Menu.class, id);
   }

   /**
    * Delete a Menu by its name
    * @param name The name of the Menu
    */
   public static void delete(String name) {
      Menu menu = MongoEntity.getDs().find(Menu.class, "name", name).get();
      if (menu != null) {
         menu.delete();
      }
   }
}
