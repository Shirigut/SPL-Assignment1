package bgu.spl.mics.application.passiveObjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private static ArrayList <Ewok> ewoks;

    // Singleton thread-safe implementation
    private static class EwoksHolder {
        private static Ewoks instance= new Ewoks();
    }

    private Ewoks() {
        ewoks= new ArrayList<>();
    }

    public static Ewoks getInstance() {
      return EwoksHolder.instance;
    }

    // create list of ewoks from input. serial number as the number of ewoks
    public static void setEwoks(int numOfE) {
        for (int i = 0; i < numOfE; i++) {
            ewoks.add(new Ewok(i + 1));
        }
    }

    public void acquire(List<Integer> serials) {
        Collections.sort(serials);
        // sort the ewoks so MS would try to acquire them by numerical order to
        // prevent deadlocks
        for (int i=0; i<serials.size();i++) {
            ewoks.get(serials.get(i)-1).acquire();
        }
    }

    public void release(List<Integer> serials) {
        for (int i=0; i<serials.size();i++) {
            ewoks.get(serials.get(i)-1).release();
        }
    }
}