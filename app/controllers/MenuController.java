package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import converter.MenuItemConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import job.WriteFileMenu;
import models.menu.Menu;
import models.menu.MenuItem;
import models.menu.items.MenuItem_ControllerChain;
import models.menu.items.MenuItem_LinkToPage;
import models.menu.items.MenuItem_OutgoingURL;
import models.menu.items.MenuItem_Title;
import play.Logger;
import play.Play;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;
import controllers.secure.Secure;


/**
 * @author keruspe
 */
@With(Secure.class)
public class MenuController extends Controller {

   /**
    * List all the Menus
    */
   public static void list() {
      List<Menu> menus = Menu.findAll();
      Map<String, VirtualFile> mapOfMenusFiles = new HashMap<String, VirtualFile>();

      for (Entry<String, VirtualFile> entry : Play.modules.entrySet()) {
         for (VirtualFile vf : entry.getValue().child("data/menus/").list()) {
            mapOfMenusFiles.put(entry.getValue().getName() + " - " + vf.getName(), vf);
         }
      }

      for (VirtualFile vf : Play.getVirtualFile("data/menus/").list()) {
         mapOfMenusFiles.put(Play.configuration.getProperty("application.name") + " - " + vf.getName(), vf);
      }
      render(menus, mapOfMenusFiles);
   }

   /**
    * Edit a Menu
    *
    * @param id The id of the Menu
    */
   public static void edit(String id) {
      Menu menu = Menu.getByMongodStringId(id);
      if (menu == null) {
         notFound();
      }
      render(menu);
   }

   /**
    * End the edition of the Menu
    *
    * @param id The id of the Menu
    */
   public static void edit_end(String id) {
      Menu menu = Menu.getByMongodStringId(id);
      if (menu == null) {
         notFound();
      }
      menu.name = params.get("menu.name");
      menu.save();
      list();
   }

   /**
    * Delete a Menu
    *
    * @param id The id of the Menu
    */
   public static void delete(String id) {
      Menu.getByMongodStringId(id).delete();
      list();
   }

   /**
    * Create a new Menu
    */
   public static void newMenu() {
      render();
   }

   /**
    * Finish creation of the Menu
    */
   public static void newMenu_end() {
      Menu.findOrCreateByName(params.get("menu.name").replaceAll("[ #\\.]", "-"));
      list();
   }

   /**
    * Add a menuItem to the Menu
    *
    * @param id The id of the Menu
    */
   public static void addItem(String id) {
      Menu menu = Menu.getByMongodStringId(id);
      if (menu == null) {
         notFound();
      }
      List<String> types = new ArrayList<String>();
      types.add("ControllerChain");
      types.add("LinkToPage");
      types.add("OutgoingURL");
      types.add("Title");
      render(menu, types);
   }

   /**
    * Apply the menuItem addition
    *
    * @param id The id of the Menu
    */
   public static void doAddItem(String id) {
      Menu menu = Menu.getByMongodStringId(id);
      if (menu == null) {
         notFound();
      }
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
      validation.valid(item);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep();
         addItem(id);
      }
      item.setMenu(Menu.findByName(params.get("item.subMenu")), menu);
      item.cssLinkClass = params.get("item.cssLink");
      item.save();
      menu.addItem(item);
      edit(id);
   }

   /**
    * Edit a MenuItem
    *
    * @param type       The type of the item ("ControllerChain, Title, ...)
    * @param idMenu     The id of the Menu
    * @param idMenuItem The id of the MenuItem
    */
   public static void editItem(String type, String idMenu, String idMenuItem) {
      MenuItem item = MenuItem.getByMongodStringId(type, idMenuItem);
      if (item == null) {
         notFound();
      }
      List<String> types = new ArrayList<String>();
      types.add("ControllerChain");
      types.add("LinkToPage");
      types.add("OutgoingURL");
      types.add("Title");
      render(idMenu, item, types);
   }

   /**
    * Apply the MenuItem edition
    *
    * @param type   The type of the item ("ControllerChain, Title, ...)
    * @param idMenu The id of the Menu
    * @param id     The id of the MenuItem
    */
   public static void doEditItem(String type, String idMenu, String id) {
      MenuItem item = MenuItem.getByMongodStringId(type, id);
      Menu menu = Menu.getByMongodStringId(idMenu);
      if (item == null || menu == null) {
         notFound();
      }
      String value = params.get("item.value");
      String displayStr = params.get("item.display");

      if (displayStr == null || displayStr.isEmpty()) {
         displayStr = value;
      }
      item.setMenu(Menu.findByName(params.get("item.subMenu")), menu);
      item.cssLinkClass = params.get("item.cssLink");
      item.setValue(value);
      if (displayStr != null) {
         item.displayStr = displayStr;
      }
      validation.valid(item);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep();
         editItem(type, idMenu, id);
      }
      item.refresh().save();
      edit(idMenu);
   }

   /**
    * Remove a menuItem
    *
    * @param type       The type of the item ("ControllerChain, Title, ...)
    * @param idMenu     The id of the Menu
    * @param idMenuItem The id of the MenuItem
    */
   public static void removeItem(String type, String idMenu, String idMenuItem) {
      Menu menu = Menu.getByMongodStringId(idMenu);
      MenuItem item = MenuItem.getByMongodStringId(type, idMenuItem);
      if (menu == null || item == null) {
         notFound();
      }
      menu.removeItem(item);
      edit(idMenu);
   }

   /**
    * Write the Menu to a file (as a JSON String)
    *
    * @param id The id of the Menu
    */
   public static void writeMenuFile(String id) {
      (new WriteFileMenu(Menu.getByMongodStringId(id))).now();
      list();
   }

   /**
    * Import a menu from a JSON file
    *
    * @param path The path to the file
    */
   public static void importMenuFromFile(String path) {
      Gson gson = new GsonBuilder().registerTypeAdapter(MenuItem.class, new MenuItemConverter()).create();
      try {
         Menu menu = gson.fromJson(new FileReader(VirtualFile.fromRelativePath(path).getRealFile()), Menu.class);
         menu.save();
      } catch (FileNotFoundException e) {
         Logger.error(e.getLocalizedMessage(), new Object[]{});

      }
      list();
   }

   /**
    * View the content of a JSON menu file
    *
    * @param path The path to the file
    * @throws FileNotFoundException
    */
   public static void viewMenuFromFile(String path) throws FileNotFoundException {
      try {
         InputStream json = new FileInputStream(VirtualFile.fromRelativePath(path).getRealFile());
         renderBinary(json);
      } catch (com.google.gson.JsonParseException e) {
         Logger.error(e.getLocalizedMessage(), new Object[]{});
         renderText((Play.getVirtualFile(path).contentAsString()));
      }
   }
}
