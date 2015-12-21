import server
import ip_retriever
import script_creator

__author__ = 'mantvydas'

serverIP = ip_retriever.getIPAddress()
script_creator.finaliseInstallation()
volumrServer = server.Server(serverIP)

