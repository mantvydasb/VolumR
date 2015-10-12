import socket
import server
__author__ = 'mantvydas'


def increaseVolume():
    connection.send(bytes("vol-55", "utf8"))

print("Connecting to client...")
connection = socket.socket()
connection.connect((server.IP, server.PORT))
print(str(connection.recv(1024), "utf8"))
print("Connected to " + server.IP + ":" + str(server.PORT))
# print("Connected to " + socket.gethostbyname(socket.gethostname()) + ":" + str(volumr.PORT))
increaseVolume()
print("Sending volume increase command")
