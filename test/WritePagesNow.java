import job.WritePages;
import org.junit.*;
import play.test.*;
import play.mvc.Http.*;

public class WritePagesNow extends FunctionalTest {

    
    @Test
    public void savePages() {
        new WritePages().now();
    }
}