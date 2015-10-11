from os import popen
import os
import volumr
import subprocess

START_FROM = 0
IPv4_INDEXES = []
SEARCH_STRING = "IPv4 Address. . . . . . . . . . . : "


def getLocalIp():
    output = getIpconfigOutput()
    print(output)
    findipv4Indexes(output, START_FROM)


    # split output with IPs
    ip = output.split(SEARCH_STRING.encode("utf8"), 2)
    print(ip)


def findipv4Indexes(output, startFrom):
    index = output.find(SEARCH_STRING.encode("utf8"), startFrom)

    if index > 0:
        print(index)
        IPv4_INDEXES.append(index)
        findipv4Indexes(output, index + 1)


def getIpconfigOutput():
    return subprocess.check_output("ipconfig /all", shell=True)


getLocalIp()

# volumrServer = volumr.Server()


