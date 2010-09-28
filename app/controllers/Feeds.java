/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.blog.Post;
import mongo.MongoEntity;
import play.Play;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class Feeds extends Controller {

    public static void main() {
        SyndFeed feed = new SyndFeedImpl();
        Properties p = new Properties();
        try {

            p.load(new FileReader(Play.getVirtualFile("conf/feed.properties").getRealFile()));
        } catch (IOException ex) {
            Logger.getLogger(Feeds.class.getName()).log(Level.SEVERE, null, ex);
        }
        feed.setAuthor(p.getProperty("feed.author"));
        feed.setFeedType("rss_2.0");
        feed.setCopyright(p.getProperty("feed.copyright"));
        feed.setDescription(p.getProperty("feed.description"));
        feed.setLink(request.getBase() + p.getProperty("feed.link"));
        feed.setTitle(p.getProperty("feed.title"));
        feed.setLanguage(p.getProperty("feed.language"));
        feed.setPublishedDate(new Date());

        List<Post> posts = MongoEntity.getDs().find(Post.class).order("-postedAt").asList();
        List entries = new ArrayList();
        for (Post post : posts) {
            SyndEntry item = new SyndEntryImpl();
            item.setPublishedDate(post.postedAt);
            item.setTitle(post.title);
            SyndContent content = new SyndContentImpl();
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
