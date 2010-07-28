/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.List;
import models.Page;
import models.Tag;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class PageController extends Controller {

    public static void page(String urlId) {
        if (urlId == null) {
            notFound();
        }

        Page p = Page.getByUrlId(urlId);
        if (p == null) {
            notFound();
        }
        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(p);
        }

        render(p);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName);
        List<Page> listOfPages = Page.findTaggedWith(tagName);

        render(listOfPages, tag);
    }
}
