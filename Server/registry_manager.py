import winreg
import ip_retriever

__author__ = 'mantvydas'
PATH_AUTORUN = "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"
KEY_AUTORUN = winreg.CreateKey(winreg.HKEY_CURRENT_USER, PATH_AUTORUN)
APP_NAME = "volumr_silent"
PATH_HOME_DIR = ip_retriever.PATH_HOME_DIR
PATH_FILE = PATH_HOME_DIR + "\\" + APP_NAME + ".vbs"

def deleteFromAutorun():
    try:
        winreg.DeleteValue(KEY_AUTORUN, APP_NAME)
    except OSError:
        print(APP_NAME + " does not seem to be in the registry..")
        pass


def isInAutorun():
    try:
        return winreg.QueryValueEx(KEY_AUTORUN, APP_NAME)
    except OSError:
        return False
        pass


def writeToAutorun():
    """
    Update windows registry to automatically start the VolumR server on boot;
    """
    winreg.SetValueEx(KEY_AUTORUN, APP_NAME, 0, winreg.REG_SZ, '"' + PATH_FILE + '"')
