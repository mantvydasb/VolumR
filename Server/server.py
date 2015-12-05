import socket
import subprocess
import converters

import win32api

import win32con

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
        Opens a socket for the specified IP address that listens for volume change messages from the client;
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
            print(message)

            if message == STOP_SERVER:
                print("Message: " + STOP_SERVER)
                self.restartServer(clientSocket)
                break

            elif message != '':
                message = message.split(";", 1)
                (command, value) = message[0].split(":", 1)
                value = converters.stringToInt(value)

                if command == "volume":
                    self.changeVolume(value)

                elif command == "seek":
                    if value == "1":
                        self.seekRight()
                    else:
                        self.seekLeft()

                elif command == "space":
                    self.pressSpace()


    def changeVolume(self, message):
        newVolume = (int(message) / 100 * MAX_VOLUME)
        if newVolume / MAX_VOLUME < 1:
            subprocess.call("nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume))

    def seekRight(self):
        for i in range(0, 5):
            win32api.keybd_event(win32con.VK_RIGHT, 0, 0, 0)
            print(str(i))

    def seekLeft(self):
        for i in range(0, 5):
            win32api.keybd_event(win32con.VK_LEFT, 0, 0, 0)

    def pressSpace(self):
            win32api.keybd_event(win32con.VK_SPACE, 0, 0, 0)

    def restartServer(self, clientSocket):
        print(RESTARTING_SERVER)
        clientSocket.close()
        self.startServer()


# class Server:
#     host = None
#     ip = None
#     serverSocket = None
#     clientSocket = None
#
#     def __init__(self, ipAddress):
#         """
#         Opens a socket for the specified IP address that listens for volume change messages from the client;
#         """
#         self.ip = str(ipAddress)
#         self.startServer()
#
#     def startServer(self):
#         self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
#         self.serverSocket.bind((self.ip, PORT))
#         self.serverSocket.listen(5)
#
#         print("Server started on " + self.ip + ":" + str(PORT) + " and listening")
#         clientSocket, clientAddress = self.serverSocket.accept()
#
#         print('Got connection from', clientAddress)
#         self.listenForMessages(clientSocket)
#
#     def listenForMessages(self, clientSocket):
#         while True:
#             message = self.readMessage(clientSocket)
#
#             if message == STOP_SERVER:
#                 print("Message: " + STOP_SERVER)
#                 self.restartServer(clientSocket)
#                 break
#
#             elif message != '':
#                 print(message)
#                 command, value = message.split(":", 1)
#                 if command == "volume":
#                     self.changeVolume(value)
#                     clientSocket.send(bytes(VOLUME_CHANGED, "utf8"))
#
#     def readMessage(self, clientSocket):
#         return str(clientSocket.recv(24), "utf8")
#
#     def changeVolume(self, message):
#         newVolume = (int(message) / 100 * MAX_VOLUME)
#         if newVolume / MAX_VOLUME < 1:
#             subprocess.call("nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume))
#
#     def restartServer(self, clientSocket):
#         print(RESTARTING_SERVER)
#         clientSocket.close()
#         self.startServer()