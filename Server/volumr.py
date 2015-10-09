import socket

PORT = 8506


class Server:
    host = ""
    ip = 10
    port = 0
    server = ""

    def __init__(self):
        self.host = socket.gethostname()
        self.ip = socket.gethostbyname(self.host)
        self.port = PORT
        self.getServerInfo()
        self.startServer()

    def startServer(self):
        print("Starting server..")
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.server.bind((self.host, self.port))
        self.server.listen()
        print("Server started")

        while True:
            connectionSocket, clientAddress = self.server.accept()
            print('Got connection from', clientAddress)
            connectionSocket.send('Thank you for connecting')
            self.server.close()

    def getServerInfo(self):
        print("*** SERVER INFO ***")
        print("IP:   " + self.ip)
        print("HOST: " + self.host)
        print("PORT: " + str(self.port))

