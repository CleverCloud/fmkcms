import job.WritePages;
import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

public class WritePagesNow extends FunctionalTest {

    
    @Test
    public void savePages() {
        new WritePages().now();
    }
}