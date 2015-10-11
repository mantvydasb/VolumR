from os import popen
import os
import volumr
import subprocess

START_FROM = 0
IPv4_INDEXES = []


def getLocalIp():
    output = getIpconfigOutput()
    print(output)
    findipv4Index(output, START_FROM)


def findipv4Index(output, startFrom):
    index = output.find("IPv4 Address".encode("utf8"), startFrom)

    if index > 0:
        print(index)
        IPv4_INDEXES.append(index)
        findipv4Index(output, index + 10)


def getIpconfigOutput():
    return subprocess.check_output("ipconfig /all", shell=True)


getLocalIp()

# volumrServer = volumr.Server()


