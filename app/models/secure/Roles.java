package models.secure;

import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author waxzce
 */
public class Roles {

    public String name;
    public List<String> rights_names;

    @Override
    public String toString() {
        return (new Gson()).toJson(this);
    }

}
