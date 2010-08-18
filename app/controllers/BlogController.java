/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;

/**
 *
 * @author clementnivolle
 */
public class BlogController extends Controller {

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText();
        Cache.set(id, code, "10min");
        renderBinary(captcha);
    }
}

