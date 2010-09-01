package controllers;

import java.util.List;
import models.Page;
import models.Tag;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@With(CheckRights.class)
public class PageController extends Controller {

    public static void page(String urlId) {
        Page page = Page.getByUrlId(urlId);
        if (page == null || ! page.published)
            notFound();

        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(page);
        }

        render(page);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName);
        List<Page> listOfPages = Page.findTaggedWith(tagName);

        render(listOfPages, tag);
    }

    /*public static void searchPage(String q) {
        if (q == null) {
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

        String[] fields = new String[]{"title", "content", "pageReference.urlId", "tags.name"};

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

        render(results, q);
    }*/
    
    
}
