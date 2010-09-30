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

    public static void newPage(String otherUrlId) {
        render(otherUrlId);
    }

    public static void doNewPage() {
        String urlId = params.get("linkto.otherUrlId");
        Page page = null;
        PageRef pageRef = null;

        if (urlId != null && urlId.equals(""))
            page = Page.getPageByUrlId(urlId);
        if (page != null)
            pageRef = page.pageReference;
        else
            pageRef = PageController.doNewPageRef(params.get("pageReference.tags"));
        urlId = params.get("page.urlId");

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
            PageController.newPage("");
        }
        page.pageReference.save();
        page.save();

        if (page.published)
            PageViewer.page(urlId);
        else
            PageViewer.page("index");
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
        validation.valid(pageRef);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            PageController.newPage("");
        }

        return pageRef;
    }

}
