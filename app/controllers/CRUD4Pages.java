/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import models.*;
import play.mvc.Before;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@CRUD.For(Page.class)
@With(CheckRights.class)
public class CRUD4Pages extends CRUD {

    //@Before(unless = {"addType", "attachment", "blank", "delete", "getPageSize", "index", "list", "show"})
    @Before(only = {"save"})
    static void rewriteSomeTags() {

        List<String> extraTags = Arrays.asList(params.all().get("extraTags"));
        String tagsAsString = extraTags.get(0);


        List<String> ids = new ArrayList<String>(Arrays.asList(params.all().get("object.tags.id")));


        if (!tagsAsString.isEmpty()) {
            List<String> tags = Arrays.asList(tagsAsString.split(","));



            Iterator<String> it = tags.iterator();
            while (it.hasNext()) {
                String tagName = it.next();
                if (!tagName.isEmpty()) {
                    Tag tag = Tag.findOrCreateByName(tagName);
                    ids.add(tag.id.toString());
                }
            }

        }

        params.all().put("object.tags.id", ids.toArray(new String[0]));

    }

}
