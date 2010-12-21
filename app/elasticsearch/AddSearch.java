package elasticsearch;

import java.util.List;
import mongo.MongoEntity;
import play.Play;
import play.classloading.ApplicationClasses;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.jobs.Job;

/**
 * Add all Searchable classes of the application to the index.
 * @author waxzce
 * @author Julien Durillon
 */
//@OnApplicationStart
public class AddSearch extends Job {

   @Override
   public void doJob() throws Exception {
      ApplicationClasses appClasses = Play.classes;
      List<ApplicationClass> classes = appClasses.all();

      for (ApplicationClass acl : classes) {
         for (Class inter : acl.javaClass.getInterfaces()) {
            if (inter.equals(Searchable.class)) {
               for (Object src : MongoEntity.getDs().createQuery(acl.javaClass).asList()) {
                  Searchable casted = (Searchable) src;
                  IndexJob indexJob = new IndexJob(casted,
                                                   acl.javaClass.getCanonicalName(),
                                                   casted.getEntityId().toStringMongod());
                  indexJob.doJobWithResult();
               }
               break; // Oui, c'est sale, et alors ? Au moins ça va vite ;)
            }
         }
      }
   }
}
