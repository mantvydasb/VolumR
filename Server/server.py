import socket
import subprocess
import converters
import ssl

import win32api
import win32con
import os

__author__ = 'mantvydas'
PORT = 8506
MAX_VOLUME = 65535
VOLUME_CHANGED = 'VOLUME_CHANGED'
STOP_SERVER = 'STOP_SERVER'
RESTARTING_SERVER = "Client disconnected, restarting server.."
MESSAGE_LENGHT = 10

class Server:
    host = None
    ip = None
    serverSocket = None
    clientSocket = None


    def __init__(self, ipAddress):
        """
        Opens a socket for the specified IP address that listens for commands from the client (volume change, seek, play/pause, etc);
        """
        self.ip = str(ipAddress)
        self.startServer()

    def startServer(self):
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.serverSocket.bind((self.ip, PORT))
        self.serverSocket.listen(5)

        print("Server started on " + self.ip + ":" + str(PORT) + " and listening")
        clientSocket, clientAddress = self.serverSocket.accept()

        print('Got connection from', clientAddress)
        self.listenForMessages(clientSocket)

    def listenForMessages(self, clientSocket):
        while True:
            message = clientSocket.recv(2048).decode()
            sslMessage = ssl.wrap_socket(clientSocket, certfile="volumr.crt", keyfile="volumr.key", server_side=True)
            print("ssl message: " + sslMessage.decode())
            # message = clientSocket.recv(2048).decode()

            if message == STOP_SERVER:
                print("Message: " + STOP_SERVER)
                self.restartServer(clientSocket)
                break

            elif message != '':
                command, value = self.extractCommand(message)
                print(command, value)
                self.executeCommand(command, value)

    def executeCommand(self, command, value):
        if command == "volume":
            self.changeVolume(value)

        elif command == "right":
            self.pressRight()

        elif command == "left":
            self.pressLeft()

        elif command == "space":
            self.pressSpace()

    def extractCommand(self, message):
        message = message.split(";", 1)
        command, value = message[0].split(":", 1)
        value = converters.stringToInt(value)
        return command, value

    def changeVolume(self, message):
        newVolume = (int(message) / 100 * MAX_VOLUME)

        if newVolume / MAX_VOLUME < 1:
            command = "nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume)
            subprocess.call(command)

    def pressRight(self):
        win32api.keybd_event(win32con.VK_RIGHT, 0, 0, 0)

    def pressLeft(self):
        win32api.keybd_event(win32con.VK_LEFT, 0, 0, 0)

    def pressSpace(self):
            win32api.keybd_event(win32con.VK_SPACE, 0, 0, 0)

    def restartServer(self, clientSocket):
        print(RESTARTING_SERVER)
        clientSocket.close()
        self.startServer()
