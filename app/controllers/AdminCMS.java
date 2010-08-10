/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author waxzce
 */
@With(CheckRights.class)
public class AdminCMS extends Controller{

    public static void index(){
        render();
    }
}
