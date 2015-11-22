import server
import registry_manager
import ip_retriever
import launcher_creator

__author__ = 'mantvydas'

registry_manager.writeToAutorun()
serverIP = ip_retriever.getIPAddress()
volumrServer = server.Server(serverIP)
launcher_creator.createLauncherScript()