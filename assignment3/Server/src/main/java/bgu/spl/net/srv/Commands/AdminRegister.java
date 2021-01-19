package bgu.spl.net.srv.Commands;
import bgu.spl.net.srv.Database;


public class AdminRegister extends Command {

    public Command response(String username, String password, String currUser) {
        if (currUser != null)
            return (new ERROR(1));
        if (Database.getInstance().getUsers().get(username) != null) {
            return (new ERROR(1));
        }
        if (!Database.getInstance().addUser(username, password, true))
            return (new ERROR(1));

        return (new ACK(1, ""));
    }
}
