package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        Cache.set(id, captcha.getText(), "10min");
        renderBinary(captcha);
    }

    public static void show(String title) {
        if (session.get("username") != null)
            BlogController.show(title);
        Post post = Post.getPostByTitle(title);
        render(post, Codec.UUID());
    }

    public static void index() {
        PostRef frontPostRef = MongoEntity.getDs().find(PostRef.class).order("-postedAt").get();
        List<PostRef> olderPostRefs =  MongoEntity.getDs().find(PostRef.class).order("-postedAt").offset(1).limit(10).asList();

        Post frontPost = (frontPostRef == null) ? null : frontPostRef.getPost();
        List<Post> olderPosts = new ArrayList<Post>();
        for(PostRef postRef : olderPostRefs) {
            olderPosts.add(postRef.getPost());
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

        validation.equals(code, Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
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

}
