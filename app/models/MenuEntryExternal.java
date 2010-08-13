package models;

import javax.persistence.Entity;

/**
 *
 * @author waxzce
 */
@Entity
public class MenuEntryExternal extends MenuEntry{

    public String title;
    public String url;

    public String getTitle() {
        return this.title;
    }

    public String getLink() {
        return this.url;
    }

}
