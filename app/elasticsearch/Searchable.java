package elasticsearch;

/**
 *
 * @author waxzce
 */
public interface Searchable {

    public String getPrintTitle();
    public String getPrintDesc();
    public String getPrintURL();
    public float getScore();
    public void setScore(float score);


}
