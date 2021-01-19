package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

public class IsRegistered extends Command {
    public Command response(String username, int courseNum){
        User user = Database.getInstance().getUsers().get(username);
        if (user.getIsAdmin())
            return new ERROR(9);
        if (Database.getInstance().getCourses().get(courseNum)== null)
            return new ERROR(9);
        if (user.getMyCourses().contains(courseNum))
            return new ACK(9, "REGISTERED");
        return new ACK(9, "NOT REGISTERED");
    }
}
