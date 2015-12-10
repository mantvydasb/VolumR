import socket, ssl

bindsocket = socket.socket()
bindsocket.bind(("192.168.2.3", 8506))
bindsocket.listen(5)

def do_something(data):
    print(data)
    return False

def deal_with_client(connstream):
    while True:
        data = connstream.recv(1024).decode()
        if data:
            do_something(data)

while True:
    print("Listening..")
    newsocket, fromaddr = bindsocket.accept()
    print("Incoming..")
    connstream = ssl.wrap_socket(newsocket,
                                 server_side=True,
                                 certfile="server.crt",
                                 ssl_version=ssl.PROTOCOL_TLSv1_2,
                                 keyfile="server.key"
                                 )
    print("Will deal with connection")

    try:
        deal_with_client(connstream)
    finally:
        print("done")
        # connstream.shutdown(socket.SHUT_RDWR)
        # connstream.close()
