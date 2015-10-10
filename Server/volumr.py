import socket
import subprocess
import sys

PORT = 8506
IP = "192.168.2.6"
FULL_VOLUME = 65535
VOLUME_CHANGED = 'VOLUME_CHANGED'
STOP_SERVER = 'STOP_SERVER'

class Server:
    host = None
    ip = None
    ServerSocket = None
    clientSocket = None

    def __init__(self):
        self.getServerInfo()
        self.startServer()

    def startServer(self):
        self.ServerSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.ServerSocket.bind((IP, PORT))
        self.ServerSocket.listen(9999)
        clientSocket, clientAddress = self.ServerSocket.accept()
        print("Server started and listening")
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
        newVolume = (int(message) / 100 * FULL_VOLUME)
        if newVolume / FULL_VOLUME < 1:
            subprocess.call("nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume))

    def restartServer(self, clientSocket):
        print("Stopping server")
        clientSocket.close()
        self.startServer()

    def getServerInfo(self):
        self.host = socket.gethostname()
        self.ip = socket.gethostbyname(self.host)
        print("*** SERVER INFO ***")
        print("IP:   " + IP)
        print("HOST: " + self.host)
        print("PORT: " + str(PORT))

