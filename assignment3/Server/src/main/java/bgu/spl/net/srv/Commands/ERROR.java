package bgu.spl.net.srv.Commands;

public class ERROR extends Command {
    private String error;

    public ERROR(int messageOpcode){
        error = String.valueOf(13)+String.valueOf(messageOpcode) +" ";
    }

    public String getReply(){return error;}
}
