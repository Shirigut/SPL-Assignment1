package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

public class Login extends Command {

    public Command response(String username, String password) {
        User user = Database.getInstance().getUsers().get(username);
        if (user == null)
            return (new ERROR(3));
        if (!user.getPassword().equals(password)){
            return (new ERROR(3));
        }
        if (user.getLogged()){
            return (new ERROR(3));
        }
        user.setLogged(true);
        return (new ACK(3, ""));
    }
}
