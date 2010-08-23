/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crud;

import controllers.CRUDFieldProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import crud.java.util.LocaleField;
import models.blog.Post;

/**
 *
 * @author waxzce
 */
@CRUDFieldProvider(isMultiple = true, tagName = "crud.fmkcms.blogdatamap")
public class BlogDataMapField {

    public static Map<Locale, Post> process(Map<String, List<String>> values) {
        Map<Locale, Post> returnmap = new HashMap<Locale, Post>();
        Iterator<String> itrId = values.get("idpd").iterator();
        Iterator<String> itrTitle = values.get("title").iterator();
        Iterator<String> itrContent = values.get("content").iterator();
        Iterator<String> itrLang = values.get("lang").iterator();
        while (itrId.hasNext()) {
            String id = itrId.next();
            if (!id.isEmpty()) {
                Post pd = Post.findById(Long.parseLong(id));
                String string = itrLang.next();
                Locale locale = LocaleField.process(string);
                pd.title = itrTitle.next();
                pd.content = itrContent.next();
                pd.save();
                returnmap.put(locale, pd);
            }
        }

        while (itrLang.hasNext()) {
            String string = itrLang.next();
            Locale locale = LocaleField.process(string);
            Post pd = new Post();
            pd.title = itrTitle.next();
            pd.content = itrContent.next();
            pd.save();
            returnmap.put(locale, pd);
        }
        return returnmap;
    }

    public static Set<String> getArgsList() {
        Set<String> s = new TreeSet<String>();
        s.add("idpd");
        s.add("title");
        s.add("content");
        s.add("lang");
        return s;
    }
}
