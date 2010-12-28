package models.menu.items;

import models.menu.Menu;
import models.menu.MenuItem;
import play.mvc.Router;
import com.google.code.morphia.annotations.Transient;

/**
 *
 * @author keruspe
 */
public class MenuItem_ControllerChain extends MenuItem {

    public String controllerChain;

    public MenuItem_ControllerChain(String controllerChain, String displayStr, Menu menu) {
        super(displayStr, menu);
        this.controllerChain = controllerChain.trim();
    }

    public MenuItem_ControllerChain(String controllerChain, String displayStr) {
        super(displayStr);
        this.controllerChain = controllerChain.trim();
    }

    @Override
    public String getLink() {
        return Router.reverse(this.controllerChain).url;
    }
}
