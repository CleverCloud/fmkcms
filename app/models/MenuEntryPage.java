package models;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import play.mvc.Router;

/**
 *
 * @author waxzce
 */
@Entity
public class MenuEntryPage extends MenuEntry {

    public String urlid;

    public String getTitle() {
        Page page = Page.getByUrlId(urlid);
        if (page == null)
            return null;
        return page.title;
    }

    public String getLink() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlId",urlid);
        return Router.reverse("PageController.page", map).url;
    }

}
