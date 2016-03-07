import socket
import utils
import ssl
import command_executor

__author__ = 'mantvydas'
PORT = 8506
RESTARTING_SERVER = "Client disconnected, restarting server.."
KEY = "server.key"
CERTIFICATE = "server.crt"
STOP_SERVER = 'STOP_SERVER'


class Server:
    host = None
    ip = None
    serverSocket = None
    clientSocket = None
    isThisLinux = None
    command_executor = ''

    def __init__(self, ipAddress, isThisLinux):
        """
        Opens a socket for the specified IP address that listens for commands from the client (volume change, seek, play/pause, etc);
        """
        self.ip = str(ipAddress)
        self.isThisLinux = isThisLinux
        self.commandExecutor = command_executor.CommandExecutor(isThisLinux)
        self.startServer()

        while 1:
            secureClientSocket = self.acceptConnections()
            self.receiveMessages(secureClientSocket)

    def startServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.serverSocket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.serverSocket.bind((self.ip, PORT))
        self.serverSocket.listen(5)
        print("Server started on " + self.ip + ":" + str(PORT) + " and listening")

    def restartServer(self, secureClientSocket):
        print(RESTARTING_SERVER)
        secureClientSocket.close()
        if secureClientSocket._closed:
            self.__init__(self.ip, self.isThisLinux)

    def acceptConnections(self):
        while True:
            clientSocket, clientAddress = self.serverSocket.accept()
            print('Connection from', clientAddress)
            return ssl.wrap_socket(
                    clientSocket,
                    keyfile=KEY,
                    server_side=True,
                    certfile=CERTIFICATE)

    def receiveMessages(self, secureClientSocket):
        while True:
            message = secureClientSocket.recv(2048).decode()

            if message == STOP_SERVER:
                self.restartServer(secureClientSocket)
                break
            elif message != '':
                command, value = self.extractCommand(message)
                print(command, value)
                self.commandExecutor.executeCommand(command, value)

    def extractCommand(self, message):
        message = message.split(";", 1)
        command, value = message[0].split(":", 1)
        value = utils.stringToInt(value)
        return command, value
