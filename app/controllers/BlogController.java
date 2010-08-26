package controllers;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import models.blog.Post;
import models.blog.PostRef;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Controller;

/**
 *
 * @author clementnivolle
 * @author keruspe
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
        Post post = postRef.getPost(I18nController.getBrowserLanguages());
        String randomID = Codec.UUID();
        render(post, randomID);
    }

    public static void index() {
        PostRef frontPostRef = PostRef.find("Order by postedAt desc").first();
        List<PostRef> olderPostRefs = PostRef.find("Order by postedAt desc").from(1).fetch(10);

        List<Locale> locales = I18nController.getBrowserLanguages();
        Post frontPost = frontPostRef.getPost(locales);
        List<Post> olderPosts = new ArrayList<Post>();
        for(PostRef postRef : olderPostRefs) {
            olderPosts.add(postRef.getPost(locales));
        }

        render(frontPost, olderPosts);
    }

    public static void postComment(Long postId, String email, String pseudo, String password, String content, String code, String randomID) {
        Post post = Post.findById(postId);
        if (post == null)
            return;

        validation.equals(code, Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        if (Validation.hasErrors())
            render("BlogController/show.html", post, randomID);

        post.addComment(email, pseudo, password, content);
        Cache.delete(randomID);
        BlogController.show(post.postReference.id);
    }

    public static void listTagged(String tag) {
        List<Post> posts = Post.findTaggedWith(tag);
        
        render(tag, posts);
    }

    public static void jsondump() {
        renderJSON(new Gson().toJson(PostRef.all().fetch()));
    }
}
