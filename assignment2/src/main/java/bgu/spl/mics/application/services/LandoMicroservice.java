package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;


/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;


    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration=duration;
    }

    @Override
    protected void initialize() {
        subscribeToTerminate();
        // subscribe and give the callback for the BombDestroy event
        subscribeEvent(BombDestroyerEvent.class, (BombDestroyerEvent bombDestroy) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            complete(bombDestroy, true);
            // finished the last event, call the other MS to terminate
            sendBroadcast(new TerminationBroadcast());
        });

    }
    protected void setTerminationTime () {
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
