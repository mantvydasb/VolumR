from os import popen
import os
import volumr
import subprocess


output = subprocess.check_output("ipconfig",  shell=True)
print(output)
res = output.index("Ethernet adapter Ethernet:".encode("utf8"))
print(res)


volumrServer = volumr.Server()


