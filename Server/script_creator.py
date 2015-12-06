import win32api

import config
import os.path
import subprocess

APP_NAME = config.APP_NAME
HOME_PATH = os.path.curdir
VOLUMR_PATH = os.path.abspath(APP_NAME + ".exe")
WIN_DIR_PATH = win32api.GetWindowsDirectory()
NIRCMD_EXE_PATH = "nircmd.exe"
NIRCMD_INSTALL_BAT_PATH = "install.bat"

def createLauncherScript():
    """
    Generates a VBS script that will launch volumr.exe in a silent mode (no UI will be shown) everytime the user boots up the machine.
    :return:
    """
    script = \
        "Set WshShell = CreateObject(" + '"WScript.Shell"'") \n" \
        "WshShell.Run" + ' "' + VOLUMR_PATH + '"' + ", 0 \n" \
        "Set WshShell = Nothing"
    createScriptFile(config.APP_SILENT_LAUNCHER, script)

def createNircmdInstallationScript():
    script = "copy " + NIRCMD_EXE_PATH + " " + WIN_DIR_PATH + "\\" + NIRCMD_EXE_PATH
    createScriptFile(NIRCMD_INSTALL_BAT_PATH, script)

def installNircmd():
    subprocess.call(NIRCMD_INSTALL_BAT_PATH);

def createScriptFile(fileName, script):
    launcherFile = open(fileName, mode="w+");
    launcherFile.write(script)
    launcherFile.close()

def install():
    createLauncherScript()
    createNircmdInstallationScript()
    installNircmd()