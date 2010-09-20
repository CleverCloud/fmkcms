package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import models.Tag;
import models.blog.Post;
import models.blog.PostRef;
import models.blog.User;
import mongo.MongoEntity;
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
        PostRef frontPostRef = MongoEntity.getDs().find(PostRef.class).order("-postedAt").get();
        List<PostRef> olderPostRefs =  MongoEntity.getDs().find(PostRef.class).order("-postedAt").offset(1).limit(10).asList();

        List<Locale> locales = I18nController.getBrowserLanguages();
        Post frontPost = (frontPostRef == null) ? null : frontPostRef.getPost();
        List<Post> olderPosts = new ArrayList<Post>();
        for(PostRef postRef : olderPostRefs) {
            olderPosts.add(postRef.getPost());
        }

        render(frontPost, olderPosts);
    }

    public static void postComment(Long postId, String email, String pseudo, String password, String content, String code, String randomID) {
        Post post = MongoEntity.getDs().find(Post.class, "id", postId).get();
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

    public static void newPost() {
        render();
    }

    public static void doNewPost() {
        String otherTitle = params.get("linkTo.title");
        String title = params.get("post.title");
        String content = params.get("post.content");
        Locale language = params.get("post.language", Locale.class);
        Date postedAt = new Date();
        /* TODO: handle author
         User author = params.get("post.author"); */

        Post post = Post.getPostByTitle(otherTitle);
        if (post != null) {
            post = post.addTranslation(null, language, title, content); // TODO: handle author
        } else {
            System.out.println("Kikoo");
            PostRef postRef = BlogController.doNewPostRef(params.get("postReference.tags"), postedAt, null); // TODO: handle author
            validation.valid(postRef);
            if (Validation.hasErrors()) {
                params.flash(); // add http parameters to the flash scope
                Validation.keep(); // keep the errors for the next request
                BlogController.newPost();
            }

            post = new Post();
            post.author = null; // TODO: handle author
            post.content = content;
            post.title = title;
            post.postedAt = postedAt;
            post.postReference = postRef.save();
            post.language = language;
            validation.valid(post);
            if (Validation.hasErrors()) {
                params.flash(); // add http parameters to the flash scope
                Validation.keep(); // keep the errors for the next request
                BlogController.newPost();
            } else
                post.save();
        }

        BlogController.index();
    }

    private static PostRef doNewPostRef(String tagsString, Date postedAt, User author) {
        PostRef postRef = new PostRef();
        Set<Tag> tags = new TreeSet<Tag>();

        if (!tagsString.isEmpty()) {
            for (String tag : Arrays.asList(tagsString.split(","))) {
                tags.add(Tag.findOrCreateByName(tag));
            }
        }

        postRef.tags = tags;
        postRef.author = author;
        postRef.postedAt = postedAt;
        return postRef;
    }

}
