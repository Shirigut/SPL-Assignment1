#include "../include/Graph.h"
#include "../include/Session.h"
#include <queue>

using namespace std;

// constructor
Graph::Graph(vector<vector<int>> matrix) : edges(matrix), infected(edges.size(), Healthy) {};

// copy constructor
void Graph::infectNode(int nodeInd) {
    if (infected[nodeInd] == Healthy) {
        infected[nodeInd] = Carrier;
    } else {
        infected[nodeInd] = Sick;
    }
}

// check if carries a virus (carrier or sick)
bool Graph::isInfected(int nodeInd) {
    return (infected[nodeInd] != Healthy);
}

// check if is sick
bool Graph::isSick(int nodeInd) {
    if (infected[nodeInd] == Sick)
        return true;
    return false;
}

// get graph edges matrix
vector<vector<int>> Graph::getEdges() { return edges; };

Tree *Graph::BFS(int node, Session &session) {
    size_t n = edges.size(); // number of vertices
    Tree *bfsTree = Tree::createTree(session, node); // create the root of the specific treeType required
    vector<bool> visited(n, false);

    // initialize
    queue<Tree *> q; // trees queue
    visited[node] = true;
    q.push(bfsTree);
    size_t size = edges.size();

    // visit each v in the graph once and add him to the BFS tree
    while (!q.empty()) {
        Tree *currTree = q.front();
        int currNode = currTree->getNode();
        q.pop();
        for (size_t x = 0; x < size; x++) {
            if (edges[x][currNode] == 1 && !visited[x]) {
                visited[x] = true;
                Tree *xTree = Tree::createTree(session, x); // create the child tree who's root is x
                currTree->addChild(*xTree);
                delete xTree;
            }
        }
        for (Tree *child: currTree->getChildren()) {
            q.push(child);
        }
    }
    return bfsTree;
};

void Graph::removeEdge(int neighbor, int node) {
    edges[neighbor][node] = 0;
    edges[node][neighbor] = 0;
}
