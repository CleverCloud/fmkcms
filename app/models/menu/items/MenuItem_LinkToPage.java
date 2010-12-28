package models.menu.items;

import java.util.HashMap;
import java.util.Map;
import models.menu.Menu;
import models.menu.MenuItem;
import play.mvc.Router;
import com.google.code.morphia.annotations.Transient;

/**
 *
 * @author keruspe
 */
public class MenuItem_LinkToPage extends MenuItem {

    public String urlId;

    public MenuItem_LinkToPage(String urlId, String displayStr, Menu menu) {
        super(displayStr, menu);
        this.urlId = urlId;
    }

    public MenuItem_LinkToPage(String urlId, String displayStr) {
        super(displayStr);
        this.urlId = urlId;
    }

    @Override
    public String getLink() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlId", urlId);
        return Router.reverse("PageViewer.page", map).url;
    }
}
