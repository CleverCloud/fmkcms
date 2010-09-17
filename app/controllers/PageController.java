package controllers;

import elasticsearch.SearchJob;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Page;
import models.PageRef;
import models.Tag;
import play.data.validation.Validation;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 * @author keruspe
 */
public class PageController extends Controller {

    public static void page(String urlId) {
        List<Page> pages = Page.getPagesByUrlId(urlId);
        Page page = null;

        switch (pages.size()) {
            case 0:
                notFound();
            case 1:
                page = pages.get(0);
                break;
            default:
                List<Locale> locales = I18nController.getBrowserLanguages();
                for (Locale locale : locales) {
                    // Try exact Locale
                    for (Page candidat : pages) {
                        if ((candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) && candidat.published) {
                            page = candidat;
                            break;
                        }
                    }
                }

                if (page == null)
                    page = pages.get(0); // pick up first for now
        }

        if (!page.published) {
            notFound();
        }

        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(page);
        }

        render(page);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName); /* avoid NPE in view ... */
        render(Page.findTaggedWith(tagName), tag);
    }

    public static void newPage() {
        render();
    }

    public static void doNewPage() {
        String urlId = params.get("linkTo.urlId");
        if (urlId.equals("new"))
            urlId = params.get("linkTo.newUrlId");
        String title = params.get("page.title");
        String content = params.get("page.content");
        Locale language = params.get("page.language", Locale.class);
        Boolean published = (params.get("page.published") == null) ? Boolean.FALSE : Boolean.TRUE;

        Page page = Page.getPageByUrlId(urlId);
        if (page != null) {
            page = page.addTranslation(urlId, title, content, language, published);
        } else {
            PageRef pageRef = PageController.doNewPageRef(params.get("pageReference.tags"));
            validation.valid(pageRef);
            if (Validation.hasErrors()) {

                params.flash(); // add http parameters to the flash scope
                Validation.keep(); // keep the errors for the next request

                PageController.newPage();
            }

            page = new Page();
            page.pageReference = pageRef.save();
            page.urlId = urlId;
            page.title = title;
            page.content = content;
            page.language = language;
            page.published = published;
            validation.valid(page);
            if (Validation.hasErrors()) {

                params.flash(); // add http parameters to the flash scope
                Validation.keep(); // keep the errors for the next request

                PageController.newPage();
            } else {
                page.pageReference.delete(); // TODO: better handling of this (to avoid useless pageRefs)
                page.save();
            }
        }

        if (page.published) {
            PageController.page(urlId);
        } else {
            PageController.page("index");
        }
    }

    private static PageRef doNewPageRef(String tagsString) {
        PageRef pageRef = new PageRef();
        Set<Tag> tags = new TreeSet<Tag>();

        if (!tagsString.isEmpty()) {
            for (String tag : Arrays.asList(tagsString.split(","))) {
                tags.add(Tag.findOrCreateByName(tag));
            }
        }

        pageRef.tags = tags;
        return pageRef;
    }

    public static void searchPage(String q) {
        if (request.isNew) {

            Future<String> task = new SearchJob(q).now();
            request.args.put("task", task);
            waitFor(task);
        }
        try {
            renderText(((Future<String>) request.args.get("task")).get());
            // TODO: Reimplement Search
            /*if (q == null) {
            q = "search";
            }
            EntityManager em = JPA.entityManagerFactory.createEntityManager();
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
            try {
            fullTextEntityManager.createIndexer().startAndWait();
            } catch (InterruptedException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
            }
            org.hibernate.Session s = (org.hibernate.Session) JPA.em().getDelegate();
            FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(s);
            Transaction tx = fullTextSession.beginTransaction();
            String[] fields = new String[]{"title", "content", "urlId", "tags.name"};
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_20, fields, new StandardAnalyzer(Version.LUCENE_20));
            org.apache.lucene.search.Query query = null;
            try {
            query = parser.parse(q);
            } catch (ParseException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
            }
            org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, Page.class);
            List<Page> results = hibQuery.list();
            tx.commit();
            render(results, q);*/
            // render();
        } catch (InterruptedException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
