/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.menu.MenuItem;

/**
 *
 * @author waxzce
 */
public class MenuItemConverter implements JsonDeserializer<MenuItem> {

    public MenuItem deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        try {
            Class c = Class.forName(je.getAsJsonObject().get("classname").getAsString());
            Object obj = c.getConstructor(new Class[]{}).newInstance();
            Iterator<Entry<String, JsonElement>> it = je.getAsJsonObject().entrySet().iterator();
            Gson gson = new Gson();
            while (it.hasNext()) {
                Entry<String, JsonElement> entry = it.next();
                if (entry.getKey().equals("classname") || entry.getKey().equals("id")) {
                    continue;
                }
                Field f = c.getField(entry.getKey());
                f.set(obj, gson.fromJson(entry.getValue(), f.getType()));
            }
            return (MenuItem) obj;
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MenuItemConverter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return null;
    }
}
