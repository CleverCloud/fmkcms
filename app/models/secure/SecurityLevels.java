/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.secure;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *s
 * @author waxzce
 */
public class SecurityLevels {

    public Map<String, List<String>> securityLevels = new HashMap<String, List<String>>();

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
