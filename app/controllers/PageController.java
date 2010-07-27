/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.google.gson.Gson;
import java.util.List;
import models.Page;
import models.Tag;
import play.Logger;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class PageController extends Controller {

    public static void page(String urlid) {
        if (urlid == null) {
            notFound();
        }

        Page p = Page.getByUrlid(urlid);
        if (p == null) {
            notFound();
        }
        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(p);
        }

        render(p);
    }

    public static void pagestag(String tagname) {
        Tag tag = Tag.findOrCreateByName(tagname);
        List<Page> listOfPages = Page.findTaggedWith(tagname);

        render(listOfPages, tag);
    }
}
