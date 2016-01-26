import server
import ip_retriever
import installation
import utils

__author__ = 'mantvydas'

isThisLinux = utils.isThisLinux()
serverIP = ip_retriever.getIPAddress()
installation.finaliseInstallation(isThisLinux)
volumrServer = server.Server(serverIP, isThisLinux)