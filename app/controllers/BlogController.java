package controllers;

import com.google.gson.Gson;
import java.util.List;
import models.blog.Post;
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
        Post post = Post.findById(id);
        String randomID = Codec.UUID();
        render(post, randomID);
    }

    public static void index() {
        Post frontPost = Post.find("Order by postedAt desc").first();
        List<Post> olderPosts = Post.find("Order by postedAt desc").from(1).fetch(10);

        render(frontPost, olderPosts);
    }

    public static void postComment(Long postId, String email, String pseudo, String password, String content, String code, String randomID) {
        Post post = Post.findById(postId);
        if (post == null)
            return;

        validation.equals(code, Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        if (Validation.hasErrors())
            render("BlogController/show.html", Post.findById(postId), randomID);

        post.getData(I18nController.getBrowserLanguages()).addComment(email, pseudo, password, content);
        Cache.delete(randomID);
        BlogController.show(postId);
    }

    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }

    public static void jsondump(){
        renderJSON(new Gson().toJson(Post.all().fetch()));
    }

}