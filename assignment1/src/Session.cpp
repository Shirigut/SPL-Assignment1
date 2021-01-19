#include "../include/Session.h"

using json = nlohmann::json;
using namespace std;

// Constructor
Session::Session(const std::string &path) :
        g(vector<vector<int>>()), treeType(), agents(), infectingQueue(), cycle(0),
        infectedOutput() {
    initFromJson(path);
}

// import parameters from input file
void Session::initFromJson(const std::string &path) {
    ifstream file(path);
    json j;
    file >> j;

    // initialize graph
    g = Graph(j["graph"]);

    //initialize required tree
    string treeT(j["tree"]);
    if (treeT == "M")
        treeType = MaxRank;
    else if (treeT == "C")
        treeType = Cycle;
    else treeType = Root;

    // initialize agents list
    for (auto &elem: j["agents"]) {
        if (elem[0] == "V") {
            int x = elem[1];
            Virus v = Virus(x);
            g.infectNode(x);
            addAgent(v);
            infectedOutput.push_back(v.getNode());

        } else {
            ContactTracer c = ContactTracer();
            addAgent(c);
        }
    }

}

// Copy constructor
Session::Session(const Session &other) :
// initialize data members
        g(other.g), treeType(other.treeType), agents(), infectingQueue(other.infectingQueue),
        cycle(other.cycle), infectedOutput(other.infectedOutput) {

    int otherSize = other.agents.size();
    for (int i = 0; i < otherSize; i++) {
        agents.push_back(other.agents[i]->clone());
    }
}

// Destructor
Session::~Session() {
    clear();
}

//operator =
const Session& Session:: operator=(const Session &other){
    if (this != &other) {
        clear();
        g = other.g;
        treeType = other.treeType;
        infectingQueue = other.infectingQueue;
        cycle = other.cycle;
        for (size_t i = 0; i < other.agents.size(); i++) {
            agents.push_back(other.agents[i]->clone());
        }
    }
    return *this;
}


// Move copy constructor
Session::Session(Session &&other) :
// initialize data members
        g(other.g), treeType(other.treeType), agents(), infectingQueue(other.infectingQueue),
        cycle(other.cycle), infectedOutput(other.infectedOutput) {

    for (size_t i = 0; i < other.agents.size(); i++) {
        agents.push_back(other.agents[i]);
        other.agents[i] = nullptr;
    }
    other.clear();
}

// Move assignment operator
const Session &Session::operator=(Session &&other) {
    if (this != &other) {
        clear();
        g = other.g;
        treeType = other.treeType;
        infectingQueue = other.infectingQueue;
        cycle = other.cycle;

        for (size_t i = 0; i < other.agents.size(); i++) {
            agents.push_back(other.agents[i]);
            other.agents[i] = nullptr;
        }
        other.clear();
    }
    return *this;
}

void Session::simulate() {
    size_t numOfAgents;
    do {
        numOfAgents = agents.size();
        for (size_t i = 0; i < numOfAgents; i++) {
            agents[i]->act(*this);
        }
        cycle++;
    } while (numOfAgents != agents.size()); //while in the current cycle no agent was added
    nlohmann::json j;
    j["graph"] = g.getEdges();
    j["infected"] = infectedOutput;
    ofstream o("output.json");
    o << j;
}


void Session::addAgent(const Agent &agent) {
    agents.push_back(agent.clone());
}

void Session::setGraph(const Graph &graph) {
    g = graph;
}

void Session::enqueueInfected(int node) {
    infectingQueue.push_back(node);
}

int Session::dequeueInfected() {
    if (infectingQueue.empty())
        return -1;
    int x = infectingQueue.front();
    infectingQueue.erase(infectingQueue.begin());
    return x;
}

void Session::clear() {
    for (Agent *agent: agents) {
        if (agent)
            delete agent;
    }
    agents.clear();
}

TreeType Session::getTreeType() const { return treeType; };

Graph Session::getGraph() const { return g; };

int Session::getCycle() const { return cycle; };

void Session::enqueueOutput(int node) {
    infectedOutput.push_back(node);
};
