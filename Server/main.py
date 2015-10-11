import volumr
import subprocess

SEARCH_STRING = "IPv4 Address. . . . . . . . . . . : "


def getIPAddresses():
    output = getIPConfigOutput()
    return extractIPAdresses(output)


def extractIPAdresses(output):
    IPAddressesRaw = output.split(SEARCH_STRING.encode("utf8"), 2)
    ip4Addresses = []

    for item in IPAddressesRaw[1:IPAddressesRaw.__sizeof__()]:
        IPAddressesRaw = item.split("(".encode("utf8"), 1)[0]
        ip4Addresses.append(str(IPAddressesRaw, "utf8"))
    return ip4Addresses


def getIPConfigOutput():
    return subprocess.check_output("ipconfig /all")


def presentIPAddresses():
    print("We're almost there, hommie! \nBelow are the IP addresses associated with your computer:")
    i = 0
    for address in ipAddresses:
        print("[" + str(i) + "] " + address)
        i += 1


def getUserIPInput():
    lastIPIndex = str(ipAddresses.__len__() - 1)
    print("Enter a number from 0 to " + lastIPIndex +
          " to indicate the IP of a computer you want to control the volume on:")

    try:
        index = int(input())
    except ValueError:
        print("Dude, numbers please!")
        getUserIPInput()
    # index = int(input())

    if 0 <= index < ipAddresses.__len__():
        print("You chose: " + str(index))
        return index
    else:
        print("Dude! From 0 to " + lastIPIndex)
        getUserIPInput()


ipAddresses = getIPAddresses()
presentIPAddresses()
IPIndex = getUserIPInput()
volumrServer = volumr.Server(ipAddresses[IPIndex])


