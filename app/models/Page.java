/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 */
@Entity
public class Page extends Model {

    @Required
    public String title;
    @Required
    @Lob
    @MaxSize(60000)
    public String content;
    @Required
    public String urlid;

    public static Page getByUrlid(String urlid) {
        return Page.find("urlid = ?", urlid).first();
    }
}
