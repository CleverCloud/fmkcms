/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.Locale;
import play.Logger;
import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author clementnivolle
 */
public class BlogController extends Controller {

    public static Locale getBrowserLang(){
        String lang = Http.Request.current().acceptLanguage().get(0);
        Locale localeFromBrowser = null;
        if (lang.split("-").length == 2) {
            localeFromBrowser = new Locale(lang.split("-")[0],lang.split("-")[1]);
        } else {
            localeFromBrowser = new Locale(lang.split("-")[0]);
        }
        return localeFromBrowser;
    }

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText();
        Cache.set(id, code, "10min");
        renderBinary(captcha);
    }
}

