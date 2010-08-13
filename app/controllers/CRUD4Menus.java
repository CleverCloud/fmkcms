package controllers;

import models.*;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@CRUD.For(Menu.class)
@With(CheckRights.class)
public class CRUD4Menus extends CRUD {
}
