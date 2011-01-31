package controllers;

import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import models.Tag;
import models.blog.Post;
import models.blog.PostRef;
import models.user.GAppUser;
import models.user.User;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.mvc.With;
import play.vfs.VirtualFile;

/**
 *
 * @author clementnivolle
 * @author keruspe
 */
@With(Secure.class)
public class BlogController extends Controller {

   public static void deletePost_confirm(String urlId, String language) {
      Post post = Post.getPostByLocale(urlId, new Locale(language));
      render(post);
   }

   public static void deletePost(String urlId, String language) {
      Post post = Post.getPostByLocale(urlId, new Locale(language));
      if (post == null) {
         return;
      }
      PostRef postRef = post.postReference;
      post.delete();
      if (Post.getFirstPostByPostRef(postRef) == null) {
         postRef.delete();
      }
      BlogViewer.index();
   }

   public static void newPost() {
      renderArgs.put("action", "create");
      render();
   }

   public static void edit(String urlId, String language) {
      Post otherPost = Post.getPostByLocale(urlId, new Locale(language));
      renderArgs.put("otherPost", otherPost);
      renderArgs.put("action", "edit");
      String overrider = null;
      for (Post p : Post.getPostsByPostRef(otherPost.postReference)) {
         overrider = "/view/PageEvent/edit/" + p.urlId + ".html";
         if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
            break;
         }
      }
      if (!VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
         overrider = "BlogController/newPost.html";
      }
      render(overrider);
   }

   public static void translate(String otherUrlId, String language) {
      Post otherPost = Post.getPostByLocale(otherUrlId, new Locale(language));
      renderArgs.put("otherPost", otherPost);
      renderArgs.put("action", "translate");
      String overrider = null;
      for (Post p : Post.getPostsByPostRef(otherPost.postReference)) {
         overrider = "/view/PageEvent/translate/" + p.urlId + ".html";
         if (VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
            break;
         }
      }
      if (!VirtualFile.fromRelativePath("app/views" + overrider).getRealFile().exists()) {
         overrider = "BlogController/newPost.html";
      }
      render(overrider);
   }

   public static void doNewPost(String actionz, String otherUrlId, String otherLanguage) {
      String urlId = params.get("post.urlId");
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

      Post post = Post.getPostByUrlId(otherUrlId);
      PostRef postRef = null;
      if (post != null) {
         postRef = post.postReference;
      } else {
         postRef = BlogController.doNewPostRef(params.get("postReference.tags"), postedAt, author, actionz, otherUrlId, otherLanguage);
      }

      if (!actionz.equals("edit"))
         post = new Post();
      post.postReference = postRef;
      post.author = author;
      post.content = content;
      post.urlId = urlId;
      post.title = title;
      post.postedAt = postedAt;
      post.language = language;

      validation.valid(post);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep(); // keep the errors for the next request
         if (actionz.equals("edit")) {
            BlogController.edit(urlId, otherLanguage);
         } else if (actionz.equals("translate")) {
            BlogController.translate(urlId, otherLanguage);
         } else {
            BlogController.newPost();
         }
      }
      post.postReference.save();
      post.save();

      BlogViewer.index();
   }

   private static PostRef doNewPostRef(String tagsString, Date postedAt, User author, String action, String otherUrlId, String otherLanguage) {
      PostRef postRef = new PostRef();
      Set<Tag> tags = new TreeSet<Tag>();
      Tag t = null;

      if (!tagsString.isEmpty()) {
         for (String tag : Arrays.asList(tagsString.split(","))) {
            t = Tag.findOrCreateByName(tag);
            if (!tags.contains(t)) {
               tags.add(t);
            }
         }
      }

      postRef.tags = tags;
      postRef.author = author;
      postRef.postedAt = postedAt;

      validation.valid(postRef);
      if (Validation.hasErrors()) {
         params.flash(); // add http parameters to the flash scope
         Validation.keep(); // keep the errors for the next request
         if (action.equals("edit")) {
            BlogController.edit(otherUrlId, otherLanguage);
         } else if (action.equals("translate")) {
            BlogController.translate(otherUrlId, otherLanguage);
         } else {
            BlogController.newPost();
         }
      }

      return postRef;
   }

}
