package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author keruspe
 */
public class I18nController extends Controller {

    public static List<Locale> getBrowserLanguages() {
        List<String> languages = Http.Request.current().acceptLanguage();
        List<Locale> locales = new ArrayList<Locale>();

        for (String language : languages) {
            if (language.contains("-"))
                locales.add(new Locale(language.split("-")[0], language.split("-")[1]));
            else
                locales.add(new Locale(language));
        }

        return locales;
    }

}
