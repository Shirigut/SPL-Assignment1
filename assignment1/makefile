CC = g++
CFLAGS= -g -Wall -Weffc++ -std=c++11
all: cTrace

cTrace:   bin/main.o bin/Session.o bin/Agent.o bin/Graph.o bin/Tree.o
	@echo 'Building target: cTrace'
	$(CC) -o bin/cTrace bin/main.o bin/Session.o bin/Agent.o bin/Graph.o bin/Tree.o
	@echo 'Finished building target: cTrace'

bin/main.o: src/main.cpp
	$(CC)  $(CFLAGS) -c -Iinclude -o bin/main.o src/main.cpp

bin/Session.o: src/Session.cpp
	$(CC)  $(CFLAGS) -c -Iinclude -o bin/Session.o src/Session.cpp

bin/Agent.o: src/Agent.cpp
	$(CC)  $(CFLAGS) -c -Iinclude -o bin/Agent.o src/Agent.cpp

bin/Graph.o: src/Graph.cpp
	$(CC)  $(CFLAGS) -c -Iinclude -o bin/Graph.o src/Graph.cpp

bin/Tree.o: src/Tree.cpp
	$(CC)  $(CFLAGS) -c -Iinclude -o bin/Tree.o src/Tree.cpp

#clean the build directory
clean:
	rm -f bin/*