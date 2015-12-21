import win32api

import config
import os.path
import subprocess

APP_NAME = config.APP_NAME
HOME_PATH = os.path.curdir
VOLUMR_EXE_PATH = os.path.abspath(APP_NAME + ".exe")
WIN_DIR_PATH = win32api.GetWindowsDirectory()
NIRCMD_EXE_PATH = config.NIRCMD_EXE_PATH
NIRCMD_INSTALL_SCRIPT_PATH = "copy_nircmd.bat"

def createSilentLauncherScript():
    """
    Generates a VBS script that will launch volumr.exe in a silent mode (no UI will be shown) everytime the user boots up the machine.
    :return:
    """
    script = \
        "Set WshShell = CreateObject(" + '"WScript.Shell"'") \n" \
        "WshShell.Run" + ' "' + VOLUMR_EXE_PATH + '"' + ", 0 \n" \
        "Set WshShell = Nothing"
    createScriptFile(config.APP_SILENT_LAUNCHER, script)

def createNircmdInstallerScript():
    script = \
        "@echo Copying " + NIRCMD_EXE_PATH + " to Windows... \n" \
        "copy " + NIRCMD_EXE_PATH + " " + WIN_DIR_PATH + "\\" + NIRCMD_EXE_PATH + "\n"
    createScriptFile(NIRCMD_INSTALL_SCRIPT_PATH, script)

def installNircmd():
    isNircmdInstalled = os.path.isfile(WIN_DIR_PATH + "\\" + NIRCMD_EXE_PATH)
    if not isNircmdInstalled:
        subprocess.call(NIRCMD_INSTALL_SCRIPT_PATH)


def createScriptFile(fileName, script, mode="w+"):
    launcherFile = open(fileName, mode=mode);
    launcherFile.write(script)
    launcherFile.close()

def finaliseInstallation():
    createSilentLauncherScript()
    createNircmdInstallerScript()
    installNircmd()

def createScheduledTaskXML():
    script = '' \
        '<?xml version="1.0" encoding="UTF-16"?>'\
        '<Task version="1.2" xmlns="http://schemas.microsoft.com/windows/2004/02/mit/task">'\
          '<RegistrationInfo>' \
            '<Date>2015-12-21T16:50:14.1041484</Date>'\
            '<Author>VolumR</Author>' \
            '<URI>v8</URI>'\
          '</RegistrationInfo>'\
          '<Triggers>'\
            '<BootTrigger>'\
              '<Enabled>true</Enabled>'\
            '</BootTrigger>'\
          '</Triggers>'\
          '<Principals>'\
            '<Principal id="Author">'\
              '<UserId>S-1-5-18</UserId>'\
              '<RunLevel>HighestAvailable</RunLevel>'\
            '</Principal>'\
          '</Principals>'\
          '<Settings>'\
            '<MultipleInstancesPolicy>IgnoreNew</MultipleInstancesPolicy>'\
            '<DisallowStartIfOnBatteries>true</DisallowStartIfOnBatteries>'\
            '<StopIfGoingOnBatteries>true</StopIfGoingOnBatteries>'\
            '<AllowHardTerminate>true</AllowHardTerminate>'\
            '<StartWhenAvailable>false</StartWhenAvailable>'\
            '<RunOnlyIfNetworkAvailable>false</RunOnlyIfNetworkAvailable>'\
            '<IdleSettings>'\
              '<StopOnIdleEnd>true</StopOnIdleEnd>'\
              '<RestartOnIdle>false</RestartOnIdle>'\
            '</IdleSettings>'\
            '<AllowStartOnDemand>true</AllowStartOnDemand>'\
            '<Enabled>true</Enabled>'\
            '<Hidden>false</Hidden>'\
            '<RunOnlyIfIdle>false</RunOnlyIfIdle>'\
            '<WakeToRun>false</WakeToRun>'\
            '<ExecutionTimeLimit>PT72H</ExecutionTimeLimit>'\
            '<Priority>7</Priority>'\
          '</Settings>'\
          '<Actions Context="Author">'\
            '<Exec>'\
              '<Command>powershell</Command>'\
              '<Arguments>Start-Process E:\Dev\Android\VolumR\Server\dist\volumr_silent.vbs</Arguments>'\
              '<WorkingDirectory>E:\Dev\Android\VolumR\Server\dist\</WorkingDirectory>'\
            '</Exec>'\
          '</Actions>'\
        '</Task>'\

    createScriptFile(config.APP_SILENT_LAUNCHER, script)

# schtasks /create /tn TaskNameFromCommand /tr "powershell Start-Process E:\Dev\Android\VolumR\Server\dist\volumr.exe" /sc onstart
# schtasks /create /tn TaskNameFromCommand /tr "powershell Start-Process E:\Dev\Android\VolumR\Server\dist\volumr.exe" /sc onstart /ru system
# schtasks.exe /Create /XML "E:\Dev\Android\VolumR\Server\TaskVolumeController.xml" /tn tasknameFromXl