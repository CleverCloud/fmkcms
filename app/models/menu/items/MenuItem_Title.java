package models.menu.items;

import com.google.code.morphia.annotations.PostLoad;
import com.google.code.morphia.annotations.Transient;
import models.menu.Menu;
import models.menu.MenuItem;

/**
 *
 * @author keruspe
 */
public class MenuItem_Title extends MenuItem {

    public String title;
    
   

    public MenuItem_Title(String title, String displayStr, Menu menu) {
        super(displayStr, menu);
        this.title = title;
    }

    public MenuItem_Title(String title, String displayStr) {
        super(displayStr);
        this.title = title;
    }

    @Override
    public String getLink() {
        return null;
    }
}
