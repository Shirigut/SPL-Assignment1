package bgu.spl.net.srv;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class User {
    private final String username;
    private String password;
    private boolean logged;
    private boolean isAdmin;
    private List<Integer> myCourses;

    public User(String username, String password,boolean isAdmin) {
        this.username = username;
        this.password = password;
        logged = false;
        this.isAdmin = isAdmin;
        myCourses = new ArrayList<Integer>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean getLogged(){
        return logged;
    }
    public void setLogged(boolean set){
        logged = set;
    }

    public boolean getIsAdmin(){
        return isAdmin;
    }


     public List<Integer> getMyCourses() {
        // create a list of the student courses by their original order in the courses file
        LinkedHashMap<Integer, Course> allCourses = Database.getInstance().getCourses();
        List<Integer> studentCourses = new ArrayList<>();

        // check for each course in the database if the student is registered to it
        allCourses.forEach((k, v)-> {
        int courseNum = v.getNum();
        if (this.myCourses.contains(courseNum))
            studentCourses.add(courseNum);
        });
        myCourses = studentCourses;
        return myCourses;
    }
}
