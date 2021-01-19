package bgu.spl.mics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;


public class FutureTest { //changed all of the methods

    private Future<Boolean> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }

    @Test
    public void testGet()
    {
        assertFalse(future.isDone());
        assertNull(future.get(100, TimeUnit.MILLISECONDS));
        future.resolve(true);
        assert (future.get(100, TimeUnit.MILLISECONDS)!=null);
        assertTrue(future.isDone());
    }

    @Test
    public void testResolve(){
        Boolean result= true;
        assertNull(future.get(100, TimeUnit.MILLISECONDS));
        future.resolve(result);
        assertTrue(future.isDone());
        assertEquals(result, future.get(100, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testIsDone(){
        Boolean result= true;
        assertFalse(future.isDone());
        future.resolve(result);
        assertEquals(result, future.get(100, TimeUnit.MILLISECONDS));
        assertTrue(future.isDone());
    }

    @Test
    public void testGetWithTimeOut() throws InterruptedException
    {
        assertFalse(future.isDone());
        future.get(100,TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve(true);
        assertEquals(future.get(100,TimeUnit.MILLISECONDS),true);
    }
}
