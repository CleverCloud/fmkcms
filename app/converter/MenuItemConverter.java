/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package converter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import models.menu.MenuItem;

/**
 *
 * @author waxzce
 */
public class MenuItemConverter implements JsonDeserializer<MenuItem> {

    public MenuItem deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        System.out.println("fkjsqhfkslqhfj");
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
