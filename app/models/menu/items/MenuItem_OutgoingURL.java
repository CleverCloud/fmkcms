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

    public MenuItem_OutgoingURL(String url, String displayStr, Menu menu) {
        //super(displayStr, menu); // Morphia failure with latest play
        this.displayStr = displayStr;
        this.menu = menu;
        this.url = url;
    }

    public MenuItem_OutgoingURL(String url, String displayStr) {
        //super(displayStr);
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
      this.url = value;
   }
}
