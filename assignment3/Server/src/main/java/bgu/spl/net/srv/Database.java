package bgu.spl.net.srv;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {
    private ConcurrentHashMap<String, User> users;
    private LinkedHashMap<Integer, Course> courses;

    //to prevent user from creating new Database
    private Database() {
        users = new ConcurrentHashMap<>();
        courses = new LinkedHashMap<Integer, Course>();

    }

    public static class DatabaseHolder {
        private static Database instance = new Database();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Database getInstance() {
        return DatabaseHolder.instance;
    }

    public ConcurrentHashMap<String, User> getUsers() {
        return users;
    }

    public LinkedHashMap<Integer, Course> getCourses() {
        return courses;
    }

    public synchronized boolean addUser(String userName, String password, boolean isAdmin) {
        if (users.get(userName)!=null)
            return false;
        else {
            User user = new User(userName, password, isAdmin);
            users.put(userName, user);
            return true;
        }
    }

    /**
     * loades the courses from the file path specified
     * into the Database, returns true if successful.
     */
    public boolean initialize(String coursesFilePath) {
        courses = new LinkedHashMap<Integer, Course>();
        File courseFile = new File(coursesFilePath);
        try (Scanner myScanner = new Scanner(courseFile)) {
            while (myScanner.hasNextLine()) {
                String line = myScanner.nextLine();
                if (!line.equals("")) {
                    Course newCourse = new Course(line);
                    courses.put(newCourse.getNum(), newCourse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
