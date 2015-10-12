import os
import server
import subprocess

SEARCH_STRING = "IPv4 Address. . . . . . . . . . . : "
HOME_DIR = os.path.dirname(os.path.realpath(__file__))
SETTINGS_FILE = open(HOME_DIR + "\settings.ini", mode='r+')
SERVER_IP = ''


def getIPAddresses():
    output = getIPConfigOutput()
    return extractIPAdresses(output)


def extractIPAdresses(output):
    IPAddressesRaw = output.split(SEARCH_STRING.encode("utf8"), 10)
    IPAddresses = []

    for item in IPAddressesRaw[1:IPAddressesRaw.__sizeof__()]:
        IPAddressesRaw = item.split("(".encode("utf8"), 1)[0]
        IPAddresses.append(str(IPAddressesRaw, "utf8"))
    return IPAddresses


def getIPConfigOutput():
    return subprocess.check_output("ipconfig /all")


def presentIPAddresses():
    print("We're almost there, hommie! \nBelow are the IP addresses associated with your computer:")
    i = 0
    for address in IPAddresses:
        print("[" + str(i) + "] " + address)
        i += 1


def getUserIPInput():
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
            return getUserIPInput()

    except ValueError:
        print("Dude, numbers please!")
        return getUserIPInput()


def getSavedIP():
    savedIP = SETTINGS_FILE.readline()
    return savedIP if not None else False


def saveServerIPtoFile():
    SETTINGS_FILE.write(SERVER_IP)
    SETTINGS_FILE.close()


def setAutoRunInRegistry():
    print("pienas")

SERVER_IP = getSavedIP().strip("\n")

if SERVER_IP == '':
    IPAddresses = getIPAddresses()
    presentIPAddresses()

    SERVER_IP = IPAddresses[getUserIPInput()]
    saveServerIPtoFile()



volumrServer = server.Server(SERVER_IP)

