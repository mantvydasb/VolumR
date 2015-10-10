import socket
import volumr
__author__ = 'mantvydas'


def main():
    print("Connecting to client...")
    connection = socket.socket()
    connection.connect((socket.gethostname(), volumr.PORT))
    print(connection.recv(1024))

main()

