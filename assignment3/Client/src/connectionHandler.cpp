#include <connectionHandler.h>

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port) : host_(host), port_(port), io_service_(),
                                                                socket_(io_service_) {}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
    std::cout << "Starting connect to "
             << host_ << ":" << port_ << std::endl;
    try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception &e) {
        std::cout << "Connection failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

//use to get the bytes from the server
bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp) {
            tmp += socket_.read_some(boost::asio::buffer(bytes + tmp, bytesToRead - tmp), error);
        }

        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cout << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

//used to send the bytes to the server
bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if (error)
            throw boost::system::system_error(error);
    } catch (std::exception &e) {
        std::cout << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

//unused
bool ConnectionHandler::getLine(std::string &line) {
//what we get from the server- error or ack
    return true;
}

//used to encode the string that we got from the keyboard
bool ConnectionHandler::sendLine(std::string &line) {
    return encoder(line);
}

//unused
bool ConnectionHandler::getFrameAscii(std::string &frame, char delimiter) {
    char ch;
    // Stop when we encounter the null character.
    // Notice that the null character is not appended to the frame string.
    try {
        do {
            if (!getBytes(&ch, 1)) {
                return false;
            }
            if (ch != '\0')
                frame.append(1, ch);
        } while (delimiter != ch);
    } catch (std::exception &e) {
        std::cerr << "recv failed2 (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}

//encode the string we got from the user
bool ConnectionHandler::encoder(const std::string &frame) {
    short opcodeNum = 0;
    string command = frame.substr(0, frame.find_first_of(' '));
    string info = frame.substr(frame.find_first_of(' ') + 1);
    if (command == "ADMINREG")
        opcodeNum = 1;
    if (command == "STUDENTREG")
        opcodeNum = 2;
    if (command == "LOGIN")
        opcodeNum = 3;
    if (command == "LOGOUT")
        opcodeNum = 4;
    if (command == "COURSEREG")
        opcodeNum = 5;
    if (command == "KDAMCHECK")
        opcodeNum = 6;
    if (command == "COURSESTAT")
        opcodeNum = 7;
    if (command == "STUDENTSTAT")
        opcodeNum = 8;
    if (command == "ISREGISTERED")
        opcodeNum = 9;
    if (command == "UNREGISTER")
        opcodeNum = 10;
    if (command == "MYCOURSES")
        opcodeNum = 11;

    if (opcodeNum == 0)
        return false;

    //insert the opcode to the bytes array
    char bytesArr[1024];
    bytesArr[0] = ((opcodeNum >> 8) & 0xFF);
    bytesArr[1] = (opcodeNum & 0xFF);
    int limit = 2;

    if (opcodeNum <= 3) {
        string userName = info.substr(0, info.find_first_of(' '));

        for (size_t i = 0; i < userName.length(); i++) {
            bytesArr[limit] = userName[i];
            limit++;
        }
        bytesArr[limit] = '\0';
        limit++;
        string password = info.substr(info.find_first_of(' ') + 1);
        for (size_t i = 0; i < password.length(); i++) {
            bytesArr[limit] = password[i];
            limit++;
        }
        bytesArr[limit] = '\0';
        limit++;
    }

    if ((opcodeNum >= 5) & (opcodeNum <= 10) & (opcodeNum != 8)) {
        short courseNumber = stol(info.substr(0));
        bytesArr[limit] = ((courseNumber >> 8) & 0xFF);
        bytesArr[limit + 1] = (courseNumber & 0xFF);
        limit = 4;
    }

    if (opcodeNum == 8) {
        for (size_t i = 0; i < info.length(); i++) {
            bytesArr[limit] = info[i];
            limit++;
        }
        bytesArr[limit] = '\0';
        limit++;
    }
    return sendBytes(bytesArr, limit);

}

bool ConnectionHandler::decoder(short opcode, string &attachment, string &messageOpcode) {
    if (opcode == 12) {
        if (attachment!= "")
        std::cout << "ACK " + messageOpcode  + "\n"
                      + attachment << std::endl;
        else
            std::cout << "ACK " + messageOpcode << std::endl;
        return true;
    }
    if (opcode == 13) {
        std::cout << "ERROR " + messageOpcode  << std::endl;
        return true;
    }
    return false;
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try {
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

void ConnectionHandler::shortToBytes(short num, char *bytesArr) {
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short ConnectionHandler::bytesToShort(char *bytesArr) {
    short result = (short) ((bytesArr[0] & 0xff) << 8);
    result += (short) (bytesArr[1] & 0xff);
    return result;
}

void ConnectionHandler::decodeACK(short opcode, short messageOpcode) {
    char ans[1024];
    char c;
    int counter = 0;
    do {
        if (!getBytes(&c, 1))
            std::cout << "Disconnected. Exiting...\n" << std::endl;
        ans[counter] = c;
        counter++;
    } while (c != '\0');
    std::string attachment(ans, counter - 1);
    string msg = std::to_string(messageOpcode);
    decoder(12, attachment, msg);
}

    void ConnectionHandler::decodeERROR(short opcode, short messageOpcode) {
        string msg = std::to_string(messageOpcode);
        string empty = "";
        decoder(13, empty, msg);
    }
