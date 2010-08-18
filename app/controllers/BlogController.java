package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author clementnivolle
 */
public class BlogController extends Controller {

    public static List<Locale> getBrowserLanguages() {
        List<String> languages = Http.Request.current().acceptLanguage();
        List<Locale> locales = new ArrayList<Locale>();

        Locale locale = null;
        for (String language : languages) {
            if (language.split("-").length == 2)
                locale = new Locale(language.split("-")[0], language.split("-")[1]);
            else
                locale = new Locale(language.split("-")[0]);

            locales.add(locale);
        }

        return locales;
    }

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText();
        Cache.set(id, code, "10min");
        renderBinary(captcha);
    }
    
}

