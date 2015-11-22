import config

VOLUMR_PATH = config.PATH_HOME_DIR + "\\volumr.exe"
SCRIPT_BODY = \
    "Set WshShell = CreateObject(" + '"WScript.Shell"'") \n" \
    "WshShell.Run" + ' "' + VOLUMR_PATH + '"' + ", 0 \n" \
    "Set WshShell = Nothing"


def createScript():
    """
    Generates a VBS script that will launch volumr.exe in a silent mode (no UI will be shown) everytime the user boots up the machine.
    :return:
    """
    launcherFile = open("volumr_silent.vbs", mode="w+");
    launcherFile.write(SCRIPT_BODY)
    launcherFile.close()

createScript()
