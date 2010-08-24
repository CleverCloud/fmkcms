package controllers;

import models.*;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@CRUD.For(Page.class)
@With(CheckRights.class)
public class CRUD4Pages extends CRUD {
}
