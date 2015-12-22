import win32api
import config
import os
import subprocess
import shutil
import getpass

APP_NAME = config.APP_NAME
HOME_PATH = os.path.curdir
VOLUMR_EXE_PATH = os.path.abspath(APP_NAME + ".exe")
WIN_DIR_PATH = win32api.GetWindowsDirectory()
NIRCMD_EXE_PATH = config.NIRCMD_EXE_PATH
SCHEDULED_TASK_PATH = "scheduled-task"

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

def copyNircmdToWindowsDirectory():
    isNircmdInstalled = os.path.isfile(WIN_DIR_PATH + "\\" + NIRCMD_EXE_PATH)

    if not isNircmdInstalled:
        print("Finishing installtion..")
        shutil.copy(NIRCMD_EXE_PATH, WIN_DIR_PATH)

def importScheduledTaskToSystem():
    subprocess.call('schtasks.exe /Create /XML ' + SCHEDULED_TASK_PATH + ' /tn ' + APP_NAME + '-background-service')

def createScriptFile(fileName, script, mode="w+"):
    launcherFile = open(fileName, mode=mode);
    launcherFile.write(script)
    launcherFile.close()

def finaliseInstallation():
    createSilentLauncherScript()
    copyNircmdToWindowsDirectory()
    createScheduledTask()

def createScheduledTask():
    securityId = subprocess.check_output("wmic sysaccount where name='system' get sid").decode("utf8")
    securityId = securityId.split("\r\r\n")[1].split("  ")[0]

    command = VOLUMR_EXE_PATH
    arguments = ''

    silentLauncherPath = os.path.abspath(config.APP_SILENT_LAUNCHER)
    workingDirectory = os.path.dirname(silentLauncherPath)

    script = '' \
        '<?xml version="1.0" encoding="UTF-16"?>\n'\
        '<Task version="1.2" xmlns="http://schemas.microsoft.com/windows/2004/02/mit/task">\n'\
          '<RegistrationInfo>\n'\
            '<Date>2015-12-21T16:50:14.1041484</Date>\n'\
            '<Author>' + APP_NAME + '</Author>' \
            '<URI>v8</URI>\n'\
          '</RegistrationInfo>\n'\
          '<Triggers>\n'\
            '<BootTrigger>\n'\
              '<Enabled>true</Enabled>\n'\
            '</BootTrigger>\n'\
          '</Triggers>\n'\
          '<Principals>\n'\
            '<Principal id="Author">\n'\
              '<UserId>' + securityId + '</UserId>\n'\
              '<RunLevel>HighestAvailable</RunLevel>\n'\
            '</Principal>\n'\
          '</Principals>\n'\
          '<Settings>\n'\
            '<MultipleInstancesPolicy>IgnoreNew</MultipleInstancesPolicy>\n'\
            '<DisallowStartIfOnBatteries>true</DisallowStartIfOnBatteries>\n'\
            '<StopIfGoingOnBatteries>true</StopIfGoingOnBatteries>\n'\
            '<AllowHardTerminate>true</AllowHardTerminate>\n'\
            '<StartWhenAvailable>false</StartWhenAvailable>\n'\
            '<RunOnlyIfNetworkAvailable>false</RunOnlyIfNetworkAvailable>\n'\
            '<IdleSettings>\n'\
              '<StopOnIdleEnd>true</StopOnIdleEnd>\n'\
              '<RestartOnIdle>false</RestartOnIdle>\n'\
            '</IdleSettings>\n'\
            '<AllowStartOnDemand>true</AllowStartOnDemand>\n'\
            '<Enabled>true</Enabled>\n'\
            '<Hidden>false</Hidden>\n'\
            '<RunOnlyIfIdle>false</RunOnlyIfIdle>\n'\
            '<WakeToRun>false</WakeToRun>\n'\
            '<ExecutionTimeLimit>PT72H</ExecutionTimeLimit>\n'\
            '<Priority>7</Priority>\n'\
          '</Settings>\n'\
          '<Actions Context="Author">\n'\
            '<Exec>\n'\
              '<Command>' + command +'</Command>\n'\
              '<Arguments>' + arguments +'</Arguments>\n'\
              '<WorkingDirectory>' + workingDirectory + '</WorkingDirectory>\n'\
            '</Exec>\n'\
          '</Actions>\n'\
        '</Task>\n'\

    createScriptFile(SCHEDULED_TASK_PATH, script)
    importScheduledTaskToSystem()