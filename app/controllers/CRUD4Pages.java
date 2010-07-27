/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import models.*;
import play.mvc.Before;

/**
 *
 * @author waxzce
 */
@CRUD.For(Page.class)
public class CRUD4Pages extends CRUD {

    @Before(unless = {"addType", "attachment", "blank", "delete", "getPageSize", "index", "list", "show"})
    static void rwsometags() {
        List<String> extratags = Arrays.asList(params.all().get("extratags"));
        String tagsasstring = extratags.get(0);
        if (!tagsasstring.isEmpty()) {
            List<String> tags = Arrays.asList(tagsasstring.split(","));

            List<String> ltid = new ArrayList<String>(Arrays.asList(params.all().get("object.tags@id")));

            for (Iterator<String> it = tags.iterator(); it.hasNext();) {
                String tagname = it.next();
                if (!tagname.isEmpty()) {
                    Tag tag = Tag.findOrCreateByName(tagname);
                    ltid.add(tag.id.toString());
                }
            }
            params.all().put("object.tags@id", ltid.toArray(new String[0]));
        }


    }
}
