/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.fmkcms;

import com.clevercloud.utils.locale.LocaleSet;
import com.google.code.morphia.annotations.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import mongo.MongoEntity;

/**
 *
 * @author waxzce
 */
@Entity
public class I18nShortValue extends MongoEntity {

    public Map<Locale, String> content;

    public Map<Locale, String> getContent() {
        if (content == null) {
            content = new HashMap<Locale, String>();
        }
        return content;
    }

    public String getBestFor(Locale l) {
        List<Locale> ll = new ArrayList<Locale>();
        ll.add(l);
        return getBestFor(ll);
    }

    public String getBestFor(List<Locale> l) {
        LocaleSet ls = new LocaleSet();
        ls.addAll(content.keySet());
        return content.get(ls.getTheBestLocale(l));
    }
}
