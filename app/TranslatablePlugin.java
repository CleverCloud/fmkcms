
import enhancer.TranslatableEnhancer;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

/**
 *
 * @author keruspe
 */
public class TranslatablePlugin extends PlayPlugin {



   private TranslatableEnhancer enhancer = new TranslatableEnhancer();


/*   @Override
   public Object bind(String name, Object o, Map<String, String[]> params) {
      if (o instanceof Translatable) {
         return o;
      }
      return null;
   }*/

   @Override
   public void enhance(ApplicationClass applicationClass) throws Exception {
      enhancer.enhanceThisClass(applicationClass);
   }
}
