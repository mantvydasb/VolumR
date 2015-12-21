import winreg
import config
import os.path

__author__ = 'mantvydas'
PATH_AUTORUN = "SOFTWARE\Microsoft\Windows\CurrentVersion\Run"
KEY_AUTORUN = winreg.CreateKey(winreg.HKEY_CURRENT_USER, PATH_AUTORUN)
APP_NAME = config.APP_NAME
PATH_SILENT_LAUNCHER = os.path.abspath(config.APP_SILENT_LAUNCHER)
# PATH_SILENT_LAUNCHER = PATH_HOME_DIR + "\\" + config.APP_SILENT_LAUNCHER

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
    if not isInAutorun():
        winreg.SetValueEx(KEY_AUTORUN, APP_NAME, 0, winreg.REG_SZ, '"' + PATH_SILENT_LAUNCHER + '"')
