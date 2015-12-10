import socket, ssl

bindsocket = socket.socket()
bindsocket.bind(("10.53.12.78", 8506))
bindsocket.listen(5)

def do_something(data):
    print(data)
    return False

def deal_with_client(connstream):
    while True:
        data = connstream.recv(1024).decode()
        if data:
            do_something(data)
        # if not do_something(connstream, data):
        #     break
        # data = connstream.read()

while True:
    newsocket, fromaddr = bindsocket.accept()
    connstream = ssl.wrap_socket(newsocket,
                                 server_side=True,
                                 ca_certs="volumr.crt",
                                 certfile="volumr.crt",
                                 keyfile="volumr.key"
                                 )

    try:
        deal_with_client(connstream)
    finally:
        print("done")
        # connstream.shutdown(socket.SHUT_RDWR)
        # connstream.close()