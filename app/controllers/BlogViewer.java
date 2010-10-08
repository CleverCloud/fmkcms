package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import models.Tag;
import models.blog.Comment;
import models.blog.Post;
import models.blog.PostRef;
import models.user.CommentUser;
import mongo.MongoEntity;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Images;
import play.libs.Images.Captcha;
import play.mvc.Controller;

/**
 *
 * @author keruspe
 */
public class BlogViewer extends Controller {

    public static void captcha(String id) {
        Captcha captcha = Images.captcha();
        Cache.set(id, captcha.getText().toLowerCase(), "10min");
        renderBinary(captcha);
    }

    public static void show(String title) {
        List<Post> posts = Post.getPostsByTitle(title);
        Post post = null;

        switch (posts.size()) {
            case 0:
                notFound();
            case 1:
                post = posts.get(0);
                break;
            default:
                List<Locale> locales = I18nController.getLanguages();
                for (Locale locale : locales) {
                    // Try exact Locale or exact language no matter the country
                    for (Post candidat : posts) {
                        if (candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) {
                            post = candidat;
                            break;
                        }
                    }
                    if (post != null)
                        break;
                }
                if (post == null)
                    post = posts.get(0);
                if (post == null)
                    notFound();
        }

        if (session.get("username") != null)
            render("BlogController/show.html", post, Codec.UUID());

        Boolean isConnected = session.contains("username");
        render(post, Codec.UUID(), isConnected);
    }

    public static void index() {
        PostRef frontPostRef = MongoEntity.getDs().find(PostRef.class).order("-postedAt").get();
        List<PostRef> olderPostRefs =  MongoEntity.getDs().find(PostRef.class).order("-postedAt").offset(1).limit(10).asList();

        Post frontPost = (frontPostRef == null) ? null : BlogViewer.getTranslation(frontPostRef);
        List<Post> olderPosts = new ArrayList<Post>();
        for(PostRef postRef : olderPostRefs) {
            olderPosts.add(BlogViewer.getTranslation(postRef));
        }
        render(frontPost, olderPosts);
    }

    public static void listTagged(String tag) {
        render(Tag.findOrCreateByName(tag), Post.findTaggedWith(tag));
    }

    public static void postComment(String title, String email, String userName, String webSite, String content, String code, String randomID) {
        Post post = Post.getPostByTitle(title);
        if (post == null)
            return;

        validation.equals(code.toLowerCase(), Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        if (Validation.hasErrors())
            render("BlogViewer/show.html", post, randomID);

        CommentUser user = new CommentUser();
        user.email = email;
        user.userName = userName;
        user.webSite = webSite;

        validation.valid(user);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            BlogViewer.show(title);
        }

        Comment comment = new Comment();
        comment.content = content;
        comment.user = user.save();
        comment.postedAt = new Date();

        validation.valid(comment);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            BlogViewer.show(title);
        }
        comment.save();

        post.addComment(comment);
        Cache.delete(randomID);
        BlogViewer.show(post.title);
    }

    private static Post getTranslation(PostRef postRef) {
        List<Post> posts = Post.getPostsByPostRef(postRef);
        if (posts == null)
            return null;

        switch (posts.size()) {
            case 0:
                return null;
            case 1:
                return posts.get(0);
            default:
                List<Locale> locales = I18nController.getLanguages();
                for (Locale locale : locales) {
                    // Try exact Locale or exact language no matter the country
                    for (Post candidat : posts) {
                        if (candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage())))
                            return candidat;
                    }
                }

                return posts.get(0);
        }

    }

}
