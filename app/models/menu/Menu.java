package models.menu;

import com.google.code.morphia.annotations.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
      if (name == null || name.isEmpty())
         return null;
      Menu menu = Menu.findByName(name);
      if (menu == null) {
         menu = new Menu(name);
         menu.save();
      }
      return menu;
   }

   public static Menu findByName(String name) {
      if (name == null || name.isEmpty())
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

   public static List<Menu> findAll() {
      return MongoEntity.getDs().find(Menu.class).asList();
   }

   public static Set<String> getAllNames() {
      Set<String> names = new TreeSet<String>();
      for (Menu menu : Menu.findAll())
         names.add(menu.name);
      names.add("");
      return names;
   }

   public Boolean isTree(MenuItem item) {
      if (item == null)
         return Boolean.TRUE;
      for (MenuItem i : this.items) {
         if(i.equals(item))
            return Boolean.FALSE;
         if (i.menu != null && !i.menu.isTree(item))
            return Boolean.FALSE;
      }
      return Boolean.TRUE;
   }

}
