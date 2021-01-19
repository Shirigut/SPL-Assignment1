#ifndef GRAPH_H_
#define GRAPH_H_

#include <vector>

class Tree;
class Session;
using namespace std;

enum Status {
    Healthy,
    Carrier,
    Sick
};

class Graph{
public:
    Graph(std::vector<std::vector<int>> matrix);
    void infectNode(int nodeInd);
    bool isInfected(int nodeInd);
    bool isSick(int nodeInd);
    std::vector<std::vector<int>> getEdges();
    Tree* BFS(int node, Session& session);
    void removeEdge(int neighbor, int node);

private:
    std::vector<std::vector<int>> edges;
    std::vector<Status> infected;
};

#endif
