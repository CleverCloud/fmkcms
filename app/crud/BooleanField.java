package crud;

import controllers.CRUDFieldProvider;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author keruspe
 */
@CRUDFieldProvider(tagName = "crud.fmkcms.booleanfield")
public class BooleanField {
    
    public static Boolean process(Map<String, String> values) {
        return (values.get("bool") != null);
    }

    public static Set<String> getArgsList() {
        Set<String> s = new TreeSet<String>();
        s.add("bool");
        return s;
    }

}
