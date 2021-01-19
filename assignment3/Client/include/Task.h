//
// Created by spl211 on 06/01/2021.
//
#ifndef BOOST_ECHO_CLIENT_TASK_H
#define BOOST_ECHO_CLIENT_TASK_H

#include <mutex>
#include <future>
#include "connectionHandler.h"

class Task {
private:
    int id;
    ConnectionHandler &handler;
    std::mutex &mutex;
    bool terminate;
    std::condition_variable &cv;

public:
    Task(int _id, ConnectionHandler &_handler, std::mutex &_mutex, std::condition_variable &_cv);
    void send();
    void setTerminate(bool set);
};

#endif //BOOST_ECHO_CLIENT_TASK_H
