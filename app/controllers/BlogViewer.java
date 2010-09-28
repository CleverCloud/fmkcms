package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Tag;
import models.blog.Post;
import models.blog.PostRef;
import mongo.MongoEntity;
import play.cache.Cache;
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

}
