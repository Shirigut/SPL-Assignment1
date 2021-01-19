package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.List;

public class Unregister extends Command {
    public Command response(String username, Integer courseNum){
        User user = Database.getInstance().getUsers().get(username);
        if (user.getIsAdmin())
            return new ERROR(10);
        List <Integer> courses= user.getMyCourses();
        if (!courses.contains(courseNum))
            return new ERROR(10);
        courses.remove(courseNum);
        Course course = Database.getInstance().getCourses().get(courseNum);
        course.getRegisteredStudents().remove(username);
        course.setNumOfRegistered(-1);
        return new ACK(10, "");
    }
}
