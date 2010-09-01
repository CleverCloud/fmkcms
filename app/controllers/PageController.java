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
/*        Page page = Page.getByUrlId(urlId);
        if (page == null || ! page.published)
            notFound();

        if (request.headers.get("accept").value().contains("json")) {
            renderJSON(page);
        }

        render(page);
 *
 */
    }

    public static void pagesTag(String tagName) {
   /*     Tag tag = Tag.findOrCreateByName(tagName);
        List<Page> listOfPages = Page.findTaggedWith(tagName);

        render(listOfPages, tag);

    */}

    
    
}
