package val;

import java.util.HashMap;
import java.util.Map;
import jregex.Matcher;
import jregex.Pattern;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;
import play.Logger;
import play.mvc.Router;

/**
 *
 * @author moocloud
 */
public class ControllerChainCheck extends AbstractAnnotationCheck<ControllerChain> {

   final static String mes = "validation.controllerchain";

   @Override
   public void configure(ControllerChain controllerchain) {
      setMessage(controllerchain.message());
   }

   public boolean isSatisfied(Object o, Object value, OValContext ovc, Validator vldtr) throws OValException {
      String controllerChain = (String) value;
      if (!controllerChain.endsWith(")")) {
         controllerChain = controllerChain + "()";
      }
      Matcher m = new Pattern("^({action}[^\\s(]+)({params}.+)?(\\s*)$").matcher(controllerChain);
      try {
         if (m.matches()) {
            String params = m.group("params");
            Map<String, Object> staticArgs = new HashMap<String, Object>();

            if (params == null || params.length() < 1) {
               Router.reverse(m.group("action"));
            }
            params = params.substring(1, params.length() - 1);
            for (String param : params.split(",")) {
               Matcher matcher = new Pattern("([a-zA-Z_0-9]+):'(.*)'").matcher(param);
               if (matcher.matches()) {
                  staticArgs.put(matcher.group(1), matcher.group(2));
               } else {
                  Logger.warn("Ignoring %s (static params must be specified as key:'value',...)", params);
               }
            }

            Router.reverse(m.group("action"), staticArgs);

         }
         Router.reverse(controllerChain);
         return true;
      } catch (Exception e) {
         return false;
      }
   }
}
