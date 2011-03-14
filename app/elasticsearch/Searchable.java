package elasticsearch;

import org.bson.types.ObjectId;

/**
 *
 * @author waxzce
 */
public interface Searchable {

   public String getPrintTitle();

   public String getPrintDesc();

   public String getPrintURL();

   public ObjectId getEntityId();

   public float getScore();

   public void setScore(float score);
}
