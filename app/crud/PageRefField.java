/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crud;

import controllers.CRUDFieldProvider;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import models.PageRef;

/**
 *
 * @author waxzce
 */
@CRUDFieldProvider(tagName = "crud.fmkcms.pagereffield")
public class PageRefField {

    public static PageRef process(Map<String, String> value) {
        if (value.containsKey("idpr") && !(value.get("idpr") == null)) {
            return PageRef.findById(Long.parseLong(value.get("idpr")));
        } else {
            PageRef pr = new PageRef();
            pr.urlId = value.get("urlid");
            pr.save();
            return pr;
        }
    }

    public static Set<String> getArgsList() {
        Set<String> s = new TreeSet<String>();
        s.add("idpr");
        s.add("urlid");

        return s;

    }
}
