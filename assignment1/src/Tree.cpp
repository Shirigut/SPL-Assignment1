#include "../include/Tree.h"
#include <queue>

// Constructor
Tree:: Tree(int rootLabel): node(rootLabel), children({}){};

// Copy Constructor
Tree:: Tree(const Tree &other): node(other.node), children({}) {
    copyChildren(other);
}

// Destructor
Tree:: ~Tree() {
    clearChildren();
}

// Assignment operator
const Tree& Tree::operator=(const Tree &other) {
    if (this != &other) {
        clearChildren();
        node = other.node;
        copyChildren(other);
    }
    return *this;
}

// Move operator
Tree::Tree(Tree &&other): node(other.node), children({}){
    moveChildren((Tree&&)other);
}

// Move assignment operator
const Tree & Tree::operator=(Tree &&other) {
    if (this != &other) {
        children.clear();
        node = other.node;
        moveChildren((Tree&&)other);
        other.children.clear();
    }
    return *this;
}

void Tree::clearChildren() {
    for (Tree* child: children) {
        if (child)
            delete child;
    }
    children.clear();
}

void Tree::copyChildren(const Tree &other) {
    for (size_t i = 0; i < other.children.size(); i++) {
        addChild(*(other.children[i]));
    }
}

void Tree::moveChildren(Tree &&other) {
    for (size_t i = 0; i < other.children.size(); i++) {
        addChild(*(other.children[i]));
        other.children[i] = nullptr;
    }
}

int Tree::getNode() const {return node;};

vector<Tree*> Tree::getChildren() const {return children;};

void Tree::addChild(const Tree &child) {
    children.push_back(child.clone());

}

Tree * Tree::createTree(const Session& session, int rootLabel) {
    if (session.getTreeType() == Root)
        return (new RootTree(rootLabel));
    else if(session.getTreeType() == MaxRank)
        return (new MaxRankTree(rootLabel));
    else
        return (new CycleTree(rootLabel, session.getCycle()));
}

// -----------------------------CycleTree-----------------------------

CycleTree::CycleTree(int rootLabel, int currCycle): Tree(rootLabel), currCycle(currCycle){}

Tree* CycleTree::clone() const{
    return (new CycleTree(*this));
}

int CycleTree::traceTree() {
    int c = currCycle;
    Tree *currNode = this;
    while (c > 0 && !(currNode->getChildren().empty())) { // didn't search c times & currNode is not a leaf
        currNode = currNode->getChildren()[0];
        c = c - 1;
    }
    return currNode->getNode();
}

// ---------------------------MaxRankTree-----------------------------

MaxRankTree::MaxRankTree(int rootLabel): Tree(rootLabel) {}

int MaxRankTree::traceTree() {
    // initialize parameters
    Tree *currNode = this;
    int maxRank = getChildren().size();
    int maxNode = getNode();
    queue<Tree *> nodes;

    // use queue to scan from left child to right child and by depth
    nodes.push(currNode);
    while (!nodes.empty()) {
        currNode = nodes.front();
        nodes.pop();
        // push in ascending order
        for (Tree *child: currNode->getChildren()) {
            nodes.push(child);
        }
        // compare number of children
        int currRank = currNode->getChildren().size();
        if (maxRank < currRank) {
            maxRank = currRank;
            maxNode = currNode->getNode();
        }
    }
    return maxNode;
}

Tree* MaxRankTree::clone() const{
    return(new MaxRankTree(*this));
}

//-------------------------RootTree-----------------------------------

RootTree::RootTree(int rootLabel): Tree(rootLabel) {};

int RootTree::traceTree() {return getNode();};

Tree* RootTree::clone() const {
    return (new RootTree(*this));
}

