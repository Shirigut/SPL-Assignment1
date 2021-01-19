package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RegisterToCourse extends Command {

    public Command response(int courseNum, String username) {
        Database database = Database.getInstance();
        User user = database.getUsers().get(username);
        if (user == null)
            return (new ERROR(5));
        if (!user.getLogged())
            return (new ERROR(5));
        if (user.getIsAdmin())
            return (new ERROR(5));
        Course course = database.getCourses().get(courseNum);
        if (course == null)
            return (new ERROR(5));
        List<Integer> studentCourses = user.getMyCourses();
        if (studentCourses.contains(courseNum))
            return (new ERROR(5));
        for (Integer numOfKdamCourse : course.getKdamCourses()) {
            if (!studentCourses.contains(numOfKdamCourse)) {
                return (new ERROR(5));
            }
        }
        if (course.getMaxStudents() <= course.getNumOfRegistered())
            return (new ERROR(5));

        if (!course.addStudent(username))
            return (new ERROR(5));

        return (new ACK(5, ""));
    }
}
