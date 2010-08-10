/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package job;

import com.google.gson.Gson;
import java.io.FileReader;
import models.secure.Roles;
import models.secure.SecurityLevel;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author waxzce
 */
@OnApplicationStart
public class SecureStartJob extends Job {

    @Override
    public void doJob() throws Exception {
        // make a gson object
        Gson gson = new Gson();
        // find separator usefull in m$ window case
        String separator = System.getProperties().getProperty("file.separator");
        String pathSecurityLevels = Play.applicationPath + separator + "conf" + separator + "SecurityLevels.json";
        // find security levels
        Type listOfSecurityLevelType = new TypeToken<List<SecurityLevel>>() {
        }.getType();
        List<SecurityLevel> securityLevelList = gson.fromJson(new FileReader(pathSecurityLevels), listOfSecurityLevelType);

        System.out.println(securityLevelList);
        // now find Roles
        Type listOfRolesType = new TypeToken<List<Roles>>() {
        }.getType();
        String pathRoles = Play.applicationPath + separator + "conf" + separator + "roles.json";
        List<Roles> listOfRoles = gson.fromJson(new FileReader(pathRoles), listOfRolesType);
        Map<String, String> rightsMap = new HashMap<String, String>();

        Iterator<SecurityLevel> itsll = securityLevelList.iterator();
        while (itsll.hasNext()) {
            SecurityLevel securityLevel = itsll.next();
            
        }

    }
}
