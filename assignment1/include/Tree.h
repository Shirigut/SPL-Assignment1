#ifndef TREE_H_
#define TREE_H_

#include <vector>
#include <queue>
#include "Session.h"
#include <iostream>

class Session;
using namespace std;

class Tree{
public:
    Tree(int rootLabel);

    Tree(const Tree &other);

    virtual ~Tree();

    const Tree& operator=(const Tree &other);

    Tree(Tree&& other);

    const Tree& operator=(Tree&& other);

    void clearChildren();

    virtual Tree *clone() const=0;

    void copyChildren(const Tree &other);

    void moveChildren(Tree&& other);

    int getNode() const;

    std::vector<Tree*> getChildren() const;

    void addChild(const Tree& child);

    static Tree* createTree(const Session& session, int rootLabel);

    virtual int traceTree()=0;

private:
    int node;
    std::vector<Tree*> children;
};

class CycleTree: public Tree{
public:
    CycleTree(int rootLabel, int currCycle);
    virtual int traceTree();
    virtual Tree* clone() const;
private:
    int currCycle;
};

class MaxRankTree: public Tree{
public:
    MaxRankTree(int rootLabel);
    virtual int traceTree();
    virtual Tree* clone() const;
};

class RootTree: public Tree{
public:
    RootTree(int rootLabel);
    virtual int traceTree();
    virtual Tree* clone() const;
};
#endif
