package controllers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import job.SecureStartJob;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@With(CheckRights.class)
public class SecureManager extends Controller {

    public static void reloadPerms() throws InterruptedException, ExecutionException {
        if (request.isNew) {
            Future<String> task = new SecureStartJob().now();
            request.args.put("task", task);
            waitFor(task);
        }
        renderText(((Future<String>) request.args.get("task")).get());

    }
}
