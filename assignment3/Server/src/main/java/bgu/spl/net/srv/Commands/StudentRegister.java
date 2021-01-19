package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.concurrent.ConcurrentHashMap;

public class StudentRegister extends Command {

    public Command response(String username, String password,String currUser) {
        if (currUser != null)
            return (new ERROR(2));
        if (Database.getInstance().getUsers().get(username) != null) {
            return (new ERROR(2));
        }
        if (!Database.getInstance().addUser(username, password, false))
            return (new ERROR(2));
        return (new ACK(2,""));
    }
}
