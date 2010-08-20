package controllers;

import models.blog.Post;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
@CRUD.For(Post.class)
@With(CheckRights.class)
public class CRUD4BlogPosts extends CRUD {
}
