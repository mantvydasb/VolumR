import server
import ip_retriever
# import installation
import utils

__author__ = 'mantvydas'

isThisLinux = utils.isThisLinux()
# serverIP = ip_retriever.getIPAddress()
serverIP = "192.168.2.2"
# installation.finaliseInstallation()
volumrServer = server.Server(serverIP, isThisLinux)

#