package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class MyCourses extends Command {

    public Command response(String username) {
        User thisUser = Database.getInstance().getUsers().get(username);
        if (thisUser.getIsAdmin())
            return new ERROR(11);
        List<Integer> studentCourses = thisUser.getMyCourses();
        String courses = studentCourses.toString();
        String toSend = "";
        //remove spaces
        for (int i = 0; i < courses.length(); i++) {
            if (courses.charAt(i) != ' ')
                toSend += courses.charAt(i);
        }
        return new ACK(11, toSend);
    }
}
