package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.BombDestroyerEvent;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.ArrayList;


/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent]}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {

    private Attack[] attacks;
    private ArrayList<Future> futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
        this.attacks = attacks;
        futures = new ArrayList<>();
    }

    @Override
    protected void initialize() {
        try {
            Thread.sleep(300);   //sleeps until someone is subscribed for sure
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        // send the attack events to the MessageBus
        for (int i = 0; i < attacks.length; i++) {
            AttackEvent attack = new AttackEvent(attacks[i]);
            Future future = sendEvent(attack);
            futures.add(future);

        }
        subscribeToTerminate();
        // tries to get the result of the events to know if they finished. if not she would wait for them.
        // when they finish she can send the deactivation event
        //for (int i = 0; i < futures.size(); i++) {
        //        futures.get(i).get();
        //}
	for(Future f:futures){
		f.get();
	}
            Event<Boolean> deactivate = new DeactivationEvent();
            Future deactivateFuture = sendEvent(deactivate);
            futures.add(deactivateFuture);
        // tries to get the result of the deactivation. if not she would wait for it to finish.
        // when it finish she can send the deactivation event
            deactivateFuture.get();
            Event<Boolean> bombEvent = new BombDestroyerEvent();
            sendEvent(bombEvent);
        }

        protected void setTerminationTime () {
            diary.setLeiaTerminate(System.currentTimeMillis());
        }
    }
