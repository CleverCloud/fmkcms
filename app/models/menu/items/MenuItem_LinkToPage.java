package models.menu.items;

import java.util.HashMap;
import java.util.Map;
import models.menu.Menu;
import models.menu.MenuItem;
import play.mvc.Router;
import val.PageUrlID;

/**
 *
 * @author keruspe
 */
public class MenuItem_LinkToPage extends MenuItem {

   @PageUrlID
   public String urlId;

   /**
    * @param urlId The urlId of the page
    * @param displayStr The string to display (play i18n key)
    * @param menu The subMenu belonging to this item
    */
   public MenuItem_LinkToPage(String urlId, String displayStr, Menu menu) {
      this.displayStr = displayStr;
      this.menu = menu;
      this.urlId = urlId;
   }

   /**
    * @param urlid The urlid of the page
    * @param displayStr The string to display (play i18n key)
    */
   public MenuItem_LinkToPage(String urlId, String displayStr) {
      this.displayStr = displayStr;
      this.urlId = urlId;
   }

   @Override
   public String getLink() {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("urlId", urlId);
      return Router.reverse("PageViewer.page", map).url;
   }

   @Override
   public String getValue() {
      return urlId;
   }

   @Override
   public String getType() {
      return "LinkToPage";
   }

   @Override
   public void setValue(String value) {
      if (value != null) {
         this.urlId = value;
      }
   }
}
