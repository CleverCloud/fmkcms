package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import models.Tag;
import models.blog.Post;
import models.blog.PostRef;
import mongo.MongoEntity;
import play.libs.Codec;
import play.mvc.Controller;

/**
 *
 * @author keruspe
 */
public class BlogViewer extends Controller {

    public static Post getGoodPost(List<Post> posts) {
        if (posts == null) {
            return null;
        }

        Post post = null;
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
                        if (candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) {
                            return candidat;
                        }
                    }
                }
                post = posts.get(0);
                if (post == null) {
                    return null;
                }
                return post;
        }
    }

    public static void show(String urlId) {
        List<Post> posts = Post.getPostsByUrlId(urlId);
        Post post = BlogViewer.getGoodPost(posts);

        if (post == null) {
            notFound();
        }

        String randomID = Codec.UUID();
        Boolean isConnected = session.contains("username");

        if (isConnected) {

            render("BlogController/show.html", post, randomID);
        }
        Post postNext = null;
        Post postPrevious = null;
        if (post.reference.next() != null) {
            postNext = controllers.BlogViewer.getTranslation(post.reference.next());
        }
        if (post.reference.previous() != null) {
            postPrevious = controllers.BlogViewer.getTranslation(post.reference.previous());
        }

        render(post, randomID, isConnected, postNext, postPrevious);
    }

    public static void last() {
        PostRef frontPostRef = MongoEntity.getDs().find(PostRef.class).order("-postedAt").get();
        Post post = BlogViewer.getTranslation(frontPostRef);

        if (post == null) {
            notFound();
        }

        String randomID = Codec.UUID();
        Boolean isConnected = session.contains("username");

        if (isConnected) {
            render("BlogController/show.html", post, randomID);
        }

        render("BlogViewer/show.html", post, randomID, isConnected);
    }

    public static void index() {
        PostRef frontPostRef = MongoEntity.getDs().find(PostRef.class).order("-postedAt").get();
        List<PostRef> olderPostRefs = MongoEntity.getDs().find(PostRef.class).order("-postedAt").offset(1).limit(10).asList();

        Post frontPost = (frontPostRef == null) ? null : BlogViewer.getTranslation(frontPostRef);
        List<Post> olderPosts = new ArrayList<Post>();
        for (PostRef postRef : olderPostRefs) {
            olderPosts.add(BlogViewer.getTranslation(postRef));
        }
        render(frontPost, olderPosts);
    }

    public static void listTagged(String tagName) {
        Tag tag = Tag.findOrCreateByName(tagName); /* avoid NPE in view ... */
        List<PostRef> postRefs = PostRef.findTaggedWith(tag);
        List<Post> posts = new ArrayList<Post>();
        Post post = null;
        for (PostRef postRef : postRefs) {
            post = BlogViewer.getGoodPost(Post.getPostsByPostRef(postRef));
            if (post != null) {
                posts.add(post);
            }
        }
        render(posts, tag);
    }

    public static Post getTranslation(PostRef postRef) {
        List<Post> posts = Post.getPostsByPostRef(postRef);
        if (posts == null) {
            return null;
        }

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
                        if (candidat.language.equals(locale) || (!locale.getCountry().equals("") && candidat.language.getLanguage().equals(locale.getLanguage()))) {
                            return candidat;
                        }
                    }
                }

                return posts.get(0);
        }

    }
}
