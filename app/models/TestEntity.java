/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import org.bson.types.ObjectId;

/**
 *
 * @author waxzce
 */
@Entity
public class TestEntity {

    @Id
    public ObjectId id;
    public String name;
}
