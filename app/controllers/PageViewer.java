package controllers;

import elasticsearch.SearchJob;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Page;
import models.Tag;
import play.mvc.Controller;

/**
 *
 * @author keruspe
 */
@SuppressWarnings("unchecked")
public class PageViewer extends Controller {

    public static void page(String urlId) {
        List<Page> pages = Page.getPagesByUrlId(urlId);
        Page page = null;

        switch (pages.size()) {
            case 0:
                notFound(urlId);
            case 1:
                page = pages.get(0);
                if (!page.published)
                    notFound(urlId);
                break;
            default:
                List<Locale> locales = I18nController.getLanguages();
                for (Locale locale : locales) {
                    // Try exact Locale or exact language no matter the country
                    for (Page candidat : pages) {
                        if ((candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) && candidat.published) {
                            page = candidat;
                            break;
                        }
                    }
                    if (page != null)
                        break;
                }

                if (page == null || !page.published) {
                    for (Page candidat : pages) {
                        if (candidat.published) {
                            page = candidat; // pick up first published for now
                            break;
                        }
                    }
                }
                if (page == null || !page.published)
                    notFound(urlId);
        }

        if (request.headers.get("accept").value().contains("json"))
            renderJSON(page);

        render(page);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName); /* avoid NPE in view ... */
        render(Page.findTaggedWith(tagName), tag);
    }

    public static void searchPage(String q) {
        if (request.isNew) {
            Future<String> task = new SearchJob(q).now();
            request.args.put("task", task);
            waitFor(task);
        }
        try {
            renderText(((Future<String>) request.args.get("task")).get());
        } catch (InterruptedException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
