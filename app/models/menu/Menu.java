package models.menu;

import com.google.code.morphia.annotations.Reference;
import controllers.PageViewer;
import java.util.ArrayList;
import java.util.List;
import models.Page;
import models.Page;
import models.PageRef;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
public class Menu extends MongoEntity {

   @Required
   public String name;

   @Reference
   public List<PageRef> items;

   private Menu(String name) {
      this.name = name;
   }

   public Menu addPage(Page page) {
      this.items.add(page.pageReference);
      return this;
   }

   public Menu addPage(PageRef pageRef) {
      this.items.add(pageRef);
      return this;
   }

   public List<Page> getPages() {
      List<Page> pages = new ArrayList<Page>();
      for (PageRef pr : this.items) {
         pages.add(PageViewer.getGoodPage(Page.getPagesByPageRef(pr)));
      }
      return pages;
   }

   public static Menu findOrCreateByName(String name) {
      if (name.isEmpty())
         return null;
      Menu menu = MongoEntity.getDs().find(Menu.class, "name", name).get();
      if (menu == null) {
         menu = new Menu(name);
         menu.save();
      }
      return menu;
   }

   public static void delete(String name) {
      Menu menu = MongoEntity.getDs().find(Menu.class, "name", name).get();
      if (menu != null)
         menu.delete();
   }
}
