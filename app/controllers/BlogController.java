package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import models.blog.Post;
import models.blog.PostData;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;
import play.mvc.Http;

/**
 *
 * @author clementnivolle
 */
public class BlogController extends Controller {

    public static List<Locale> getBrowserLanguages() {
        List<String> languages = Http.Request.current().acceptLanguage();
        List<Locale> locales = new ArrayList<Locale>();

        Locale locale = null;
        for (String language : languages) {
            if (language.split("-").length == 2) {
                locale = new Locale(language.split("-")[0], language.split("-")[1]);
            } else {
                locale = new Locale(language.split("-")[0]);
            }

            locales.add(locale);
        }

        return locales;
    }

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

    public static void postComment(Long postId, Long postDataId, String author, String content, String code, String randomID) {
        PostData postData = PostData.findById(postDataId);

        validation.equals(code, Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        postData.addComment(author, content);
        if (Validation.hasErrors()) {
            render("BlogController/show.html", Post.findById(postId), randomID);
        }
        Cache.delete(randomID);
        BlogController.show(postId);
    }

    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        render(tag, posts);
    }

}