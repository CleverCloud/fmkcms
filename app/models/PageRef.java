package models;

import com.google.code.morphia.annotations.Entity;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import mongo.MongoEntity;
import play.data.validation.Required;

/**
 *
 * @author keruspe
 */
@Entity
public class PageRef extends MongoEntity {

    @Required
    public String urlId;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Tag> tags;
    
}
