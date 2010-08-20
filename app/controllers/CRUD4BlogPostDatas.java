package controllers;

import models.blog.PostData;
import play.mvc.With;

/**
 *
 * @author keruspe
 */
@CRUD.For(PostData.class)
@With(CheckRights.class)
public class CRUD4BlogPostDatas extends CRUD {
}
