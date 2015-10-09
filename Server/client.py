import socket
import volumr
__author__ = 'mantvydas'

print("Connecting to client...")

socket = socket.socket()
socket.connect((socket.gethostname(), volumr.PORT))
print(socket.recv(1024))

