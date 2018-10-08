#!/home/rokner/anaconda3/envs/misc/bin/python

import shlex
from subprocess import Popen, PIPE, call
import time
import sys

TITLE = 'metadata title'

def getInfo(type):
    cmd = "playerctl -p spotify " + type
    process = Popen(shlex.split(cmd), stdout=PIPE)
    (out, _) = process.communicate()
    exit_code = process.wait()
    if  exit_code==0:
        return out.decode('utf-8').strip()
    return ''

songs = int(sys.argv[1])
currSong = getInfo(TITLE)

while  songs > 0:
    time.sleep(1)
    newSong = getInfo(TITLE)
    if  newSong != currSong:
        currSong = newSong
        songs -= 1

call(['systemctl', 'suspend'])
