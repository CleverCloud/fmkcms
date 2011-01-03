package models.menu.items;

import java.util.HashMap;
import java.util.Map;
import models.menu.Menu;
import models.menu.MenuItem;
import play.mvc.Router;

/**
 *
 * @author keruspe
 */
public class MenuItem_LinkToPage extends MenuItem {

    public String urlId;

    public MenuItem_LinkToPage(String urlId, String displayStr, Menu menu) {
        //super(displayStr, menu); // Morphia failure with latest play
        this.displayStr = displayStr;
        this.menu = menu;
        this.urlId = urlId;
    }

    public MenuItem_LinkToPage(String urlId, String displayStr) {
        //super(displayStr);
        this.displayStr = displayStr;
        this.urlId = urlId;
    }

    @Override
    public String getLink() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlId", urlId);
        return Router.reverse("PageViewer.page", map).url;
    }
}
