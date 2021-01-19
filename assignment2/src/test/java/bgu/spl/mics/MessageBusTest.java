package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest { //changed all of the methods

    private static MessageBusImpl Bus;

    public class TestBroadcast implements Broadcast {
    }

    public class TestMicroservice extends MicroService {
        private int counter;

        public TestMicroservice(String name) {
            super(name);
            counter = 0;
        }

        @Override
        protected void initialize() {
        }

    }

    @BeforeAll
    public static void setUp() {
        Bus= MessageBusImpl.getInstance();
    }

    @Test
    void complete() {
        List<Integer> serials = new ArrayList<>();
        serials.add(1);
        Attack attack = new Attack(serials, 1000);
        Event e = new AttackEvent(attack);
        TestMicroservice m = new TestMicroservice("Test");
        Bus.register(m);
        m.subscribeEvent(AttackEvent.class, (AttackEvent event) -> {
        });
        Future future = Bus.sendEvent(e);
        assertFalse(future.isDone());
        assertNull(future.get(100, TimeUnit.MILLISECONDS));
        Bus.complete(e, true);
        assertTrue(future.isDone());
        assertNotNull(future.get());
        Bus.unregister(m);
    }

    @Test
        //checks that the relevant microservice that subscribes to a broadcast, gets it.
    void sendBroadcast() throws InterruptedException {
        TestMicroservice m1 = new TestMicroservice("m1");
        Broadcast broadcast1 = new TestBroadcast();
        Broadcast broadcast2 = new TestBroadcast();
        Bus.register(m1);
        m1.subscribeBroadcast(TestBroadcast.class, (TestBroadcast broadcast) -> {
        });
        m1.sendBroadcast(broadcast1);
        m1.sendBroadcast(broadcast2);
        Message message1 = Bus.awaitMessage(m1);
        assertNotNull(message1);
        Message message2 = Bus.awaitMessage(m1);
        assertNotNull(message2);//checks that the microservice got the broadcast
        Bus.unregister(m1);
    }

    @Test
    void sendEvent() throws InterruptedException {
        TestMicroservice m2 = new TestMicroservice("m2");
        List<Integer> serials = new ArrayList<>();
        serials.add(1);
        Attack attack = new Attack(serials, 1000);
        Event e = new AttackEvent(attack);
        Bus.register(m2);
        m2.subscribeEvent(AttackEvent.class, (AttackEvent attackE) -> {
        });
        m2.sendEvent(e);
        Message message = Bus.awaitMessage(m2);
        assertNotNull(message);//checks that the microservice got the event
        Bus.unregister(m2);
    }

    @Test
    void awaitMessage() {
        TestMicroservice m3 = new TestMicroservice("m3");
        Message message = null;
        try { //Not registered
            try {
                message = Bus.awaitMessage(m3);
            } catch (NullPointerException nullE) {
                System.out.println("passed null test: microservice is not registered");
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Bus.register(m3);
            m3.subscribeEvent(AttackEvent.class, (AttackEvent attack) -> {
            });
            List<Integer> list = new ArrayList<>();
            list.add(1);
            Attack attack = new Attack(list, 1000);
            Event e = new AttackEvent(attack);
            m3.sendEvent(e);
            try {
                message = Bus.awaitMessage(m3);
            } catch (InterruptedException ex2) {
                ex2.printStackTrace();
            }
            assertNotNull(message); //checks that the microservice got the message
        }
    }
}
