package models.i18n;

import java.lang.Class;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import mongo.MongoEntity;

/**
 *
 * @author keruspe
 */
public abstract class TranslatableRef<T extends Translatable, R extends TranslatableRef> extends MongoEntity {

    /**
     * Get all available locales for this Translatable
     * @return The list of Locale
     */
    public <T extends Translatable> List<Locale> getAvailableLocales() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();//getGenericSuperClass();
        Class c = (Class) Arrays.asList(pt.getActualTypeArguments()).get(0);
        List<T> items = (List<T>) MongoEntity.getDs().find(c, "reference", this).asList();
        List<Locale> locales = new ArrayList<Locale>();

        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                locales.add(item.language);
            }
        }
        return locales;
    }
}
