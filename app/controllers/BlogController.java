package controllers;

import com.google.gson.Gson;
import java.util.List;
import models.blog.PostRef;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;

/**
 *
 * @author clementnivolle
 */
public class BlogController extends Controller {

    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText();
        Cache.set(id, code, "10min");
        renderBinary(captcha);
    }

    public static void show(Long id) {
        PostRef postRef = PostRef.findById(id);
        String randomID = Codec.UUID();
        render(postRef, randomID);
    }

    public static void index() {
        PostRef frontPost = PostRef.find("Order by postedAt desc").first();
        List<PostRef> olderPosts = PostRef.find("Order by postedAt desc").from(1).fetch(10);

        render(frontPost, olderPosts);
    }

    public static void postComment(Long postId, String email, String pseudo, String password, String content, String code, String randomID) {
        PostRef postRef = PostRef.findById(postId);
        if (postRef == null)
            return;

        validation.equals(code, Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        if (Validation.hasErrors())
            render("BlogController/show.html", PostRef.findById(postId), randomID);

        postRef.getPost(I18nController.getBrowserLanguages()).addComment(email, pseudo, password, content);
        Cache.delete(randomID);
        BlogController.show(postId);
    }

    public static void listTagged(String tag) {
        List<PostRef> posts = PostRef.findTaggedWith(tag);
        render(tag, posts);
    }

    public static void jsondump() {
        renderJSON(new Gson().toJson(PostRef.all().fetch()));
    }
}
