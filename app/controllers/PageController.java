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
        Page p = Page.getByUrlid(urlid);
        render(p);

    }
}
