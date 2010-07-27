/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.google.gson.Gson;
import models.Page;
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

    public static void pagejson(String urlid) {
        renderText("sdfhjgqsdkjfhgsqdf");


    }
}
