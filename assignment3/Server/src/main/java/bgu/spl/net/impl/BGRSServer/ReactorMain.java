package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.BGRSencoderDecoder;
import bgu.spl.net.srv.BGRSprotocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Reactor;
import java.util.function.Supplier;

public class ReactorMain {
    public static void main(String[] args) {
       Database database = Database.getInstance();
       database.initialize("Courses.txt");
        int port =Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);
        Supplier<MessagingProtocol<String>> protocolFactory = new Supplier<MessagingProtocol<String>>() {
            public MessagingProtocol<String> get() {
                return new BGRSprotocol();
            }
        };
        Supplier<MessageEncoderDecoder<String>> encoderDecoderFactory = new Supplier<MessageEncoderDecoder<String>>() {
            public MessageEncoderDecoder<String> get() {
                return new BGRSencoderDecoder();
            }
        };
        Reactor reactor = new Reactor(numOfThreads, port, protocolFactory, encoderDecoderFactory);
        reactor.serve();
    }
}
