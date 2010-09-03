package controllers;

import models.Tag;
import models.blog.Post;
import org.bson.types.ObjectId;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.libs.Images.Captcha;
import play.mvc.Controller;

/**
 *
 * @author clementnivolle
 * @author keruspe
 */
public class BlogController extends Controller {

    public static void captcha(String id) {
        Captcha captcha = Images.captcha();
        Cache.set(id, captcha.getText(), "10min");
        renderBinary(captcha);
    }

    public static void show(ObjectId id) {
        render(Post.getPost(id), Codec.UUID());
    }

    public static void index() {
        // TODO: index
        /*PostRef frontPostRef = PostRef.find("Order by postedAt desc").first();
        List<PostRef> olderPostRefs =  PostRef.find("Order by postedAt desc").from(1).fetch(10);

        List<Locale> locales = I18nController.getBrowserLanguages();
        Post frontPost = frontPostRef.getPost(locales);
        List<Post> olderPosts = new ArrayList<Post>();
        for(PostRef postRef : olderPostRefs) {
            olderPosts.add(postRef.getPost(locales));
        }

        render(frontPost, olderPosts);*/
    }

    public static void postComment(Long postId, String email, String pseudo, String password, String content, String code, String randomID) {
        // TODO: Adapt postComment
        Post post = null; //Post.findById(postId);
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
        render(Tag.findOrCreateByName(tag), Post.findTaggedWith(tag));
    }

}
