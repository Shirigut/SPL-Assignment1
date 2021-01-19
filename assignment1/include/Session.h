#ifndef SESSION_H_
#define SESSION_H_


#include "json.hpp"
#include <vector>
#include <string>
#include "Graph.h"
#include "Agent.h"
#include "Tree.h"
#include <iostream>
#include <fstream>

class Agent;


enum TreeType{
  Cycle,
  MaxRank,
  Root
};

class Session {
public:
    Session(const std::string &path);

    Session(const Session &other);

    virtual ~Session();

    const Session &operator=(const Session &other);

    Session(Session &&other);

    const Session &operator=(Session &&other);

    void simulate();

    void addAgent(const Agent &agent);

    void setGraph(const Graph &graph);

    void enqueueInfected(int);

    int dequeueInfected();

    void initFromJson(const std::string &path);

    void clear();

    TreeType getTreeType() const;

    Graph getGraph() const;

    int getCycle() const;

    void enqueueOutput(int);

private:
    Graph g;
    TreeType treeType;
    std::vector<Agent*> agents;
    std::vector<int> infectingQueue;
    int cycle;
    std:: vector<int> infectedOutput;
}
;
#endif
