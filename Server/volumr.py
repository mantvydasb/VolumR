import socket


class Server:
    ip = 10
    port = 0

    def __init__(self):
        self.ip = "192.168.2.6"
        self.port = 8506

    def getIp(self):
        print(self.ip)

