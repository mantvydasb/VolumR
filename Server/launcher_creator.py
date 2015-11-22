import ip_retriever


VOLUMR_PATH = ip_retriever.PATH_HOME_DIR + "\\volumr.exe"
SCRIPT_BODY = \
    "Set WshShell = CreateObject(" + '"WScript.Shell"'") \n" \
    "WshShell.Run chr(34) & " + '"' + VOLUMR_PATH + '"' + ", 0 \n" \
    "Set WshShell = Nothing"


def createLauncherScript():
    launcherFile = open("volumr_silent.vbs", mode="w+");
    launcherFile.write(SCRIPT_BODY)
    launcherFile.close()


createLauncherScript()
