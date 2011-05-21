package controllers;

import elasticsearch.AddSearch;
import elasticsearch.Searchable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import mongo.MongoEntity;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.mvc.Controller;
import play.mvc.With;
import annotations.Check;
import controllers.secure.Secure;

/**
 *
 * @author judu
 */
@With(Secure.class)
@Check(SearchController.CAN_EDIT)
public class SearchController extends Controller {

    public static final String CAN_EDIT = "can_edit_search";

    public static void indexAndStats(Boolean reindex) {
        ApplicationClasses classes = Play.classes;
        List<ApplicationClass> appClassesList = classes.all();

        Map<String, Long> indexesStats = new HashMap<String, Long>();

        for (ApplicationClass appClass : appClassesList) {
            if (appClass.getPackage().contains("models")) {
                for (Class inter : appClass.javaClass.getInterfaces()) {
                    if (inter.equals(Searchable.class)) {
                        Long count = MongoEntity.getDs().getCount(appClass.javaClass);
                        indexesStats.put(appClass.javaClass.getSimpleName(), count);

                    }
                }
            }
        }

        if (reindex != null && reindex) {
            renderArgs.put("reindex", true);
        }
        render(indexesStats);
    }

    public static void reindex() {
        AddSearch sjob = new AddSearch();
        try {
            sjob.now();
        } catch (Exception ex) {
            Logger.getLogger(SearchController.class.getName()).log(Level.SEVERE, null, ex);
        }

        indexAndStats(Boolean.TRUE);
    }
}
