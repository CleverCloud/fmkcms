/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package val;

import java.util.logging.Level;
import models.Page;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import play.Logger;

/**
 *
 * @author waxzce
 */
public class PageUrlIDCheck extends AbstractAnnotationCheck<PageUrlID> {

    final static String mes = "validation.urlid";

    @Override
    public void configure(PageUrlID urlid) {
        setMessage(urlid.message());
    }

    public boolean isSatisfied(Object o, Object value, OValContext ovc, Validator vldtr) throws OValException {
        Page p = Page.getPageByUrlId((String) value);
        if (p == null) {
            return false;
        } else {
            return true;
        }
    }
}
