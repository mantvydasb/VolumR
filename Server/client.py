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

moveRight()


