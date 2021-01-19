
#include <thread>
#include "../include/connectionHandler.h"
#include "../include/Task.h"

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cout << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }

    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cout << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }
    std::mutex mutex;
    std::condition_variable cv;
    Task task1(1, connectionHandler, mutex, cv);
    std::thread sender(&Task::send, &task1);

    while (1) { //reading the server's answer
        char opcode[2];
        char messageOpcode[2];
        if (!connectionHandler.getBytes(opcode, 2)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        short opcodeNum = connectionHandler.bytesToShort(opcode);

        if (!connectionHandler.getBytes(messageOpcode, 2)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        short messageNum = connectionHandler.bytesToShort(messageOpcode);

        if (opcodeNum == 12)
            connectionHandler.decodeACK(opcodeNum, messageNum);
        if (opcodeNum == 13)
            connectionHandler.decodeERROR(opcodeNum, messageNum);
        if (messageNum == 4) {
            if (opcodeNum == 12) { //LOGOUT WITH ACK
                task1.setTerminate(true);
                { std::lock_guard<std::mutex> lk(mutex); }
                cv.notify_all();
                break;
            } else {//LOGOUT WITH ERROR
                { std::lock_guard<std::mutex> lk(mutex); }
                cv.notify_all();
            }
        }
    }
    sender.join();
    return 0;
}
