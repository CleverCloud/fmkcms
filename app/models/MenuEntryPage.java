package models;

import controllers.I18nController;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import play.mvc.Router;

/**
 *
 * @author waxzce
 */
@Entity
public class MenuEntryPage extends MenuEntry{

    public String urlid;

    public String getTitle() {
        PageRef pageRef = PageRef.getByUrlId(urlid);
        if (pageRef == null)
            return null;
        return pageRef.getPage(I18nController.getBrowserLanguages()).title;
    }

    public String getLink() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlId",urlid);
        return Router.reverse("PageController.page", map).url;
    }

}
