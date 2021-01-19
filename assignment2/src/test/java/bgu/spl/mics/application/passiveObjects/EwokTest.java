package bgu.spl.mics.application.passiveObjects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EwokTest {

    private Ewok ewok;

    @BeforeEach
        public void setUp(){
        ewok = new Ewok(1);
    }

    @Test
    void acquireTest() {
        assertTrue(ewok.available);
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
    }

    @Test
    void releaseTest() {
        ewok.acquire();
        assertFalse(ewok.available);
        ewok.release();
        assertTrue(ewok.available);
    }
}
