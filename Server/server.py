import socket
import subprocess

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
        self.startServer(ipAddress)

    def startServer(self, ipAddress):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.serverSocket.bind((ipAddress, PORT))
        self.serverSocket.listen(9999)

        print("Server started on " + ipAddress + ":" + str(PORT) + " and listening")
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
        print("Stopping server")
        clientSocket.close()
        self.startServer()