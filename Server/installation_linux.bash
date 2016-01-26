#!/bin/bash
echo Adding to auto startup...
cp volumr.desktop /usr/share/applications/volumr.desktop
cp volumr.desktop ~/.config/autostart/ && echo Done successfully || echo Something went wrong..
