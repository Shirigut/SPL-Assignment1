#include "Task.h"

Task::Task(int _id, ConnectionHandler &_handler, std::mutex &_mutex, std:: condition_variable &_cv) :
        id(_id), handler(_handler), mutex(_mutex), terminate(false), cv(_cv){};

void Task::send() {

    while (!terminate) {
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        if (!handler.sendLine(line)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        if (line == "LOGOUT") {
            std::unique_lock<std::mutex>lk(mutex);
            cv.wait(lk);
        }
    }
}


void Task::setTerminate(bool set) {
    terminate = set;
}