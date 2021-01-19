package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */


public class Main {

    public static void main(String[] args) {
        //reading from json
        Gson gson = new Gson();
        Input input = null;
        try {
            Reader reader = new FileReader(args[0]);
            input = gson.fromJson(reader, Input.class);
        } catch (Exception ex) {
            System.out.println("File has not found");
        }
        if (input != null) {
            Ewoks.getInstance();
            Ewoks.setEwoks(input.getEwoks());
            Diary diary = Diary.getInstance();
            MessageBusImpl.getInstance();
            Thread leia;

            Thread hansolo = new Thread(new HanSoloMicroservice());
            Thread c3po = new Thread(new C3POMicroservice());
            Thread r2d2 = new Thread(new R2D2Microservice(input.getR2D2()));
            Thread lando = new Thread(new LandoMicroservice(input.getLando()));
            leia = new Thread(new LeiaMicroservice(input.getAttacks()));
            leia.start();
            hansolo.start();
            c3po.start();
            r2d2.start();
            lando.start();
            try {
                // main thread needs to wait for each microservice to terminate before writing output
                leia.join();
                hansolo.join();
                c3po.join();
                r2d2.join();
                lando.join();
            } catch (InterruptedException ex) {
            }

            //writing to json
            try {
                FileWriter writer = new FileWriter(args[1]);
                gson.toJson(diary, writer);
                writer.close();
            } catch (IOException e) {
            }
        }
    }
}

