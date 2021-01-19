package bgu.spl.net.srv.Commands;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.User;

public class StudentStat extends Command {

    public Command response(String student, String user){
        User thisUser = Database.getInstance().getUsers().get(user);
        if (!thisUser.getIsAdmin()) //if the client is not an admin
            return (new ERROR(8));
        User studentUser= Database.getInstance().getUsers().get(student);
        if (studentUser == null)  //if the student is not registered
            return (new ERROR(8));
        if (studentUser.getIsAdmin()) //if the user is an admin and not a student
            return (new ERROR(8));

        String name = "Student: " + student;
        String myCourses= studentUser.getMyCourses().toString();
        String coursesList= "";
        for (int i = 0; i < myCourses.length(); i++) {
            if (myCourses.charAt(i) != ' ')
                coursesList += myCourses.charAt(i);
        }
        String courses = "Courses: " + coursesList;
        String studentStat = name + "\n" + courses;
        return new ACK(8, studentStat);
    }
}
