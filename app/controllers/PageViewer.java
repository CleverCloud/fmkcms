package controllers;

import elasticsearch.SearchJob;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Page;
import models.PageRef;
import models.Tag;
import play.mvc.Controller;

/**
 *
 * @author keruspe
 */
@SuppressWarnings("unchecked")
public class PageViewer extends Controller {

    private static Page getGoodPage(List<Page> pages) {
        if(pages == null)
            return null;

        Page page = null;
        switch (pages.size()) {
            case 0:
                return null;
            case 1:
                page = pages.get(0);
                if (!page.published)
                    return null;
                return page;
            default:
                List<Locale> locales = I18nController.getLanguages();
                linguas: for (Locale locale : locales) {
                    // Try exact Locale or exact language no matter the country
                    for (Page candidat : pages) {
                        if ((candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) && candidat.published)
                            return candidat;
                    }
                }

                if (page == null || !page.published) {
                    for (Page candidat : pages) {
                        if (candidat.published)
                            return candidat; // pick up first published for now
                    }
                }
                return null;
        }
    }

    public static void page(String urlId) {
        List<Page> pages = Page.getPagesByUrlId(urlId);
        Page page = PageViewer.getGoodPage(pages);
        if (page == null)
            notFound(urlId);

        if (request.headers.get("accept").value().contains("json"))
            renderJSON(page);

        Boolean isConnected = session.contains("username");
        render(page, isConnected);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName); /* avoid NPE in view ... */
        List<PageRef> pageRefs = PageRef.findTaggedWith(tag);
        List<Page> pages = new ArrayList<Page>();
        Page page = null;
        for (PageRef pageRef : pageRefs) {
            page = PageViewer.getGoodPage(Page.getPagesByPageRef(pageRef));
            if (page != null)
                pages.add(page);
        }
        render(pages, tag);
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
