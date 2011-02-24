
import enhancer.TranslatableEnhancer;
import java.lang.annotation.Annotation;
import java.util.Map;
import models.i18n.Translatable;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

/**
 *
 * @author keruspe
 */
public class TranslatablePlugin extends PlayPlugin {

   @Override
   public Object bind(String name, Object o, Map<String, String[]> params) {
      if (o instanceof Translatable) {
         return o;
      }
      return null;
   }

   @Override
   public void enhance(ApplicationClass applicationClass) throws Exception {
      new TranslatableEnhancer().enhanceThisClass(applicationClass);
   }
}
