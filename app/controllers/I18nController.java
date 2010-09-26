package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import play.Play;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author keruspe
 */
public class I18nController extends Controller {

    public static List<Locale> getLanguages() {
        List<Locale> locales = new ArrayList<Locale>();

        String tld4locales = Play.configuration.getProperty("fmkcms.tld4locales");
        if (tld4locales != null && tld4locales.equalsIgnoreCase("true")) {
            String[] domainSplitted = Http.Request.current().domain.split("\\.");
            String tld = domainSplitted[domainSplitted.length - 1];
            try {
                Integer.parseInt(tld);
                /* test whether we're facing an IP or a domain name */
            } catch(NumberFormatException e) {
                locales.add(new Locale(tld));
            }
        }

        locales.addAll(I18nController.getBrowserLanguages());
        return locales;
    }

    public static List<Locale> getBrowserLanguages() {
        List<Locale> locales = new ArrayList<Locale>();
        List<String> languages = Http.Request.current().acceptLanguage();

        for (String language : languages) {
            if (language.contains("-"))
                locales.add(new Locale(language.split("-")[0], language.split("-")[1]));
            else
                locales.add(new Locale(language));
        }

        return locales;
    }

}
