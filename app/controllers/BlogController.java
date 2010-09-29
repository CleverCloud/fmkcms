package controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import models.Tag;
import models.blog.Comment;
import models.blog.Post;
import models.blog.PostRef;
import models.user.GAppUser;
import models.user.User;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Codec;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author clementnivolle
 * @author keruspe
 */
@With(Secure.class)
public class BlogController extends Controller {

    public static void show(String title) {
        Post post = Post.getPostByTitle(title);
        render(post, Codec.UUID());
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

        String openId = session.get("username");
        GAppUser author = GAppUser.getByOpenId(openId);
        if (author == null) {
            author = new GAppUser();
            author.openId = openId;
            author.firstName = session.get("firstName");
            author.lastName = session.get("lastName");
            author.userName = author.firstName + " " + author.lastName;
            author.email = session.get("email");
            author.language = new Locale(session.get("language"));

            validation.valid(author);
            if (Validation.hasErrors()) {
                unauthorized("Could not authenticate you");
            }
            author.save();
        }

        Post post = Post.getPostByTitle(otherTitle);
        PostRef postRef = null;
        if (post != null)
            postRef = post.postReference;
        else
            postRef = BlogController.doNewPostRef(params.get("postReference.tags"), postedAt, author);

        post = new Post();
        post.postReference = postRef;
        post.author = author;
        post.content = content;
        post.title = title;
        post.postedAt = postedAt;
        post.language = language;

        validation.valid(post);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            BlogController.newPost();
        }
        post.postReference.save();
        post.save();

        BlogViewer.index();
    }

    private static PostRef doNewPostRef(String tagsString, Date postedAt, User author) {
        PostRef postRef = new PostRef();
        Set<Tag> tags = new TreeSet<Tag>();
        Tag t = null;

        if (!tagsString.isEmpty()) {
            for (String tag : Arrays.asList(tagsString.split(","))) {
                t = Tag.findOrCreateByName(tag);
                if (!tags.contains(t))
                    tags.add(t);
            }
        }

        postRef.tags = tags;
        postRef.author = author;
        postRef.postedAt = postedAt;

        validation.valid(postRef);
        if (Validation.hasErrors()) {
            params.flash(); // add http parameters to the flash scope
            Validation.keep(); // keep the errors for the next request
            BlogController.newPost();
        }

        return postRef;
    }

    public static void postComment(String title, String content, String code, String randomID) {
        Post post = Post.getPostByTitle(title);
        if (post == null)
            return;

        validation.equals(code.toLowerCase(), Cache.get(randomID)).message("Wrong validation code. Please reload a nother code.");
        if (Validation.hasErrors())
            render("BlogController/show.html", post, randomID);

        GAppUser user = GAppUser.getByOpenId(session.get("username"));
        if (user == null) {
            user = new GAppUser();
            user.openId = session.get("username");
            user.firstName = session.get("firstName");
            user.lastName = session.get("lastName");
            user.userName = user.firstName + " " + user.lastName;
            user.email = session.get("email");
            user.language = new Locale(session.get("language"));

            validation.valid(user);
            if (Validation.hasErrors()) {
                unauthorized("Could not authenticate you");
            }
            user.save();
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
