package bgu.spl.net.srv.Commands;
import bgu.spl.net.srv.Database;
import java.util.List;

public class KdamCheck extends Command {

    public Command response (String username, int courseNum){
        if (Database.getInstance().getUsers().get(username).getIsAdmin())
            return new ERROR(6);
        if (Database.getInstance().getCourses().get(courseNum)==null)
            return new ERROR(6);
        List<Integer> kdamCourses = Database.getInstance().getCourses().get(courseNum).getKdamCourses();
        return (new ACK(6, kdamCourses.toString()));
    }
}
