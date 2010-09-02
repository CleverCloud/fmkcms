/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import elasticsearch.IndexJob;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.Future;
import models.Page;
import models.TestEntity;
import play.mvc.Controller;

/**
 *
 * @author waxzce
 */
public class esTest extends Controller {

    public static void index() {
        if (request.isNew) {
            TestEntity report = new TestEntity();
            report.name = "qsdfqsdfqsdfsqdfqsdfqsdf";
            Future<String> task = new IndexJob(report, "report", "1").now();
            request.args.put("task", task);


        }
        System.out.println("qsdfqsdf");
        renderText((Future<String>) request.args.get("task"));
    }

    public static void page() {
        Page p = new Page();
        p.content = "sdfgsdfgsdfg";
        p.title = "mon titre";
        p.language = new Locale("fr");
        p = p.save();
        renderJSON(p);
    }
}
