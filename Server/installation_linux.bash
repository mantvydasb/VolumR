#!/bin/bash
# Run this script as sudo to install VolumR server into your applications list and also autostart it when the machine boots;
echo Adding to auto startup...
cp volumr.desktop /usr/share/applications/volumr.desktop
cp volumr.desktop ~/.config/autostart/ && echo Done successfully || echo Something went wrong..
