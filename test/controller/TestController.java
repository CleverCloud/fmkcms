/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import job.WritePages;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class TestController extends Controller {

    public static void writePages() {
        new WritePages().now();

        //   redirect("/");
    }
}
