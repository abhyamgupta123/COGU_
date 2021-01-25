#!/usr/bin/python2.7
import os,platform,psutil,uuid,re,socket
import subprocess,fnmatch
import alsaaudio
import logging
from pubnub.callbacks import SubscribeCallback
from pubnub.enums import PNStatusCategory
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from time import sleep
import requests, time, base64


DEVICE_NAME = "ENTER_YOUR_DEVICE_NAME"                                                                  # Unique Device name
CHANNEL = "ENTER_YOUR_CHANNEL_NAME"                                                                     # Channel name
flag = 1

pnconfig = PNConfiguration()
pnconfig.publish_key = "ENTER_YOUR_PUBLISH_KEY"                                                         # PubNub's Publish key:-
pnconfig.subscribe_key = "ENTER_YOUR_SUBSCRIBE_KEY"                                                     # PubNub's Subscribe key
pnconfig.uuid = DEVICE_NAME                                                                             # Unique identifier name over channel
# pnconfig.reconnect_policy = PNReconnectionPolicy.LINEAR

pubnub = PubNub(pnconfig)

class MySubscribeCallback(SubscribeCallback):
  def presence(self, pubnub, event):
    var1_presence = "echo "+("[PRESENCE: {}]".format(event.event))
    os.system(var1_presence)
    var2_presence = "echo "+("uuid: {}, channel: {}".format(event.uuid, event.channel))
    os.system(var2_presence)
    timeout = (" {} uuid: {}, channel: {}".format(event.event, event.uuid, event.channel))
    if event.event == "timeout":
        os.system("sudo systemctl restart cogu.service")
        # os.system( "/usr/local/bin/cogu_alert./sh COGU_PRESENCE $" + "\"" + timeout + "\"" + " > " + "/dev/null" + " 2>&1" )
    pass

  def status(self, pubnub, event):
    global flag
    if event.category == PNStatusCategory.PNConnectedCategory:
      var1_status = "echo "+("[STATUS: PNConnectedCategory]")
      os.system(var1_status)
      var2_status = "echo "+("connected to channels: {}".format(event.affected_channels))
      os.system(var2_status)
      flag = 0

    elif event.category == PNStatusCategory.PNDisconnectedCategory:
      var3_status = "echo "+("STATUS: PNDIscoonectedCategory")
      os.system(var3_status)
      flag = 1

    pass

  def message(self, pubnub, event):
    try:
        if (str(event.message['comm']) == "DISSMISS"):
          os.system("notify-send 'COGU' 'COGU is Stopped..!!'")
          print "The publisher has ended the session."
          os._exit(0)

        elif (str(event.message['comm']) == "requestSysActivity"):

          #TO GET IP ADDRRESS OF SYSTEM:
          s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
          try:
            # doesn't even have to be reachable
            s.connect(('10.255.255.255', 1))
            IP = s.getsockname()[0]
          except Exception:
            IP = '127.0.0.1'
          finally:
            s.close()

          #TO GET CONNECTED NETWORK WIFI SSID:
          out_object = subprocess.Popen(["iwgetid","-r"],stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
          stdout, stderr = out_object.communicate()

          #TO GET BATTERY DEATILS:
          #1.bettery percet
          for file in os.listdir("/sys/class/power_supply/BAT0/"):
            if (fnmatch.fnmatch(file, '*charge_full') or fnmatch.fnmatch(file, '*power_full')):
              battery_full = file
            if (fnmatch.fnmatch(file, '*charge_now') or fnmatch.fnmatch(file, '*power_now')):
              battery_now = file

          power_now = open("/sys/class/power_supply/BAT0/{}".format(battery_now), "r").readline()
          power_full = open("/sys/class/power_supply/BAT0/{}".format(battery_full), "r").readline()
          # print float(power_now)/float(power_full) * 100 , "%"
          bat_level = str(int(float(power_now)/float(power_full) * 100)) + "%"
          #2.battery charging state:
          state_out = subprocess.check_output(
            'upower -i /org/freedesktop/UPower/devices/battery_BAT0 | grep state',
            shell=True,
            )
          state_out2 = state_out.split()
          state = state_out2[1]



          the_message = {}
          the_message["Platform"] = platform.system()
          the_message["arch"] = platform.machine()
          the_message["mac-addr"] = ':'.join(re.findall('..', '%012x' % uuid.getnode()))
          the_message['ram']=str(round(psutil.virtual_memory().total / (1024.0 **3)))+" GB"
          the_message["name"] = platform.uname()[1]
          the_message["CPU"] = str(psutil.cpu_percent()) + "%"
          the_message["RamUsage"] = psutil.virtual_memory().percent
          the_message["IP_addr"] = IP
          the_message["comm"] = "sysInfoPub"
          the_message["pid"] = str(os.getpid())
          the_message["volume"] = alsaaudio.Mixer().getvolume()
          the_message["state"] = state

          if stdout:
            the_message["ssid"] = stdout.rstrip()
          else:
            the_message["ssid"] = "Not able to get SSID of Connected WIFI"

          if bat_level:
            the_message["bat_level"] = bat_level
          else:
            the_message["bat_level"] = "NOT AVAILABLE"


          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

        elif (str(event.message['comm']) == "systemctl suspend" or str(event.message['comm']) == "suspend"):
          os.system("notify-send 'COGU' 'Going to sleep..!!'")
          comm = "systemctl suspend"
          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System is suspended"
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          os.system(comm)

        elif (str(event.message['comm']) == "shutdown now" or str(event.message['comm']) == "shutdown"):
          os.system("notify-send 'COGU' 'Shutting Down System..!!'")
          comm = "shutdown now"
          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System is Shutting Down..."
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          os.system(comm)

        elif(str(event.message['comm']) == "reboot"):
          os.system("notify-send 'COGU' 'Rebooting your System..!!'")
          comm = str(event.message['comm'])
          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System Restarted..."
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          os.system(comm)

        elif (str(event.message['comm']) == "loginctl unlock-session" or str(event.message['comm']) == "unlock"):
          #Obtain the session number:
          session = subprocess.check_output(
            'loginctl list-sessions',
            shell=True,
            )

          output = session.split()
          session_no = output[4]

          comm = "loginctl unlock-session " + session_no
          os.system("echo 'Desktop unlocked..!!'")
          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System Unlocked..."
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          os.system(comm)

        elif (str(event.message['comm']) == "super unlock"):
          comm = "/bin/bash /etc/pam.d/.cogu_ULK_config.sh"
          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System is unlocked (SUPER Unlock)...!!"
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass
          os.system(comm)
          os.system("echo 'Desktop is unlocked as super user priviledges without entering password...!!'")

        elif (str(event.message['comm']) == "lock"):
          comm = "xdotool key Ctrl+alt+l"

          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "System is locked...!!"
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass


          os.system(comm)
          os.system("echo 'Desktop Locked..!!'")

        elif (str(event.message['comm']) == "ss"):
          # For capturing Screenshot:-
          screenshot_file_name = str(int(time.time())) + ".jpg"
          screenshot_file_path = "/usr/local/cogu_photos/cogu_screenshots/" + screenshot_file_name
          cap_command = "gnome-screenshot --file=" + screenshot_file_path
          os.system(cap_command)
          os.system("echo 'Screenshot Captured..!!'")

          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "Uploading Screenshot Picture..."
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          #Uploading picture:-
          params = (
                    ('expires', '1'),
                   )

          files = {
            'file': (screenshot_file_path, open(screenshot_file_path, 'rb')),
                  }

          response = requests.post('https://file.io/', params=params, files=files)


          #Exracting link:-
          json_data = response.json()
          unicode_link = json_data["link"]
          link = unicode_link.encode('ascii','ignore')
          os.system("echo " + link)
          os.system("echo 'Sending photo...!!'")

          the_message = {}
          the_message["comm"] = "culpritPhoto"
          the_message["url"] = link
          the_message["name"] = screenshot_file_name
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()
          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)


          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              print("something")

          os.system("echo sent...!!")



        elif (str(event.message['comm']) == "culprit"):
          # capturing photo
          culprit_file_name = str(int(time.time())) + ".jpg"
          culprit_file_path = "/usr/local/cogu_photos/cogu_culprit_photos/" + culprit_file_name
          cap_command = "fswebcam -r 640x480 --jpeg 85 -D 0.8 " + culprit_file_path
          os.system(cap_command)
          os.system("echo 'Picture Captured..!!'")

          the_message = {}
          the_message["comm"] = "command_status"
          the_message["message"] = "Uploading Picture..."
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)
              pass

          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              pass

          #Uploading picture:-
          params = (
                    ('expires', '1'),
                   )

          files = {
            'file': (culprit_file_path, open(culprit_file_path, 'rb')),
                  }

          response = requests.post('https://file.io/', params=params, files=files)


          #Exracting link:-
          json_data = response.json()
          unicode_link = json_data["link"]
          link = unicode_link.encode('ascii','ignore')
          os.system("echo "+link)
          os.system("echo 'Sending photo...!!'")

          the_message = {}
          the_message["comm"] = "culpritPhoto"
          the_message["url"] = link
          the_message["name"] = culprit_file_name
          envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()
          if envelope.status.is_error():
              print("[PUBLISH: fail]")
              print("error: %s" % status.error)


          else:
              print("[PUBLISH: sent]")
              print("timetoken: %s" % envelope.result.timetoken)
              print("something")

          os.system("echo sent...!!")

        else:
          if (str(event.message['comm']) == "sysInfoPub" or str(event.message['comm']) == "Harmless." or str(event.message['comm']) == "culpritPhoto" or str(event.message['comm']) == "command_status"):
            pass

          else:
            comm = str(event.message['comm'])
            os.system(comm)
            the_message = {}
            the_message["comm"] = "command_status"
            the_message["message"] = "'{}'".format(comm)
            envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

            if envelope.status.is_error():
                print("[PUBLISH: fail]")
                print("error: %s" % status.error)

            else:
                print("[PUBLISH: sent]")
                print("timetoken: %s" % envelope.result.timetoken)

	    os.system("echo " + the_message["message"])

    except :
        print("Error has occured and handled. Maybe message doesn't contained desired values")



logger = logging.getLogger('pubnub')
logger.setLevel(logging.DEBUG)
fh = logging.FileHandler('/var/local/cogu_logging.log')
fh.setLevel(logging.DEBUG)
logger.addHandler(fh)


pubnub.add_listener(MySubscribeCallback())
sleep(1)
pubnub.subscribe().channels(CHANNEL).with_presence().execute()
sleep(3)

if flag == 1:
  os.system("echo Connecting...!!")
  os.system("sudo systemctl restart cogu.service")
