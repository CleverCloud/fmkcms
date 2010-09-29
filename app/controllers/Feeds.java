package controllers;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.blog.Post;
import play.Play;
import play.mvc.Controller;
import utils.LangProperties;

/**
 *
 * @author waxzce
 */
@SuppressWarnings("unchecked")
public class Feeds extends Controller {

    public static void main(String lang) {

        Locale locale = null;
        String[] s = lang.split("_");
        switch (s.length) {
            case 1:
                locale = new Locale(s[0]);
                break;
            case 2:
                locale = new Locale(s[0], s[1].substring(0, 2));
                break;
        }

        SyndFeed feed = new SyndFeedImpl();
        LangProperties p = new LangProperties();

        try {
            p.load(new FileReader(Play.getVirtualFile("conf/feed.properties").getRealFile()));
        } catch (IOException ex) {
            Logger.getLogger(Feeds.class.getName()).log(Level.SEVERE, null, ex);
        }

        feed.setAuthor(p.getProperty("feed.author", locale));
        feed.setFeedType("rss_2.0");
        feed.setCopyright(p.getProperty("feed.copyright", locale));
        feed.setDescription(p.getProperty("feed.description", locale));
        feed.setLink(request.getBase() + "/" + p.getProperty("feed.link", locale));
        feed.setTitle(p.getProperty("feed.title", locale));
        feed.setLanguage(locale.toString());
        feed.setPublishedDate(new Date());

        List<Post> posts = Post.getLatestPostsByLocale(locale, 20, 1);
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry item = null;
        SyndContent content = null;
        for (Post post : posts) {
            item = new SyndEntryImpl();
            item.setPublishedDate(post.postedAt);
            item.setTitle(post.title);
            content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(post.content);
            item.setDescription(content);
            item.setLink(request.getBase() + "/post/" + post.title);
            entries.add(item);
        }
        feed.setEntries(entries);

        StringWriter writer = new StringWriter();
        SyndFeedOutput out = new SyndFeedOutput();
        try {
            out.output(feed, writer);
        } catch (IOException e) {
            flash("error", "Erreur d'entré/sortie (StringWriter) lors de la sérialisation du flux : " + e.getMessage());
        } catch (FeedException e) {
            flash("error", "Erreur lors de la sérialisation du flux : " + e.getMessage());
        }
        response.contentType = "application/rss+xml";
        renderXml(writer.toString());
    }

}
