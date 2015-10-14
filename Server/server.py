import socket
import subprocess

__author__ = 'mantvydas'
PORT = 8506
MAX_VOLUME = 65535
VOLUME_CHANGED = 'VOLUME_CHANGED'
STOP_SERVER = 'STOP_SERVER'


class Server:
    host = None
    ip = None
    serverSocket = None
    clientSocket = None

    def __init__(self, ipAddress):
        """
        Opens a socket for the specified IP address that listens for volume change messages from the client;
        """
        self.ip = str(ipAddress)
        self.startServer()

    def startServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.serverSocket.bind((self.ip, PORT))
        self.serverSocket.listen(9999)

        print("Server started on " + self.ip + ":" + str(PORT) + " and listening")
        clientSocket, clientAddress = self.serverSocket.accept()

        print('Got connection from', clientAddress)
        self.listenForMessages(clientSocket)

    def listenForMessages(self, clientSocket):
        while True:
            message = self.readMessage(clientSocket)

            if message == STOP_SERVER:
                print("Message: " + STOP_SERVER)
                self.restartServer(clientSocket)
                break
            else:
                print("Message: volume - " + message)
                self.changeVolume(message)
                clientSocket.send(bytes(VOLUME_CHANGED, "utf8"))

    def readMessage(self, clientSocket):
        return str(clientSocket.recv(1024), "utf8")

    def changeVolume(self, message):
        newVolume = (int(message) / 100 * MAX_VOLUME)
        if newVolume / MAX_VOLUME < 1:
            subprocess.call("nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume))

    def restartServer(self, clientSocket):
        print("Client disconnected, restarting server..")
        clientSocket.close()
        self.startServer()