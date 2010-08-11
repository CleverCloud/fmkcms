/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
        return title;
    }

    public String getLink() {
        return url;
    }

}
