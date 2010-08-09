package controllers;

import controllers.secureStuff.SecureConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import models.Page;
import models.Tag;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.util.Version;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import play.db.jpa.JPA;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class PageController extends Controller {

    @Check(SecureConstants.READ_PAGE)
    public static void page(String urlId) {
        if (urlId == null) {
            notFound();
        }

        Page p = Page.getByUrlId(urlId);
        if (p == null) {
            notFound();
        }
        if (!p.published) {
            notFound();
        }
        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(p);
        }

        render(p);
    }

    public static void pagesTag(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName);
        List<Page> listOfPages = Page.findTaggedWith(tagName);

        render(listOfPages, tag);
    }

    public static void searchPage(String toFind) {

        EntityManager em = JPA.entityManagerFactory.createEntityManager();

        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        org.hibernate.Session s = ((org.hibernate.Session) JPA.em().getDelegate());
        FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(s);

        Transaction tx = fullTextSession.beginTransaction();


        String[] fields = new String[]{"title", "content", "urlId", "tags.name"};

        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_20, fields, new StandardAnalyzer(Version.LUCENE_20));
        org.apache.lucene.search.Query query = null;
        try {
            query = parser.parse(toFind);
        } catch (ParseException ex) {
            Logger.getLogger(PageController.class.getName()).log(Level.SEVERE, null, ex);
        }

        org.hibernate.Query hibQuery = fullTextSession.createFullTextQuery(query, Page.class);

        List<Page> results = hibQuery.list();

        tx.commit();

        renderJSON(results);

    }
}
