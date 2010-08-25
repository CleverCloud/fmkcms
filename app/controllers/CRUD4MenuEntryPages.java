package controllers;

import models.*;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@CRUD.For(MenuEntryPage.class)
@With(CheckRights.class)
public class CRUD4MenuEntryPages extends CRUD {

    public static void quickAdd(String urlId) {
        PageRef p = PageRef.getByUrlId(urlId);
        if (p != null) {
            MenuEntryPage mep = new MenuEntryPage();
            mep.urlid = urlId;
            mep.save();
            renderJSON(mep);
        }
    }
}
