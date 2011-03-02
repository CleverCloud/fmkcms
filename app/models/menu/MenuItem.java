package models.menu;

import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;
import models.menu.items.MenuItem_ControllerChain;
import models.menu.items.MenuItem_LinkToPage;
import models.menu.items.MenuItem_OutgoingURL;
import models.menu.items.MenuItem_Title;
import mongo.MongoEntity;
import org.bson.types.ObjectId;
import play.data.validation.Required;
import play.i18n.Messages;

/**
 *
 * @author keruspe
 */
public abstract class MenuItem extends MongoEntity {

   @Reference
   public Menu menu;

   @Required
   public String displayStr;

   public String cssLinkClass;

   @Transient
   private String classname = getClass().getCanonicalName();

   public MenuItem() {
   }

   public String getClassname() {
      return classname;
   }

   public MenuItem(String displayStr) {
      this.displayStr = displayStr;
   }

   public MenuItem(String displayStr, Menu menu) {
      this.displayStr = displayStr;
      this.menu = menu;
   }

   /**
    * @return The link to put in an anchor
    */
   public abstract String getLink();

   /**
    * Set the menu as submenu from this item if no recursion is found
    * @param menu The submenu
    * @param parent The parent menu
    */
   public void setMenu(Menu menu, Menu parent) {
      if (menu != null && menu.isTree(this, parent)) {
         this.menu = menu;
      }
   }

   /**
    * @param type The type of the menuItem
    * @param id The id of the menuItem
    * @return The MenuItem
    */
   public static MenuItem getByMongodStringId(String type, String id) {

      if (type.equals("ControllerChain")) {
         return MongoEntity.getDs().get(MenuItem_ControllerChain.class, new ObjectId(id));
      } else if (type.equals("LinkToPage")) {
         return MongoEntity.getDs().get(MenuItem_LinkToPage.class, new ObjectId(id));
      } else if (type.equals("OutgoingURL")) {
         return MongoEntity.getDs().get(MenuItem_OutgoingURL.class, new ObjectId(id));
      } else if (type.equals("Title")) {
         return MongoEntity.getDs().get(MenuItem_Title.class, new ObjectId(id));
      } else {
         return null;
      }
   }

   /**
    * Transforms the displayStr of the item into a play i18n key
    * @return The play i18n value
    */
   public String getDisplayStr() {
      Object[] dummy = new Object[]{};
      return Messages.get(this.displayStr, dummy);
   }

   /**
    * Get the value of the item (controllerChain, title, ...)
    * @return The value
    */
   public abstract String getValue();

   /**
    * Set the value of the item (controllerChain, title, ...)
    * @param value The value
    */
   public abstract void setValue(String value);

   /**
    * Get the type of the item
    * @return The type ("ControllerChain", "Title", ...)
    */
   public abstract String getType();
}
