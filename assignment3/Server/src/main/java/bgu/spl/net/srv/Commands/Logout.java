package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

public class Logout extends Command {

    public Command response(String username) {
        if (username==null)
            return (new ERROR(4));
        User user = Database.getInstance().getUsers().get(username);
        if (user==null)
            return (new ERROR(4));
        if (!user.getLogged())
            return (new ERROR(4));
        user.setLogged(false);
        return (new ACK(4, ""));
    }
}
