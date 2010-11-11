package controllers;

import java.util.Arrays;
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

    public static void deletePage(String urlId, String language) {
        Page page = Page.getPageByLocale(urlId, new Locale(language));
        if (page == null)
            return;
        PageRef pageRef = page.pageReference;
        page.delete();
        if (Page.getFirstPageByPageRef(pageRef) == null)
            pageRef.delete();
        PageViewer.page("index");
    }

    public static void newPage(String action, String otherUrlId, String language) {
        if (action.equals("delete"))
            PageController.deletePage(otherUrlId, language);
        render(action, otherUrlId, language);
    }

    public static void doNewPage(String action, String otherUrlId, String otherLanguage) {
        String urlId = otherUrlId;
        Page page = null;
        PageRef pageRef = null;
        String tagsString = params.get("pageReference.tags");

        if (urlId != null && !urlId.equals(""))
            page = Page.getPageByUrlId(urlId);
        if (page != null)
            pageRef = page.pageReference;
        else
            pageRef = PageController.doNewPageRef(action, otherUrlId, otherLanguage);
        urlId = params.get("page.urlId");

        Set<Tag> tags = new TreeSet<Tag>();
        if (tagsString != null && !tagsString.isEmpty()) {
            for (String tag : Arrays.asList(tagsString.split(",")))
                tags.add(Tag.findOrCreateByName(tag));
        }
        pageRef.tags = tags;

        if (!action.equals("edit"))
            page = new Page();
        page.pageReference = pageRef;
        page.urlId = urlId;
        page.title = params.get("page.title");
        page.content = params.get("page.content");
        page.language = params.get("page.language", Locale.class);
        page.published = (params.get("page.published") == null) ? Boolean.FALSE : Boolean.TRUE;

        validation.valid(page);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            PageController.newPage(action, otherUrlId, otherLanguage);
        }
        page.pageReference.save();
        page.save();

        if (page.published)
            PageViewer.page(urlId);
        else
            PageViewer.page("index");
    }

    private static PageRef doNewPageRef(String action, String otherUrlId, String otherLanguage) {
        PageRef pageRef = new PageRef();

        validation.valid(pageRef);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            PageController.newPage(action, otherUrlId, otherLanguage);
        }

        return pageRef;
    }

}
