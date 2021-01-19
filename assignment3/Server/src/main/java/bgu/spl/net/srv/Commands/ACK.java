package bgu.spl.net.srv.Commands;

public class ACK extends Command {
    private String reply;

    public ACK(int messageOpcode, String toSend) {
        reply = String.valueOf(12) +
                String.valueOf(messageOpcode) + " " +
                toSend+ "\0";
    }
   public String getReply(){return reply;}
}
