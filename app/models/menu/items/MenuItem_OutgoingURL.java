package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;
import play.data.validation.URL;

/**
 *
 * @author keruspe
 */
public class MenuItem_OutgoingURL extends MenuItem {

   @URL
   public String url;

   /**
    * @param url The url
    * @param displayStr The string to display (play i18n key)
    * @param menu The subMenu belonging to this item
    */
   public MenuItem_OutgoingURL(String url, String displayStr, Menu menu) {
      this.displayStr = displayStr;
      this.menu = menu;
      this.url = url;
   }

   /**
    * @param url The url
    * @param displayStr The string to display (play i18n key)
    */
   public MenuItem_OutgoingURL(String url, String displayStr) {
      this.displayStr = displayStr;
      this.url = url;
   }

   @Override
   public String getLink() {
      return this.url;
   }

   @Override
   public String getValue() {
      return url;
   }

   @Override
   public String getType() {
      return "OutgoingURL";
   }

   @Override
   public void setValue(String value) {
      if (value != null) {
         this.url = value;
      }
   }
}
