package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.user.GAppUser;
import models.user.User;
import play.Play;
import play.mvc.Controller;
import play.utils.Java;

/**
 * This class works like Secure / Security
 *
 * You can get the connected User. This way, a fmkcms user can extend the users.User model
 * and be able to post on the blog.
 *
 * @author judu
 */
public class AccessManager extends Controller {

   public static User getConnected() {
      try {
         return (User) UserManager.invoke("getConnected");
      } catch (Throwable ex) {
         Logger.getLogger(AccessManager.class.getName()).log(Level.SEVERE, null, ex);
         return null;
      }
   }

   public static String connected() {
      return session.get("username");
   }

   public static boolean isGAppConnected() {
      return session.get("username") != null && session.get("firstName") != null
              && session.get("lastName") != null && session.get("email") != null;
   }

   public static class UserManager extends Controller {

      /**
       * Default: tries to get a GAppUser out of the database.
       * @return
       */
      public static User getConnected() {
         String username = session.get("username");
         return GAppUser.getByOpenId(username);
      }

      private static Object invoke(String meth, Object... args) throws Throwable {
         Class manager = null;
         List<Class> classes = Play.classloader.getAssignableClasses(AccessManager.UserManager.class);
         if (classes.isEmpty()) {
            manager = AccessManager.UserManager.class;
         } else {
            manager = classes.get(0);
         }
         try {
            return Java.invokeStaticOrParent(manager, meth, args);
         } catch (InvocationTargetException e) {
            throw e.getTargetException();
         }
      }
   }
}
