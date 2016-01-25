import socket
import subprocess
import utils
import ssl
# import win32api
# import win32con
import config
# import installation

__author__ = 'mantvydas'
PORT = 8506
MAX_VOLUME = 65535
STOP_SERVER = 'STOP_SERVER'
RESTARTING_SERVER = "Client disconnected, restarting server.."
KEY = "server.key"
CERTIFICATE = "server.crt"
NIRCMD_EXE_PATH = config.NIRCMD_EXE_PATH
RIGHT = "Right"
LEFT = "Left"
SPACE = "space"


class Server:
    host = None
    ip = None
    serverSocket = None
    clientSocket = None
    isThisLinux = None

    def __init__(self, ipAddress, isThisLinux):
        """
        Opens a socket for the specified IP address that listens for commands from the client (volume change, seek, play/pause, etc);
        """
        self.ip = str(ipAddress)
        self.isThisLinux = isThisLinux
        self.startServer()
        secureClientSocket = self.acceptConnections()
        self.receiveMessages(secureClientSocket)

    def startServer(self):
        # try:
        self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM, 0)
        self.serverSocket.bind((self.ip, PORT))
        self.serverSocket.listen(5)
        print("Server started on " + self.ip + ":" + str(PORT) + " and listening")
        # except socket.error as e:
        #     exceptionMessage = format(str(e))

            # installation.createScriptFile("crash.txt", exceptionMessage)
            # raise "Socket problems"

    def acceptConnections(self):
        while True:
            clientSocket, clientAddress = self.serverSocket.accept()
            print('Connection from', clientAddress)
            return ssl.wrap_socket(clientSocket,
                                                 keyfile=KEY,
                                                 server_side=True,
                                                 certfile=CERTIFICATE)

    def receiveMessages(self, secureClientSocket):
        while True:
            message = secureClientSocket.recv(2048).decode()

            if message == STOP_SERVER:
                self.restartServer(secureClientSocket)
                break
            elif message != '':
                command, value = self.extractCommand(message)
                print(command, value)
                self.executeCommand(command, value)

    def executeCommand(self, command, value):
        if command == "volume":
            self.changeVolume(value)

        elif command == "right":
            self.pressRight()

        elif command == "left":
            self.pressLeft()

        elif command == "space":
            self.pressSpace()

    def extractCommand(self, message):
        message = message.split(";", 1)
        command, value = message[0].split(":", 1)
        value = utils.stringToInt(value)
        return command, value

    def changeVolume(self, message):
        newVolume = (int(message) / 100 * MAX_VOLUME)

        if newVolume / MAX_VOLUME < 1:
            if self.isThisLinux:
                # change volume in linux;
                newVolume = int(newVolume)
                command = "amixer sset 'Master' " + str(message) + "%"
            else:
                # change volume in windows;
                command = NIRCMD_EXE_PATH + " setvolume 0 " + str(newVolume) + " " + str(newVolume)
            subprocess.Popen(command, shell=True)

    def importWin32Libraries(self):
        win32api, win32con = utils.importWin32libraries()

    def pressRight(self):
        self.pressVirtualKey(RIGHT)

    def pressLeft(self):
        self.pressVirtualKey(LEFT)

    def pressSpace(self):
        self.pressVirtualKey(SPACE)

    def restartServer(self, secureClientSocket):
        print(RESTARTING_SERVER)
        secureClientSocket.close()
        if not secureClientSocket._closed:
            self.__init__(self.ip, self.isThisLinux)

    def pressVirtualKey(self, virtualKey):
        if self.isThisLinux:
            command = 'xdotool key '
            if virtualKey == RIGHT:
                subprocess.call(command + RIGHT, shell=True)
            if virtualKey == LEFT:
                subprocess.call(command + LEFT, shell=True)
            if virtualKey == SPACE:
                subprocess.call(command + SPACE, shell=True)
        else:
            if virtualKey == RIGHT:
                win32api.keybd_event(win32con.VK_RIGHT, 0, 0, 0)
            if virtualKey == LEFT:
                win32api.keybd_event(win32con.VK_LEFT, 0, 0, 0)
            if virtualKey == SPACE:
                win32api.keybd_event(win32con.VK_SPACE, 0, 0, 0)

# todo move out commands to a separate file
