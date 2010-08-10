/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.secure;

import com.google.gson.Gson;
import java.util.List;

/**
 *s
 * @author waxzce
 */
public class SecurityLevel {

    public String name;
    public List<String> controllers;

    
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


}
