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
        System.out.println(values);
        System.out.println("check1");
        String r = values.get("bool");
        if (r == null) {
            return false;
        }
        System.out.println("check2");
        return r.equals("on");
    }

    public static Set<String> getArgsList() {
        Set<String> s = new TreeSet<String>();
        s.add("bool");
        return s;
    }

}
