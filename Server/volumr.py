import server
import registry_manager
import ip_retriever
import script_creator

__author__ = 'mantvydas'

registry_manager.writeToAutorun()
# serverIP = "10.53.12.29"
serverIP = ip_retriever.getIPAddress()
script_creator.install()
volumrServer = server.Server(serverIP)
