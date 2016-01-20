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