from time import sleep
import requests, time, base64
import os,platform,psutil,uuid,re,socket
import subprocess,fnmatch
import alsaaudio
import logging

class command_handler:
    def __init__(self, pubnub, event):
        self.pubnub = pubnub
        self.event = event

    def requestSystemActivity(self):
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


        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)
            pass

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            pass
    def suspend(self):
        os.system("notify-send 'COGU' 'Going to sleep..!!'")
        comm = "systemctl suspend"
        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "System is suspended"
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)
            pass

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            pass

        os.system(comm)

    def shutdown(self):
        os.system("notify-send 'COGU' 'Shutting Down System..!!'")
        comm = "shutdown now"
        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "System is Shutting Down..."
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)
            pass

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            pass

        os.system(comm)

    def reboot(self):
        os.system("notify-send 'COGU' 'Rebooting your System..!!'")
        comm = "reboot"
        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "System Restarted..."
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)
            pass

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            pass

        os.system(comm)

    def unlock(self):
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
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)
            pass

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            pass

        os.system(comm)

    def superUnlock(self):
        comm = "/bin/bash /etc/pam.d/.cogu_ULK_config.sh"
        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "System is unlocked (SUPER Unlock)...!!"
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

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

    def lock(self):
        comm = "xdotool key Ctrl+alt+l"

        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "System is locked...!!"
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

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

    def screenShot(self):
        # For capturing Screenshot:-
        screenshot_file_name = str(int(time.time())) + ".jpg"
        screenshot_file_path = "/usr/local/cogu_photos/cogu_screenshots/" + screenshot_file_name
        cap_command = "gnome-screenshot --file=" + screenshot_file_path
        os.system(cap_command)
        os.system("echo 'Screenshot Captured..!!'")

        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "Uploading Screenshot Picture..."
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

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

    def culprit(self):
        # capturing photo
        culprit_file_name = str(int(time.time())) + ".jpg"
        culprit_file_path = "/usr/local/cogu_photos/cogu_culprit_photos/" + culprit_file_name
        cap_command = "fswebcam -r 640x480 --jpeg 85 -D 0.8 " + culprit_file_path
        os.system(cap_command)
        os.system("echo 'Picture Captured..!!'")

        the_message = {}
        the_message["comm"] = "command_status"
        the_message["message"] = "Uploading Picture..."
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()

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
        envelope = self.pubnub.publish().channel(CHANNEL).message(the_message).sync()
        if envelope.status.is_error():
            print("[PUBLISH: fail]")
            print("error: %s" % status.error)

        else:
            print("[PUBLISH: sent]")
            print("timetoken: %s" % envelope.result.timetoken)
            print("something")

        os.system("echo sent...!!")
