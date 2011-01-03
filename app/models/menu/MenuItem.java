package models.menu;

import com.google.code.morphia.annotations.Reference;
import com.google.code.morphia.annotations.Transient;
import models.menu.items.MenuItem_ControllerChain;
import models.menu.items.MenuItem_LinkToPage;
import models.menu.items.MenuItem_OutgoingURL;
import models.menu.items.MenuItem_Title;
import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 *
 * @author keruspe
 */
public abstract class MenuItem extends MongoEntity {

    @Reference
    public Menu menu;
    public String displayStr;
    public String cssLinkClass;
    @Transient
    private String classname = getClass().getCanonicalName();

    public MenuItem() {
    }

    public String getClassname() {
        return classname;
    }

    public MenuItem(String displayStr) {
        this.displayStr = displayStr;
    }

    public MenuItem(String displayStr, Menu menu) {
        this.displayStr = displayStr;
        this.menu = menu;
    }

    public abstract String getLink();

    public void setMenu(Menu menu, Menu parent) {
        if (menu != null && menu.isTree(this, parent)) {
            this.menu = menu;
        }
    }

    public static MenuItem getByMongodStringId(String id) {
        MenuItem mi = MongoEntity.getDs().get(MenuItem.class, new ObjectId(id));
        if (mi == null) {
            mi = MongoEntity.getDs().get(MenuItem_ControllerChain.class, new ObjectId(id));
        }
        if (mi == null) {
            mi = MongoEntity.getDs().get(MenuItem_LinkToPage.class, new ObjectId(id));
        }
        if (mi == null) {
            mi = MongoEntity.getDs().get(MenuItem_OutgoingURL.class, new ObjectId(id));
        }
        if (mi == null) {
            mi = MongoEntity.getDs().get(MenuItem_Title.class, new ObjectId(id));
        }
        return mi;
    }
}
