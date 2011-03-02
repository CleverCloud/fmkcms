package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_Title extends MenuItem {

    public String title;

    /**
     * @param title The title
     * @param displayStr The string to display (play i18n key)
     * @param menu The subMenu belonging to this item
     */
    public MenuItem_Title(String title, String displayStr, Menu menu) {
        //super(displayStr, menu); // Morphia failure with latest play
        this.displayStr = displayStr;
        this.menu = menu;
        this.title = title;
    }

    /**
     * @param title The title
     * @param displayStr The string to display (play i18n key)
     */
    public MenuItem_Title(String title, String displayStr) {
        this.displayStr = displayStr;
        this.title = title;
    }

    @Override
    public String getLink() {
        return null;
    }

   @Override
   public String getValue() {
      return title;
   }

   @Override
   public String getType() {
      return "Title";
   }

   @Override
   public void setValue(String value) {
      if (value != null) {
         this.title = value;
      }
   }
}
