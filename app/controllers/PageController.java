/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import models.Page;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class PageController extends Controller {

    public static void page(String urlid) {
        if(urlid == null){
            notFound();
        }
        Page p = Page.getByUrlid(urlid);
        if(p == null){
            notFound();
        }
        render(p);
    }
}
