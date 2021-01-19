LDFLAGS:=-lboost_system
TFLAG:=-pthread
# All Targets
all: bin/BGRSclient

# Tool invocations
# Executable "BGRSclient" depends on the files BGRSclient.o connectionHandler.o Task.o
bin/BGRSclient: bin/BGRSclient.o bin/connectionHandler.o bin/Task.o
	@echo 'Building target: BGRSclient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGRSclient bin/BGRSclient.o bin/connectionHandler.o bin/Task.o $(LDFLAGS) $(TFLAG)
	@echo 'Finished building target: BGRSclient'
	@echo ' '

#BGRSclient.o Depends on the source and header files src/BGRSclient.cpp include/connectionHandler.h
bin/BGRSclient.o: src/BGRSclient.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/BGRSclient.o src/BGRSclient.cpp $(TFLAG)

#connectionHandler.o Depends on the source and header files src/connectionHandler.cpp include/connectionHandler.h
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/connectionHandler.o src/connectionHandler.cpp $(LDFLAGS)

#Task.o Depends on the source and header files src/Task.cpp include/Task.h
bin/Task.o: src/Task.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Task.o src/Task.cpp $(LDFLAGS) $(TFLAG)

#Clean the build directory
clean:
	rm -f bin/*