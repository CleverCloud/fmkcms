package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import models.Page;
import models.PageRef;
import models.Tag;
import play.Play;
import play.mvc.Controller;
import play.vfs.VirtualFile;

/**
 *
 * @author keruspe
 */
@SuppressWarnings("unchecked")
public class PageViewer extends Controller {

   /**
    * Get the good translation of a Page for the user within a List of translation
    * @param pages The list a translations
    * @return The good translation
    */
   public static Page getGoodPage(List<Page> pages) {
      if (pages == null || pages.isEmpty()) {
         return null;
      }

      Page page = null;
      switch (pages.size()) {
         case 0:
            return null;
         case 1:
            page = pages.get(0);
            if (!page.published) {
               return null;
            }
            return page;
         default:
            List<Locale> locales = I18nController.getLanguages();
            for (Locale locale : locales) {
               // Try exact Locale or exact language no matter the country
               for (Page candidat : pages) {
                  if ((candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) && candidat.published) {
                     return candidat;
                  }
               }
            }

            if (page == null || !page.published) {
               for (Page candidat : pages) {
                  if (candidat.published) {
                     return candidat; // pick up first published for now
                  }
               }
            }
            return null;
      }
   }

   /**
    * Get the good translation for a given urlId
    * @param urlId The urlId of the Page
    * @return The good translation
    */
   public static Page getGoodPageByUrlId(String urlId) {
      return PageViewer.getGoodPage(Page.getPagesByUrlId(urlId));
   }

   /**
    * Display the index
    */
   public static void index() {
      PageViewer.page(Play.configuration.getProperty("fmkcms.index", "index"));
   }

   /**
    * Display a Page given its urlId
    * @param urlId the urlId of the Page
    */
   public static void page(String urlId) {
      List<Page> pages = Page.getPagesByUrlId(urlId);
      Page page = PageViewer.getGoodPage(pages);
      if (page == null) {
         notFound(urlId);
      }

      if (request.headers.get("accept").value().contains("json")) {
         renderJSON(page);
      }

      Boolean isConnected = session.contains("username");
      String overrider = null;
      for (Page p : Page.getPagesByReference(page.reference)) {
         overrider = "/view/PageEvent/view/" + p.urlId + ".html";
         if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
            break;
         }
      }
      if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
         renderTemplate(overrider, page, isConnected);
      } else {
         render(page, isConnected);
      }
   }

   /**
    * Display a list of Pages tagged by a tag
    * @param tagName The name of the tag
    */
   public static void pagesTag(String tagName) {
      Tag tag = Tag.findOrCreateByName(tagName); /* avoid NPE in view ... */
      List<PageRef> pageRefs = PageRef.findTaggedWith(tag);
      List<Page> pages = new ArrayList<Page>();
      Page page = null;
      for (PageRef pageRef : pageRefs) {
         page = PageViewer.getGoodPage(Page.getPagesByReference(pageRef));
         if (page != null) {
            pages.add(page);
         }
      }
      render(pages, tag);
   }
}
