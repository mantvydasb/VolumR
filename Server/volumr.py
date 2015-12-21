import server
import ip_retriever
import installation

__author__ = 'mantvydas'

serverIP = ip_retriever.getIPAddress()
installation.finaliseInstallation()
volumrServer = server.Server(serverIP)

