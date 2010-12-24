package controllers;

import java.util.ArrayList;
import java.util.List;
import models.menu.Menu;
import models.menu.MenuItem;
import models.menu.items.MenuItem_ControllerChain;
import models.menu.items.MenuItem_LinkToPage;
import models.menu.items.MenuItem_OutgoingURL;
import models.menu.items.MenuItem_Title;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
//@With(Secure.class)
public class MenuController extends Controller {

    public static void list() {
        List<Menu> menus = Menu.findAll();
        render(menus);
    }

    public static void edit(String id) {
        Menu menu = Menu.getByMongodStringId(id);
        render(menu);
    }
    public static void edit_end(String id) {
        Menu menu = Menu.getByMongodStringId(id);
        menu.name = params.get("menu.name");
        menu.save();
        list();
    }

    public static void delete(String id) {
        Menu.getByMongodStringId(id).delete();
        list();
    }

    public static void newMenu() {
        render();
    }

    public static void newMenu_end() {
        Menu.findOrCreateByName(params.get("menu.name").replaceAll("[ #\\.]", "-"));
        list();
    }

    public static void addItem(String name) {
        if (name == null || name.isEmpty()) {
            notFound();
        }
        List<String> types = new ArrayList<String>();
        types.add("ControllerChain");
        types.add("LinkToPage");
        types.add("OutgoingURL");
        types.add("Title");
        render(name, types);
    }

    public static void doAddItem(String name) {
        Menu menu = Menu.findOrCreateByName(name);
        MenuItem item;
        String type = params.get("item.type");
        String value = params.get("item.value");
        String displayStr = params.get("item.display");

        if (displayStr == null || displayStr.isEmpty()) {
            displayStr = value;
        }

        if (type.equals("ControllerChain")) {
            item = new MenuItem_ControllerChain(value, displayStr);
        } else if (type.equals("LinkToPage")) {
            item = new MenuItem_LinkToPage(value, displayStr);
        } else if (type.equals("OutgoingURL")) {
            item = new MenuItem_OutgoingURL(value, displayStr);
        } else if (type.equals("Title")) {
            item = new MenuItem_Title(value, displayStr);
        } else {
            return;
        }

        item.setMenu(Menu.findByName(params.get("item.subMenu")), menu);
        item.cssLinkClass = params.get("item.cssLink");
        item.save();
        menu.addItem(item);
    }
}
