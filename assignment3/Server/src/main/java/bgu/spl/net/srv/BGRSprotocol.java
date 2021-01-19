package bgu.spl.net.srv;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.srv.Commands.*;

public class BGRSprotocol implements MessagingProtocol<String> {
    private boolean shouldTerminate;
    private String currentUser;
    private Command response;
    static private Object lock = new Object();

    @Override
    public String process(String msg) {
        int opcode;
        if (msg.length() == 1)
            opcode = Character.getNumericValue(msg.charAt(0));
        else if (msg.charAt(1) == ' ')
            opcode = Character.getNumericValue(msg.charAt(0));
        else
            opcode = Integer.parseInt(msg.substring(0, 2));

        if (opcode <= 3) { // AdminRegister, StudentRegister or Login
            String username = msg.substring(2, msg.lastIndexOf(' '));
            String password = msg.substring(msg.lastIndexOf(' ') + 1);
            if (opcode == 1 ) {
                response = new AdminRegister().response(username, password, currentUser);
            } else if (opcode == 2) {
                response = new StudentRegister().response(username, password, currentUser);
            } else {
                synchronized (lock) {
                    if (currentUser == null && (Database.getInstance().getUsers().get(username) != null)) {
                        response = new Login().response(username, password);
                        currentUser = username;
                    } else
                        response = new ERROR(3);
                }
            }
        }
        if (opcode == 4) {
            response = new Logout().response(currentUser);
            if (response instanceof ACK) {
                currentUser = null;
                shouldTerminate = true;
            }
        }
        if (opcode >= 5 & opcode <= 7) {
            int courseNum = Integer.parseInt(msg.substring(2));
            if (opcode == 5)
                response = new RegisterToCourse().response(courseNum, currentUser);
            if (opcode == 6)
                response = new KdamCheck().response(currentUser, courseNum);
            if (opcode == 7)
                response = new CourseStat().response(courseNum, currentUser); // to know if user is admin
        }
        if (opcode == 8) {
            String username = msg.substring(2);
            response = new StudentStat().response(username, currentUser);
        }
        if (opcode == 9 | opcode == 10) {
            int courseNum;
            if (opcode == 9) {
                courseNum = Integer.parseInt(msg.substring(2));
                response = new IsRegistered().response(currentUser, courseNum);
            }
            if (opcode == 10) {
                courseNum = Integer.parseInt(msg.substring(3));
                response = new Unregister().response(currentUser, courseNum);
            }
        }
        if (opcode == 11)
            response = new MyCourses().response(currentUser);

        if (response instanceof ACK)
            return ((ACK) response).getReply();

        return ((ERROR) response).getReply();
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}