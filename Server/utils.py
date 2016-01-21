import subprocess

def stringToInt(value):
    try:
        value = int(value)
    except:
        pass
    return value

def isThisLinux():
    osName = subprocess.check_output("uname")
    osName = osName.decode('utf8').lower()
    return True if osName.__contains__("lin") else False

def importWin32libraries():
    api = __import__("win32api")
    constants = __import__("win32con")
    return api, constants