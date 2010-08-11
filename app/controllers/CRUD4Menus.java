/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import models.*;
import play.mvc.Before;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@CRUD.For(Menu.class)
@With(CheckRights.class)
public class CRUD4Menus extends CRUD {

   
}
