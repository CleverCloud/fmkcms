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
            page = new Page();
            page.pageReference = PageController.doNewPageRef(params.get("pageReference.tags"));
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
            }
            page.save();
        }

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
            PageController.newPage();
        }

        return pageRef.save();
    }

}
