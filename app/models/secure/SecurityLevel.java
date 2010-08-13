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
        return (new Gson()).toJson(this);
    }


}
