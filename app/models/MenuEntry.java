package models;

import javax.persistence.Entity;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 */
@Entity
public abstract class MenuEntry extends Model {

    public abstract String getTitle() ;

    public abstract String getLink() ;
}
