/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models.i18n;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import mongo.MongoEntity;
import org.bson.types.ObjectId;

/**
 *
 * @author waxzce
 */
public class TranslatableManager<T extends Translatable, R extends TranslatableRef<T, R>> {

   private Class t;
   private Class tr;

   public TranslatableManager(Class<T> t, Class<R> tr) {
      this.t = t;
      this.tr = tr;
   }

  
   
   public T getByMongodId(String id) {
      return (T) MongoEntity.getDs().find(t, "id", new ObjectId(id)).get();
   }
   
   public R getRefByMongodId(String id) {
      return (R) MongoEntity.getDs().find(tr, "id", new ObjectId(id)).get();
   }
}
