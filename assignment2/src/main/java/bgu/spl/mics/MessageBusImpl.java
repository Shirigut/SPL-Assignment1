package bgu.spl.mics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
    //saves for each message type  the microservices that interested in it
    private ConcurrentHashMap<Class<? extends Message>, BlockingQueue<MicroService>> subscribes;
    //saves the events with their future objects
    private ConcurrentHashMap<Event, Future> futures;
    //saves the microservices with their message's queues
    private ConcurrentHashMap<MicroService, BlockingQueue<Message>> queues;
    //saves the microservices with the events they subscribes to
    private HashMap<MicroService, List<Class<? extends Message>>> eventTypes;
    private static Object Lock1 = new Object(); //lock for subscribes
    private static Object Lock2 = new Object(); //lock for queues

    // Singleton thread-safe implementation
    public static class messageBusImplHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        subscribes = new ConcurrentHashMap<>();
        futures = new ConcurrentHashMap<>();
        queues = new ConcurrentHashMap<>();
        eventTypes = new HashMap<>();
    }


    public static MessageBusImpl getInstance() {
        return messageBusImplHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (type) {
            //this is the first microservice that subscribes to this event type so create the queue
            if (subscribes.get(type) == null) {
                BlockingQueue<MicroService> arr = new LinkedBlockingQueue<>();
                arr.add(m);
                subscribes.put(type, arr);
            } else {
                subscribes.get(type).add(m);
            }
            //this is the first subscription of this microservice
            if (eventTypes.get(m)==null)
                eventTypes.put(m, new LinkedList<>());
            eventTypes.get(m).add(type);
            type.notifyAll();
        }
    }


    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (type) {
            //this is the first microservice that subscribes to this broadcast type so create the queue
            if (!subscribes.containsKey(type)) {
                BlockingQueue<MicroService> arr = new LinkedBlockingQueue<>();
                arr.add(m);
                subscribes.putIfAbsent(type, arr);
                eventTypes.put(m, new LinkedList<>());
            } else {
                subscribes.get(type).add(m);
            }
            //this is the first subscription of this microservice
            if (eventTypes.get(m)==null)
                eventTypes.put(m, new LinkedList<>());
            eventTypes.get(m).add(type);
            type.notifyAll();
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        futures.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        synchronized (Lock1) {
            //get all the microservices that subscribes to this broadcast type
            BlockingQueue<MicroService> arr = subscribes.get(b.getClass());
            if (arr == null || arr.isEmpty())
                return;
            //put the broadcast in all the queues of the subscribed microservices
            for (MicroService m : arr) {
                synchronized (Lock2) {
                    try {
                        queues.get(m).put(b);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> newFuture = new Future<>();
        synchronized (Lock1) {
            if (subscribes.get(e.getClass()) == null || subscribes.get(e.getClass()).isEmpty()) {
                return null;
            }
            MicroService m;
            try {
                synchronized (Lock2) {
                    //round robin- takes the first microservice that subscribes to this event class
                    m = subscribes.get(e.getClass()).take();
                    if (queues.get(m)==null) {
                        return null;
                    }
                    futures.put(e, newFuture);
                    //put the new event in his queue
                    queues.get(m).put(e);
                    //add this microservice to the end of the event class's queue
                    subscribes.get(e.getClass()).put(m);
                }
            } catch (InterruptedException ignored) {}
        }
        return newFuture;
    }

    @Override
    public void register(MicroService m) {
        //creates new message queue for m
        queues.putIfAbsent(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {

        synchronized (Lock1) {
            //delete references
            for (int i = 0; i < eventTypes.get(m).size(); i++) {
                BlockingQueue<MicroService> arr = subscribes.get(eventTypes.get(m).get(i));
                arr.remove(m);
            }
            eventTypes.remove(m);

        synchronized (Lock2) {
            //delete queue
            queues.remove(m);
        }
    }
}

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        Message message = null;
        if (queues.get(m) == null)
            throw new NullPointerException("m is not registered");
        //tries to take the message from the queue
        try {
            message = queues.get(m).take();
        } catch (InterruptedException ex) {
            System.out.println("interrupted while waiting");
        }
        return message;
    }
}
