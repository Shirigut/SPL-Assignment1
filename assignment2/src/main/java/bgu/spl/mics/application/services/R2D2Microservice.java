package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeactivationEvent;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
    }

    @Override
    protected void initialize() {
        subscribeToTerminate();
        // subscribe and give the callback for the deactivation event
        subscribeEvent(DeactivationEvent.class, (DeactivationEvent deacivate) -> {
            try {
                Thread.sleep(duration);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            complete(deacivate, true);
            diary.setR2D2Deactivate(System.currentTimeMillis());
        });

    }

    protected void setTerminationTime () {
        diary.setR2D2Terminate(System.currentTimeMillis());
    }
}