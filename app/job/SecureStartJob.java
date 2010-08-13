package job;

import com.google.gson.Gson;
import java.io.FileReader;
import models.secure.Roles;
import models.secure.SecurityLevel;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import play.Logger;
import play.cache.Cache;
import play.jobs.Every;

/**
 *
 * @author waxzce
 */
@OnApplicationStart
@Every("3d")
public class SecureStartJob extends Job<String> {

    private static Map<String, String> cacheMap = new HashMap<String, String>();

    private void fluchCache() {
        for (Map.Entry<String, String> entry : SecureStartJob.cacheMap.entrySet()) {
            Cache.delete("SECURE_" + entry.getKey());
        }
    }

    @Override
    public String doJobWithResult() throws Exception {
        this.fluchCache();
        // make a gson object
        Gson gson = new Gson();
        // find separator usefull in m$ window case
        String separator = System.getProperties().getProperty("file.separator");
        String pathSecurityLevels = Play.applicationPath + separator + "conf" + separator + "SecurityLevels.json";
        // find security levels
        Type listOfSecurityLevelType = new TypeToken<List<SecurityLevel>>() {}.getType();
        List<SecurityLevel> securityLevelList = gson.fromJson(new FileReader(pathSecurityLevels), listOfSecurityLevelType);

        // now find Roles
        Type listOfRolesType = new TypeToken<List<Roles>>() {}.getType();
        String pathRoles = Play.applicationPath + separator + "conf" + separator + "roles.json";
        List<Roles> listOfRoles = gson.fromJson(new FileReader(pathRoles), listOfRolesType);
        Map<String, String> rightsMap = new HashMap<String, String>();
        SecureStartJob.cacheMap = new HashMap<String, String>();

        for (Roles role : listOfRoles) {
            for (String string : role.rights_names) {
                rightsMap.put(string, role.name);
            }
        }

        for (SecurityLevel securityLevel : securityLevelList) {
            for (String controllerName : securityLevel.controllers) {
                if (controllerName.contains("*")) {
                    try {
                        controllerName = controllerName.substring(0, controllerName.length() - 2);
                        Class c = Class.forName("controllers." + controllerName);
                        for (Method method : Arrays.asList(c.getMethods())) {
                            SecureStartJob.cacheMap.put(controllerName + "." + method.getName(), rightsMap.get(securityLevel.name));
                        }
                    } catch (ClassNotFoundException e) {
                        Logger.error("invalid rules : %s", controllerName);
                    }
                } else {
                    SecureStartJob.cacheMap.put(controllerName, rightsMap.get(securityLevel.name));
                }
            }
        }

        for (Map.Entry<String, String> entry : SecureStartJob.cacheMap.entrySet()) {
            Cache.add("SECURE_" + entry.getKey(), entry.getValue(), "100d");
        }
        
        return SecureStartJob.cacheMap.toString();
    }
}
