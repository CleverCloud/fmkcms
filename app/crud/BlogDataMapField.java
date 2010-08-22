/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crud;

import controllers.CRUDFieldProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import models.blog.PostData;

/**
 *
 * @author waxzce
 */

@CRUDFieldProvider(isMultiple=true,tagName="crud.fmkcms.blogdatamap")
public class BlogDataMapField {

    public static Map<Locale, PostData> process(Map<String, List<String>> values) {
        Map<Locale, PostData> returnmap = new HashMap<Locale, PostData>();
        
        return returnmap;
    }

    public static Set<String> getArgsList() {
        Set<String> s = new TreeSet<String>();
        s.add("idpd");
        s.add("title");
        s.add("content");
        return s;
    }
}
