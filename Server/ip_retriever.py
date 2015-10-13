import os
import subprocess

__author__ = 'mantvydas'
SEARCH_STRING = "IPv4 Address. . . . . . . . . . . : "
HOME_DIR = os.path.dirname(os.path.realpath(__file__))
SETTINGS_FILE = open(HOME_DIR + "\settings.ini", mode='r+')


def getIPAddress():
    serverIp = readServerIPfromFile()
    if serverIp == '':

        output = getIPConfigOutput()
        IPAddresses = extractIPAdresses(output)
        presentIPAddresses(IPAddresses)

        # ask the user to select IP address
        index = getIPIndex(IPAddresses)
        serverIp = IPAddresses[index]

        writeServerIPtoFile(serverIp)
    return serverIp


def getIPConfigOutput():
    return subprocess.check_output("ipconfig /all")


def extractIPAdresses(output):
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
    print("Enter a number from 0 to " + lastIPIndex +
          " to indicate the IP of a computer you want to control the volume on:")

    try:
        index = int(input())

        if 0 <= index < IPAddresses.__len__():
            print("You chose: " + str(index))
            return index
        else:
            print("Dude! From 0 to " + lastIPIndex)
            return getIPIndex()

    except ValueError:
        print("Dude, numbers please!")
        return getIPIndex()


def readServerIPfromFile():
    savedIP = SETTINGS_FILE.readline().strip("\n")
    return savedIP if not None else False


def writeServerIPtoFile(serverIP):
    SETTINGS_FILE.write(serverIP)
    SETTINGS_FILE.close()
