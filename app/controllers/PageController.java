package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import models.Page;
import models.PageRef;
import models.Tag;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@With(Secure.class)
public class PageController extends Controller {

   public static void listPages(Integer pagenumber) {
      if (pagenumber == null) {
         pagenumber = 0;
      }
      List<PageRef> pages = PageRef.getPageRefPage(pagenumber, 20);
      render(pages, pagenumber);
   }

   public static void deletePage_confirm(String urlId, String language) {
      Page page = Page.getPageByLocale(urlId, new Locale(language));
      render(page);
   }

   public static void deletePage(String urlId, String language) {
      Page page = Page.getPageByLocale(urlId, new Locale(language));
      if (page == null) {
         return;
      }
      PageRef pageRef = page.getPageRef();
      page.delete();
      if (Page.getFirstPageByPageRef(pageRef) == null) {
         pageRef.delete();
      }
      PageViewer.index();
   }

   public static void newPage() {
      renderArgs.put("action", "create");
      render("PageController/newPage.html");
   }

   public static void edit(String urlId, String language) {
      Page otherPage = Page.getPageByLocale(urlId, new Locale(language));
      renderArgs.put("otherPage", otherPage);
      renderArgs.put("action", "edit");
      render("PageController/newPage.html");
   }

   public static void translate(String otherUrlId, String language) {
      Page otherPage = Page.getPageByLocale(otherUrlId, new Locale(language));
      renderArgs.put("otherPage", otherPage);
      renderArgs.put("action", "translate");
      render("PageController/newPage.html");
   }

   public static void doNewPage(String actionz, String otherUrlId, String otherLanguage) {
      String urlId = otherUrlId;
      Page page = null;
      PageRef pageRef = null;
      String tagsString = params.get("pageReference.tags");

      if (urlId != null && !urlId.equals("")) {
         page = Page.getPageByUrlId(urlId);
      }
      if (page != null) {
         pageRef = page.getPageRef();
      } else {
         pageRef = PageController.doNewPageRef("edit", otherUrlId, otherLanguage);
      }
      urlId = params.get("page.urlId");

      Set<Tag> tags = new TreeSet<Tag>();
      if (tagsString != null && !tagsString.isEmpty()) {
         for (String tag : Arrays.asList(tagsString.split(","))) {
            tags.add(Tag.findOrCreateByName(tag));
         }
      }
      pageRef.tags = tags;

      if (!actionz.equals("edit")) {
         page = new Page();
      }
      page.reference = pageRef;
      page.urlId = urlId;
      page.title = params.get("page.title");
      page.content = params.get("page.content");
      page.language = params.get("page.language", Locale.class);
      page.published = (params.get("page.published") == null) ? Boolean.FALSE : Boolean.TRUE;

      validation.valid(page);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep(); // keep the errors for the next request
         if (actionz.equals("edit")) {
            PageController.edit(otherUrlId, otherLanguage);
         } else if (actionz.equals("translate")) {
            PageController.translate(otherUrlId, otherLanguage);
         } else {
            PageController.newPage();
         }
      }
      page.getPageRef().save();
      page.save();

      if (page.published) {
         PageViewer.page(urlId);
      } else {
         PageViewer.index();
      }
   }

   private static PageRef doNewPageRef(String action, String otherUrlId, String otherLanguage) {
      PageRef pageRef = new PageRef();

      validation.valid(pageRef);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep(); // keep the errors for the next request
         if (action.equals("edit")) {
            PageController.edit(otherUrlId, otherLanguage);
         } else if (action.equals("translate")) {
            PageController.translate(otherUrlId, otherLanguage);
         } else {
            PageController.newPage();
         }
      }

      return pageRef;
   }
}
