import config
import os.path

APP_NAME = config.APP_NAME
VOLUMR_PATH = os.path.abspath(APP_NAME + ".exe")

SCRIPT_BODY = \
    "Set WshShell = CreateObject(" + '"WScript.Shell"'") \n" \
    "WshShell.Run" + ' "' + VOLUMR_PATH + '"' + ", 0 \n" \
    "Set WshShell = Nothing"


def createScript():
    """
    Generates a VBS script that will launch volumr.exe in a silent mode (no UI will be shown) everytime the user boots up the machine.
    :return:
    """
    launcherFile = open(config.APP_SILENT_LAUNCHER, mode="w+");
    launcherFile.write(SCRIPT_BODY)
    launcherFile.close()

createScript()
