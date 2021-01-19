package bgu.spl.net.srv.Commands;

import bgu.spl.net.srv.Course;
import bgu.spl.net.srv.Database;
import java.util.List;

public class CourseStat extends Command {

    public Command response(int courseNum, String username){
        //check if this user is an admin
        if (!(Database.getInstance().getUsers().get(username).getIsAdmin()))
            return new ERROR(7);

        Course thisCourse= Database.getInstance().getCourses().get(courseNum);
        String courseName= thisCourse.getName();
        String courseNumber= "(" + String.valueOf(courseNum) + ")";
        String seatAvailable= thisCourse.getNumOfAvailableSeats() +"/"+thisCourse.getMaxStudents();
        List<String> registered = thisCourse.getRegisteredStudents(); // already sorted
        String registeredUsers= registered.toString();
        String students= "";
        for (int i = 0; i < registeredUsers.length(); i++) {
            if (registeredUsers.charAt(i) != ' ')
                students += registeredUsers.charAt(i);
        }
        String toSend= "Course: " + courseNumber+" "+courseName+ "\n" +
                "Seats Available: " + seatAvailable + "\n" +
                "Students Registered: " + students;
        return new ACK(7, toSend);
    }
}
