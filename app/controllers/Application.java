package controllers;

import job.WritePages;
import play.mvc.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void writePages() {
        new WritePages().now();

     //   redirect("/");
    }
}
