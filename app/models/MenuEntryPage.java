/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class MenuEntryPage extends MenuEntry{

    public String urlid;

    public String getTitle() {
        return Page.getByUrlId(urlid).title;
    }

    public String getLink() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("urlId",urlid);
        return Router.reverse("PageController.page", map).url;
    }

}
