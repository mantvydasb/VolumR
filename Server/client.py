# import socket
# import server
# __author__ = 'mantvydas'
#
#
# def increaseVolume():
#     connection.send(bytes("vol-55", "utf8"))
#
# print("Connecting to client...")
# connection = socket.socket()
# connection.connect((server.IP, server.PORT))
# print(str(connection.recv(1024), "utf8"))
# print("Connected to " + server.IP + ":" + str(server.PORT))
# increaseVolume()
# print("Sending volume increase command")
import threading
import win32api
import win32con


def moveRight():
    win32api.keybd_event(win32con.VK_RIGHT, 0, 0, 0)
    threading.Timer(1.0, moveRight).start()

# moveRight()

# import socket
# import sys
# import struct
#
# address = ('localhost', 6005)
# client_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# client_socket.connect(address)
#
# messages = ["foobar", "barbaz", "bazquxfad", "Jimmy Carter"]
#
# for s in messages:
#
#     totallen = len(s)
#     pack1 = struct.pack('>I', totallen) # the first part of the message is length
#     client_socket.sendall(pack1)
#     client_socket.sendall(s.encode('utf8'))
#
