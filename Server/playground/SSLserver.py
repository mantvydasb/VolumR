import socket, ssl

bindsocket = socket.socket()
bindsocket.bind(("10.53.12.42", 8506))
bindsocket.listen(5)

def do_something(connstream, data):
    print("do_something:", data)
    return False

def deal_with_client(connstream):
    data = connstream.read()
    while data:
        if not do_something(connstream, data):
            break
        data = connstream.read()

while True:
    newsocket, fromaddr = bindsocket.accept()
    connstream = ssl.wrap_socket(newsocket,
                                 server_side=True,
                                 certfile="volumr.crt",
                                 keyfile="volumr.key")

    try:
        connstream.do_handshake(False)
    except:
        pass


    try:
        deal_with_client(connstream)
    finally:
        print("done")
        # connstream.shutdown(socket.SHUT_RDWR)
        # connstream.close()