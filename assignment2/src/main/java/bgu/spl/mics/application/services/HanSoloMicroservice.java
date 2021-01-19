package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Ewoks;

/**
 * HanSoloMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {

    public HanSoloMicroservice() {
        super("Han");
    }


    @Override
    protected void initialize() {
        subscribeToTerminate();
        // subscribe and give the callback for the attack events
        subscribeEvent(AttackEvent.class, (AttackEvent eve)->{
            diary.setTotalAttacks();
            Ewoks.getInstance().acquire(eve.getAttack().getSerials()); // acquire the ewoks needed for the attack
            try {
                Thread.sleep(eve.getAttack().getDuration());
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            complete(eve, true);
            Ewoks.getInstance().release(eve.getAttack().getSerials());
            diary.setHanSoloFinish(System.currentTimeMillis());
        });
    }

    protected void setTerminationTime () {
        diary.setHanSoloTerminate(System.currentTimeMillis());
    }
}
