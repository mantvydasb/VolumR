import socket
import subprocess

PORT = 8506
IP = "192.168.2.6"
FULL_VOLUME = 65535


class Server:
    host = None
    ip = None
    ServerSocket = None
    clientSocket = None

    def __init__(self):
        self.getServerInfo()
        self.startServer()

    def startServer(self):
        print("Starting server..")
        self.ServerSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.ServerSocket.bind((IP, PORT))
        self.ServerSocket.listen()
        print("Server started")
        self.clientSocket, clientAddress = self.ServerSocket.accept()

        while True:
            # welcome guest
            print('Got connection from', clientAddress)
            self.clientSocket.send(bytes('Thank you for connecting', "utf8"))

            # receive client messages
            message = str(self.clientSocket.recv(4096), "utf8")
            print(message)

            newVolume = (int(message)/100 * FULL_VOLUME)
            if newVolume/FULL_VOLUME < 1:
                print("new volume: " + str(newVolume))
                subprocess.check_output("nircmd.exe setvolume 0 " + str(newVolume) + " " + str(newVolume))
                self.ServerSocket.close()

    def getServerInfo(self):
        self.host = socket.gethostname()
        self.ip = socket.gethostbyname(self.host)
        print("*** SERVER INFO ***")
        print("IP:   " + IP)
        print("HOST: " + self.host)
        print("PORT: " + str(PORT))

