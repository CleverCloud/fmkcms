package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_Title extends MenuItem {

    public String title;
    
   

    public MenuItem_Title(String title, String displayStr, Menu menu) {
        //super(displayStr, menu); // Morphia failure with latest play
        this.displayStr = displayStr;
        this.menu = menu;
        this.title = title;
    }

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
      this.title = value;
   }
}
