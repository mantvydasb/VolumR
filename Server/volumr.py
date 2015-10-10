import socket

PORT = 8506
IP = "192.168.2.6"


class Server:
    host = None
    ip = None
    ServerSocket = None

    def __init__(self):
        self.getServerInfo()
        self.startServer()

    def startServer(self):
        print("Starting server..")
        self.ServerSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.ServerSocket.bind((IP, PORT))
        self.ServerSocket.listen()
        print("Server started")

        while True:
            # welcome guest
            clientSocket, clientAddress = self.ServerSocket.accept()
            print('Got connection from', clientAddress)
            clientSocket.send(bytes('Thank you for connecting', "utf8"))

            # receive client messages
            print(str(clientSocket.recv(1024), "utf8"))

            # self.server.close()

    def getServerInfo(self):
        self.host = socket.gethostname()
        self.ip = socket.gethostbyname(self.host)
        print("*** SERVER INFO ***")
        print("IP:   " + IP)
        # print("IP:   " + self.ip)
        print("HOST: " + self.host)
        print("PORT: " + str(PORT))

