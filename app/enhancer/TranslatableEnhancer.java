package enhancer;

import javassist.CtClass;
import javassist.CtMethod;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;

/**
 *
 * @author keruspe
 */
public class TranslatableEnhancer extends Enhancer {

   @Override
   public void enhanceThisClass(ApplicationClass ac) throws Exception {
      CtClass ctClass = makeClass(ac);
      if (!ctClass.subclassOf(classPool.get("models.i18n.Translatable")) || ctClass.equals(classPool.get("models.i18n.Translatable"))) {
         System.out.println("Not enhancing " + ctClass.getName());
         return;
      }

      String className = ctClass.getName();

      System.out.println("Enhancing " + className);

      CtMethod getTranslatablesByUrlId = CtMethod.make(
              "public static java.util.List<" + className + "> get" + className + "sByUrlId(String urlId) {"
              + className + " translatable = " + className + ".get" + className + "ByUrlId(urlId);"
              + "return (translatable == null) ? new ArrayList<" + className + ">() "
              + ": MongoEntity.getDs().find(" + className + ".class, \"reference\", translatable.reference).asList();"
              + "}", ctClass);
      ctClass.addMethod(getTranslatablesByUrlId);

      CtMethod getTranslatableByUrlId = CtMethod.make(
              "public static " + className + " get" + className + "ByUrlId(String urlId) {"
              + "return MongoEntity.getDs().find(" + className + ".class, \"urlId\", urlId).get();"
              + "}", ctClass);
      ctClass.addMethod(getTranslatableByUrlId);

      CtMethod getTranslatableByLocale = CtMethod.make(
              "public static " + className + " get" + className + "ByLocale(String urlId, Locale locale) {"
              + className + " translatable = " + className + ".get" + className + "ByUrlId(urlId);"
              + "return (translatable == null) ? null "
              + ": MongoEntity.getDs().find(" + className + ".class, \"reference\", translatable.reference).filter(\"language =\", locale).get();"
              + "}", ctClass);
      ctClass.addMethod(getTranslatableByLocale);

      CtMethod getFirstTranslatableByReference = CtMethod.make(
              "public static " + className + " getFirst" + className + "ByReference(TranslatableRef translatableRef) {"
              + "return MongoEntity.getDs().find(" + className + ".class, \"reference\", translatableRef).get();"
              + "}", ctClass);
      ctClass.addMethod(getFirstTranslatableByReference);

      CtMethod getTranslatablesByReference = CtMethod.make(
              "public static java.util.List<" + className + "> get" + className + "sByReference(TranslatableRef translatableRef) {"
              + "return MongoEntity.getDs().find(" + className + ".class, \"reference\", translatableRef).asList();"
              + "}", ctClass);
      ctClass.addMethod(getTranslatablesByReference);

      ac.enhancedByteCode = ctClass.toBytecode();
      ctClass.defrost();
   }
}
