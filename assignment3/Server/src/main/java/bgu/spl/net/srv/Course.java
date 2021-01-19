package bgu.spl.net.srv;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Course {
    private int num;
    private String name;
    private List<Integer> kdamCourses;
    private int maxStudents;
    private int numOfRegistered;
    private CopyOnWriteArrayList<String> registeredStudents;

    public Course(String course) {
        numOfRegistered = 0;
        registeredStudents = new CopyOnWriteArrayList<String>();
        this.num = Integer.parseInt(course.substring(0, course.indexOf('|')));
        this.name = course.substring(course.indexOf('|') + 1, course.indexOf('[') - 1);
        String kdamArray = course.substring(course.indexOf('['), course.indexOf(']') + 1);
        kdamCourses = new ArrayList<Integer>();

        // kdamCourses decode
        String courseNum = "";
        for (int i = 1; i < kdamArray.length(); i++) {
            Character curr = kdamArray.charAt(i);
            if (!curr.equals(',') & !curr.equals(']')) {
                courseNum = courseNum + curr;
            } else {
                if (courseNum.length() > 0) {
                    kdamCourses.add(Integer.valueOf(courseNum));
                    courseNum = "";
                }
            }
        }

        this.maxStudents = Integer.parseInt(course.substring(course.indexOf(']') + 2));
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public List<Integer> getKdamCourses() {
        return kdamCourses;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public synchronized int getNumOfRegistered(){return numOfRegistered;}

    public synchronized void setNumOfRegistered(int change){numOfRegistered = numOfRegistered + change;}

    public List<String> getRegisteredStudents(){return registeredStudents;}

    public synchronized int getNumOfAvailableSeats(){return (maxStudents - numOfRegistered);}

    public boolean addStudent(String user) {
        if (registeredStudents.contains(user))
            return false;
        setNumOfRegistered(1);
        registeredStudents.add(user);
        registeredStudents.sort(Comparator.naturalOrder());
        Database.getInstance().getUsers().get(user).getMyCourses().add(num);
        return true;
    }
}
