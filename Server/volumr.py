import server
import registry_manager
import ip_retriever

__author__ = 'mantvydas'

registry_manager.writeToAutorun()
serverIP = ip_retriever.getIPAddress()
volumrServer = server.Server(serverIP)

