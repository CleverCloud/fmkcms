package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import play.Play;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author keruspe
 */
public class I18nController extends Controller {

    /**
     * Get languages supported by the user
     * @return The list of Locales
     */
    public static List<Locale> getLanguages() {
        List<Locale> locales = new ArrayList<Locale>();
        Locale tldLocale = I18nController.getTldLanguage();
        
        locales.addAll(I18nController.getQueryStringLanguages());
        if (tldLocale != null) {
            locales.add(tldLocale);
        }
        locales.add(new Locale(Lang.get()));
        locales.addAll(I18nController.getBrowserLanguages());

        return locales;
    }

    /**
     * Get the languages specified in the queryString (lang=foo&lang=bar)
     * @return The list of Locales
     */
    public static List<Locale> getQueryStringLanguages() {
        List<Locale> locales = new ArrayList<Locale>();
        
        String[] queryString = Http.Request.current().querystring.split("&");
        for (int i = 0; i < queryString.length; ++i) {
            String[] current = queryString[i].split("=");
            if (current.length != 2 || !current[0].equalsIgnoreCase("lang")) {
                continue;
            }
            String[] locale = current[1].split("-");
            switch (locale.length) {
                case 0:
                    break;
                case 1:
                    locales.add(new Locale(locale[0]));
                    break;
                default:
                    locales.add(new Locale(locale[0], locale[1].substring(0, 2)));
                    break;
            }
        }
        
        return locales;
    }

    /**
     * Gte locale from top level domainname
     * @return The Locale
     */
    public static Locale getTldLanguage() {
        String tld4locales = Play.configuration.getProperty("fmkcms.tld4locales", "false");
        
        if (!tld4locales.equalsIgnoreCase("true")) {
            return null;
        }
        
        Map<String, Locale> tldLocales = new HashMap<String, Locale>();

        // Add your tld specific locales here
        tldLocales.put("com", Locale.ENGLISH);
        tldLocales.put("org", Locale.ENGLISH);
        tldLocales.put("us", Locale.ENGLISH);
        
        String[] domainSplitted = Http.Request.current().domain.split("\\.");
        String tld = domainSplitted[domainSplitted.length - 1];
        
        Locale candidat = tldLocales.get(tld);
        if (candidat != null) {
            return candidat;
        }

        /* test whether we're facing an IP or a domain name */
        try {
            Integer.parseInt(tld);
            return null;
        } catch (NumberFormatException e) {
            return new Locale(tld);
        }
    }

    /**
     * Get the Locales supported by the user's browser
     * @return The list of Locales
     */
    public static List<Locale> getBrowserLanguages() {
        List<Locale> locales = new ArrayList<Locale>();
        List<String> languages = Http.Request.current().acceptLanguage();
        String[] locale;
        
        for (String language : languages) {
            locale = language.split("-");
            switch (locale.length) {
                case 0:
                    break;
                case 1:
                    locales.add(new Locale(locale[0]));
                    break;
                default:
                    locales.add(new Locale(locale[0], locale[1].substring(0, 2)));
                    break;
            }
        }
        
        return locales;
    }

    /**
     * Get all supported Locales as a List of String
     * @param blacklist The Locales to blacklist
     * @return The list of Locales
     */
    public static Set<String> getAllLocales(List<Locale> blacklist) {
        Set<String> locales = new TreeSet<String>();
        for (Locale locale : Arrays.asList(Locale.getAvailableLocales())) {
            locales.add(locale.toString());
        }
        if (blacklist != null && !blacklist.isEmpty()) {
            for (Locale blacklisted : blacklist) {
                locales.remove(blacklisted.toString());
            }
        }
        return locales;
    }

    /**
     * Get all supported Locales as a List of String
     * @param blacklist The Locales to blacklist
     * @return The list of Locales
     */
    public static Map<String, Locale> getAllLocalesInMap(List<Locale> blacklist) {
        Map<String, Locale> locales = new TreeMap<String, Locale>();
        for (Locale locale : Arrays.asList(Locale.getAvailableLocales())) {
            String s = locale.getDisplayLanguage().substring(0,1).toUpperCase() + locale.getDisplayLanguage().substring(1);
            locales.put(s + (!locale.getCountry().equals("") ? " : " + locale.getDisplayCountry() : ""), locale);
        }
        if (blacklist != null && !blacklist.isEmpty()) {
            for (Locale blacklisted : blacklist) {
                locales.remove(blacklisted.getDisplayLanguage() + (blacklisted.getCountry().equals("") ? " : " + blacklisted.getDisplayCountry() : ""));
            }
        }
        
        return locales;
    }

    /**
     * Change the lang for the next requests
     * @param lang The new lang
     */
    public static void changeLang(String lang) {
        Lang.change(lang);
        ok();
    }
}
