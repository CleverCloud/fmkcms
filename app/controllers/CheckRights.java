package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import play.Logger;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class CheckRights extends Controller {

    @Before
    public static void checkRights() {
        String role = (String) Cache.get("SECURE_" + request.action);
        if (role == null) {
            forbidden(request.action + " is not allowed for you. Try connect.");
        }

        if (!role.equals("*")) {
            try {
                Class c = Class.forName("controllers.RolesManagement");
                Class[] p1 = new Class[]{String.class};
                Method m = c.getMethod("haveRole", p1);

                Object[] args = new Object[]{role};
                Boolean auth = (Boolean) m.invoke(c, args);

                if (!auth) {
                   forbidden();
                }

            } catch (IllegalAccessException ex) {
                Logger.error(ex, null);
            } catch (IllegalArgumentException ex) {
                Logger.error(ex, null);
            } catch (InvocationTargetException ex) {
                Logger.error(ex, null);
            } catch (NoSuchMethodException ex) {
                Logger.error(ex, null);
            } catch (SecurityException ex) {
                Logger.error(ex, null);
            } catch (ClassNotFoundException ex) {
                Logger.error(ex, null);
            }

        }
    }
}
