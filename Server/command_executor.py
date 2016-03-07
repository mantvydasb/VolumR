import utils
import config
import subprocess

MAX_VOLUME = 65535

NIRCMD_EXE_PATH = config.NIRCMD_EXE_PATH
RIGHT = "Right"
LEFT = "Left"
SPACE = "space"

class CommandExecutor():
    isThisLinux = True

    def __init__(self, isThisLinux):
        self.isThisLinux = isThisLinux
        # return self

    def executeCommand(self, command, value):
        if command == "volume":
            self.changeVolume(value)
        elif command == "right":
            self.pressRight()
        elif command == "left":
            self.pressLeft()
        elif command == "space":
            self.pressSpace()

    def extractCommand(self, message):
        message = message.split(";", 1)
        command, value = message[0].split(":", 1)
        value = utils.stringToInt(value)
        return command, value

    def changeVolume(self, message):
        newVolume = (int(message) / 100 * MAX_VOLUME)

        if newVolume / MAX_VOLUME < 1:
            if self.isThisLinux:
                newVolume = int(newVolume)
                command = "amixer sset 'Master' " + str(message) + "%"
            else:
                command = NIRCMD_EXE_PATH + " setvolume 0 " + str(newVolume) + " " + str(newVolume)
            subprocess.Popen(command, shell=True)

    def importWin32Libraries(self):
        win32api, win32con = utils.importWin32libraries()

    def pressRight(self):
        self.pressVirtualKey(RIGHT)

    def pressLeft(self):
        self.pressVirtualKey(LEFT)

    def pressSpace(self):
        self.pressVirtualKey(SPACE)

    def pressVirtualKey(self, virtualKey):
        if self.isThisLinux:
            command = 'xdotool key '
            if virtualKey == RIGHT:
                subprocess.call(command + RIGHT, shell=True)
            if virtualKey == LEFT:
                subprocess.call(command + LEFT, shell=True)
            if virtualKey == SPACE:
                subprocess.call(command + SPACE, shell=True)
        else:
            if virtualKey == RIGHT:
                win32api.keybd_event(win32con.VK_RIGHT, 0, 0, 0)
            if virtualKey == LEFT:
                win32api.keybd_event(win32con.VK_LEFT, 0, 0, 0)
            if virtualKey == SPACE:
                win32api.keybd_event(win32con.VK_SPACE, 0, 0, 0)

