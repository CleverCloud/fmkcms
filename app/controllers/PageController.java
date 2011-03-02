package controllers;

import java.util.ArrayList;
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
import play.vfs.VirtualFile;

/**
 *
 * @author waxzce
 * @author keruspe
 */
@With(Secure.class)
public class PageController extends Controller {

   /**
    * Display a list of Pages
    * @param pagenumber The number of the page to display (20 Page by page)
    */
   public static void listPages(Integer pagenumber) {
      if (pagenumber == null) {
         pagenumber = 0;
      }
      List<PageRef> pageRefs = PageRef.getPageRefsWithPagination(pagenumber, 20);
      List<Page> pages = new ArrayList<Page>();

      for (PageRef pr : pageRefs) {
         pages.add(Page.getFirstPageByReference(pr));
      }

      render(pages, pagenumber);
   }

   /**
    * Ask confirmation for deleting a Page
    * @param urlId The urlId of the Page
    * @param language The lang of the Page
    */
   public static void deletePage_confirm(String urlId, String language) {
      Page page = Page.getPageByLocale(urlId, new Locale(language));
      render(page);
   }

   /**
    * Delete a Page
    * @param urlId The urlId of the Page
    * @param language The lang of the Page
    */
   public static void deletePage(String urlId, String language) {
      Page page = Page.getPageByLocale(urlId, new Locale(language));
      if (page == null) {
         return;
      }
      PageRef pageRef = page.reference;
      page.delete();
      if (Page.getFirstPageByReference(pageRef) == null) {
         pageRef.delete();
      }
      PageViewer.index();
   }

   /**
    * Create a new Page
    */
   public static void newPage() {
      renderArgs.put("action", "create");
      render("PageController/newPage.html");
   }

   /**
    * Edit a Page
    * @param urlId The urlId of the Page
    * @param language The lang of the Page
    */
   public static void edit(String urlId, String language) {
      Page otherPage = Page.getPageByLocale(urlId, new Locale(language));
      renderArgs.put("otherPage", otherPage);
      renderArgs.put("action", "edit");

      String overrider = null;
      
      for (Page p : Page.getPagesByReference(otherPage.reference)) {
         overrider = "/view/PageEvent/edit/" + p.urlId + ".html";
         if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists())
            break;
      }

      if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
         renderTemplate(overrider);
      } else {
         render("PageController/newPage.html");
      }
   }

   /**
    * Translate a page
    * @param otherUrlId The urlId of the Page to translate
    * @param language The lang of the Page
    */
   public static void translate(String otherUrlId, String language) {
      Page otherPage = Page.getPageByLocale(otherUrlId, new Locale(language));
      renderArgs.put("otherPage", otherPage);
      renderArgs.put("action", "translate");

      String overrider = null;

      for (Page p : Page.getPagesByReference(otherPage.reference)) {
         overrider = "/view/PageEvent/translate/" + p.urlId + ".html";
         if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists())
            break;
      }

      if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
         renderTemplate(overrider);
      } else {
         render("PageController/newPage.html");
      }
   }

   /**
    * Create/Edit/Translate a Page
    * @param actionz The action we're performing ("edit", "create", "translate")
    * @param otherUrlId The urlId of the Page we're translating or editing (may be null)
    * @param otherLanguage The lang of the Page we're translating or editing (may be null)
    */
   public static void doNewPage(String actionz, String otherUrlId, String otherLanguage) {
      String urlId = otherUrlId;
      Page page = null;
      PageRef pageRef = null;
      String tagsString = params.get("pageReference.tags");

      if (urlId != null && !urlId.equals("")) {
         page = Page.getPageByUrlId(urlId);
      }
      if (page != null) {
         pageRef = page.reference;
      } else {
         pageRef = PageController.doNewPageRef(actionz, otherUrlId, otherLanguage);
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
      if (actionz.equals("edit")) {
         page.reference.refresh().save();
         page.refresh().save();
      } else {
         page.reference.save();
         page.save();
      }

      if (page.published) {
         PageViewer.page(urlId);
      } else {
         PageViewer.index();
      }
   }

   /**
    * Create a PageRef for a new Page
    * @param actionz The action we're performing ("edit", "create", "translate")
    * @param otherUrlId The urlId of the Page we're translating or editing (may be null)
    * @param otherLanguage The lang of the Page we're translating or editing (may be null)
    * @return The PageRef
    */
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
