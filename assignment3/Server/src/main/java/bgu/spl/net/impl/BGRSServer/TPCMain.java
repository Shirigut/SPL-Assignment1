package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.*;
import java.util.function.Supplier;

public class TPCMain {
    public static void main(String[] args) {
        Database database = Database.getInstance();
        database.initialize("Courses.txt");
        int port = Integer.parseInt(args[0]);
        Supplier<MessagingProtocol<String>> protocolFactory = new Supplier<MessagingProtocol<String>>() {
            public MessagingProtocol<String> get() {
                return new BGRSprotocol();}};
        Supplier<MessageEncoderDecoder<String>> encoderDecoderFactory = new Supplier<MessageEncoderDecoder<String>>() {
            public MessageEncoderDecoder<String> get() {
                return new BGRSencoderDecoder();}};
        TPC tpc= new TPC(port, protocolFactory, encoderDecoderFactory);
        tpc.serve();
    }
}