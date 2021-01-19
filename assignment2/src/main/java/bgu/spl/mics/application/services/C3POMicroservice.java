package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.HashMap;


/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {

    public C3POMicroservice() {
        super("C3PO");
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
            diary.setC3POFinish(System.currentTimeMillis());
        });

    }

    protected void setTerminationTime () {
        diary.setC3POTerminate(System.currentTimeMillis());
    }
}

