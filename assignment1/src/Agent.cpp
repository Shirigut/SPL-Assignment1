#include "../include/Agent.h"

Agent::Agent() {};

Agent::~Agent() {};

//---------------------ContactTracer--------------------------

ContactTracer::ContactTracer() {};

void ContactTracer::act(Session &session) {
    // initialize parameters
    Graph g1 = session.getGraph();
    int nextInfected = session.dequeueInfected();
    if (nextInfected == -1)
        return;
    Tree *treeToTrace(g1.BFS(nextInfected, session));
    int nodeToDisconnect = treeToTrace->traceTree();
    delete treeToTrace;

    // disconnect the chosen node and his edges from the copy graph
    for (size_t i = 0; i < g1.getEdges().size(); i++) {
        g1.removeEdge(i, nodeToDisconnect);
    }
    // set copy graph as the new graph
    session.setGraph(g1);
}

Agent *ContactTracer::clone() const {
    return (new ContactTracer(*this));
}

//-------------------------Virus---------------------------

Virus::Virus(int nodeInd) : nodeInd(nodeInd) {};

void Virus::act(Session &session) {
    Graph g1 = session.getGraph();
    if (!g1.isSick(nodeInd)) { // the node appears in the agent list so we know it is already a carrier
        g1.infectNode(nodeInd);
        session.enqueueInfected(nodeInd);
    }

// search for a neighbor to infect
    bool stop = false;
    size_t n = g1.getEdges().size(); // number of vertices
    for (size_t i = 0; !stop && i < n; i++) {
        vector<vector<int>> ed = g1.getEdges();
        if (ed[nodeInd][i] == 1) {
            if (!g1.isInfected(i)) {
                stop = true;
                Virus newVirus = Virus(i);
                session.addAgent(newVirus);
                session.enqueueOutput(newVirus.getNode());
                g1.infectNode(i);
            }
        }
    }

    session.setGraph(g1);
}

Agent *Virus::clone() const {
    return (new Virus(*this));
};

int Virus::getNode() const {
    return nodeInd;
}

