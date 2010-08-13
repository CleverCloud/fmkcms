package models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import play.db.jpa.Model;

/**
 *
 * @author waxzce
 */
@Entity
public class Menu extends Model{

    public String name;
    
    @OrderColumn
    @OneToMany
    public List<MenuEntry> entries;
}
