import server
import ip_retriever
# import installation_win
import utils

__author__ = 'mantvydas'

isThisLinux = utils.isThisLinux()
serverIP = ip_retriever.getIPAddress()
# installation.finaliseInstallation()
volumrServer = server.Server(serverIP, isThisLinux)