package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_OutgoingURL extends MenuItem {

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
}
