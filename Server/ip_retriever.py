import subprocess
import utils

__author__ = 'mantvydas'
SEARCH_STRING = "IPv4 Address. . . . . . . . . . . : "
PATH_SETTINGS_FILE = "settings.ini"

def getIPAddress():
    """
    Tries retrieving user IP from the file if the user has already chosen the IP previously, else presents user
    with a list of IPs associated with this PC and asks him to select the one he's planning on running the server on;
    :returns string IPAddress the server will be running on;
    """
    serverIp = readServerIPfromFile()
    if not serverIp:
        output = getIPConfigOutput()
        IPAddresses = extractIPAdresses(output)
        presentIPAddresses(IPAddresses)

        # ask the user to select IP address
        index = getIPIndex(IPAddresses)
        serverIp = IPAddresses[index]

        writeServerIPtoFile(serverIp)
    return serverIp


def getIPConfigOutput():
    try:
        return subprocess.check_output("ipconfig /all")
    except:
        pass


def extractIPAdresses(output):
    if utils.isThisLinux():
        IPAddresses = subprocess.check_output("ifconfig | grep -Eo 'inet (addr:)?([0-9]*\.){3}[0-9]*' | grep -Eo '([0-9]*\.){3}[0-9]*' | grep -v '127.0.0.1'", shell=True)
        IPAddresses = IPAddresses.decode('utf8').split()
    else:
        # assuming this is windows machine then
        IPAddressesRaw = output.split(SEARCH_STRING.encode("utf8"), 10)
        IPAddresses = []

        for item in IPAddressesRaw[1:IPAddressesRaw.__sizeof__()]:
            IPAddressesRaw = item.split("(".encode("utf8"), 1)[0]
            IPAddresses.append(str(IPAddressesRaw, "utf8"))
    return IPAddresses


def presentIPAddresses(IPAddresses):
    print("We're almost there, hommie! \nBelow are the IP addresses associated with your computer:")
    i = 0
    for address in IPAddresses:
        print("[" + str(i) + "] " + address)
        i += 1


def getIPIndex(IPAddresses):
    lastIPIndex = str(IPAddresses.__len__() - 1)
    print("Enter a number to indicate the IP of a computer you want to control the volume on:")

    try:
        index = int(input())
        if index >= 0 and index <= IPAddresses.__len__():
            print("You chose: " + IPAddresses[index])
            return index
        else:
            print("Dude! From 0 to " + lastIPIndex)
            return getIPIndex()
    except:
        pass

    return getIPIndex(IPAddresses)


def readServerIPfromFile():
    try:
        settingsFile = open(PATH_SETTINGS_FILE, mode='r+')
        savedIP = settingsFile.readline().strip("\n")
        settingsFile.close()
        return savedIP if not None else False
    except OSError:
        pass


def writeServerIPtoFile(serverIP):
    settingsFile = open(PATH_SETTINGS_FILE, mode='w+')
    settingsFile.write(serverIP)
    settingsFile.close()