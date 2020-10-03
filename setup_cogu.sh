#!/bin/bash
name=$(whoami)
source ./keys_data.txt
echo " "
systemctl --user import-environment
disp=$(systemctl --user show-environment | grep DISPLAY)
auth=$(systemctl --user show-environment | grep XAUTHORITY)
cogu_var_home_setup="HOME=/home/$name"
cogu_var_user_setup="USER=$name"
echo "Environment Variables needed to be used:"
echo "  $disp"
echo "  $auth"
echo "  $cogu_var_home_setup"
echo "  $cogu_var_user_setup"
echo " "
echo "Make Sure these Python-libraries or Modules are Instaled in system in case of any error...!!"
echo "    1. os
    2.platform
    3.psutil
    4.uuid
    5.re
    6.socket
    7.subprocess
    8.fnmatch
    9.alsaaudio
    10.logging
    11.pubnub
    12.requests"

echo "
"
echo " => Updating your system..!!
"
sudo apt-get -y update
echo " "
read -p "Do you have pip2 and pip3 in your system ? (if not sure then press 'n') [y/n] " var_pip
if [ $var_pip == "n" ];then
  sudo apt-get -y install python-pip
  sudo apt-get -y install python3-pip
fi
echo " "
echo " => Installing Alsa-audio python library : "
echo " "
sudo apt-get -y install python-alsaaudio
echo " "
echo " => Installing Sed...!!"
echo " "
sudo apt-get -y install sed
echo " "
echo " => Installing xdotool...!!"
echo " "
sudo apt-get install xdotool
echo " "
echo " => Installing jq...!! "
echo " "
sudo apt-get install jq
echo ""
echo " => Installing fswebcam...!! "
echo " "
sudo apt-get install fswebcam
echo " "
echo " => Installing PUBNUB Python Library : "
echo " "
echo " 1-> Through pip2 "
echo " "
pip2 install pubnub
echo " "
echo " 2-> Through pip3 "
echo " "
pip3 install pubnub
echo " "
echo "Installing other required libraries [psutil, uuid, sockets, requests]"
echo " "
echo " 1-> Through pip2 "
echo " "
pip2 install psutil
pip2 install uuid
pip2 install sockets
pip2 install requests==2.7.0
# pip2 install opencv-python
echo " "
echo " 2-> Through pip3 "
echo " "
pip3 install psutil
pip3 install uuid
pip3 install sockets
pip3 install requests==2.7.0
# pip3 install opencv-python
echo "
"

# SETUP SERVICE FILE:--
sed -e "s~DISP~$disp~" -e "s~x_auth~$auth~" -e "s~cogu_var_user~$cogu_var_user_setup~" -e "s~cogu_var_home~$cogu_var_home_setup~" ./cogu.service > ./.cogu.service
sudo cp ./.cogu.service /etc/systemd/system/.
sudo mv /etc/systemd/system/.cogu.service /etc/systemd/system/cogu.service
sudo chmod 644 /etc/systemd/system/cogu.service
sudo rm ./.cogu.service


# SETUP SUBSRIPTION PYTHON FILE:--
sed -e "s~ENTER_YOUR_PUBLISH_KEY~$pubnub_pub_key~" -e "s~ENTER_YOUR_SUBSCRIBE_KEY~$pubnub_sub_key~" -e "s~ENTER_YOUR_CHANNEL_NAME~$pubnub_channel~" -e "s~ENTER_YOUR_DEVICE_NAME~$pubnub_device_name~" ./cogu_sub_algo.py > ./.cogu_sub_algo.py
sudo cp ./.cogu_sub_algo.py /usr/local/bin/.
sudo mv /usr/local/bin/.cogu_sub_algo.py /usr/local/bin/cogu_sub_algo.py
sudo rm ./.cogu_sub_algo.py


# SETUP PUBLISHER PYTHON FILE:--
sed -e "s~ENTER_YOUR_PUBLISH_KEY~$pubnub_pub_key~" -e "s~ENTER_YOUR_SUBSCRIBE_KEY~$pubnub_sub_key~" -e "s~ENTER_YOUR_CHANNEL_NAME~$pubnub_channel~" -e "s~ENTER_YOUR_DEVICE_NAME~"$pubnub_device_name"_"debug"~" ./cogu_pub.py > ./.cogu_pub_runable.py


# Setting for super unlocking:--
sudo cp ./.cogu_ULK_config.sh /etc/pam.d/.
sudo chmod u=rw,g=,o= /etc/pam.d/.cogu_ULK_config.sh
sudo chmod u=rw,g=,o= ./.cogu_ULK_config.sh
sudo chgrp root /etc/pam.d/.cogu_ULK_config.sh
sudo chgrp root ./.cogu_ULK_config.sh
sudo chown root /etc/pam.d/.cogu_ULK_config.sh
sudo chown root ./.cogu_ULK_config.sh


# Setting-up Culprit image capture configurations:--
sudo mkdir -p /usr/local/cogu_photos/cogu_culprit_photos
sudo mkdir -p /usr/local/cogu_photos/cogu_screenshots
sudo chmod -R 777 /usr/local/cogu_photos


# CONFIGURING RSYSLOG SERVICE TO LOG FILE INFO IN EXTERNAL PATH:--
sudo cp ./cogu.conf /etc/rsyslog.d/.


# SETTING UP SHELL FILE TO START SERVICE AFTER EACH TIME CONNECTED TO WIFI NETWORK:--
sudo cp ./cogu_service_starter.sh /etc/network/if-up.d/.
sudo chmod +x /etc/network/if-up.d/cogu_service_starter.sh


# CONFIGURING LOG FILES:--
sudo touch /var/local/cogu.log
sudo touch /var/local/cogu_logging.log
sudo chmod 777 /var/local/cogu_logging.log
sudo chown syslog:adm /var/local/cogu.log
sudo chmod 777 /var/local/cogu.log


#Setting for automatically connecting to wifi network after reboot or freshly starting system:--
echo -e "\e[1;33m  [RECOMMEND]  \e[0m"
echo -e "\e[1;33mThis option provides cogu utility to connect to available wifi connections so that it can start working just after \e[0m"
echo -e "\e[1;33mrebooting or freshly starting the system. This system is not configured to connect to wifi just after rebooting, \e[0m"
echo -e "\e[1;33mas you need to login at first to connect to available wifi networks, Thus enabling this option makes COGU to connect  \e[0m"
echo -e "\e[1;33mautomatically thus providing more powerful and immediate access to your computer by COGU. \e[0m"
echo " "
echo -e "\e[1;34m [Doing this only enables this feature for exsisting wifi networks you are currently logged in until now] \e[0m"
echo " "
read -p "Do you want to enable your system to connect to avaiable wifi networks just after reebooting (Disabled by default) ? [y/n] " var_wifi_connect
if [ $var_wifi_connect == "y" ];then
  sudo sed -i 's~^permissions.*;$~permissions=~' /etc/NetworkManager/system-connections/*
  echo " "
else
  echo " "
fi


#FINIALISING EVERYTHING:--
sudo systemctl daemon-reload
sudo systemctl restart rsyslog.service
sudo systemctl restart cogu.service
sudo systemctl enable cogu.service
echo "


"
echo "                           _________________          __________________                            __________________        ___                ___            "
echo "                          /  _______________|        /  ______________  \                          /  ______________  \      |  |               |  |            "
echo "                         /  /                       /  /              \  \                        /  /              \  \     |  |               |  |            "
echo "                        /  /                       /  /                \  \                      /  /                \__\    |  |               |  |            "
echo "                       /  /                       /  /                  \  \                    /  /                         |  |               |  |            "
echo "                      /  /                       /  /                    \  \                  /  /                          |  |               |  |            "
echo "                     /  /                       /  /                      \  \    ________    /  /         ______________    |  |               |  |            "
echo "                     \  \                       \  \                      /  /   |________|   \  \        |___________   /   |  |               |  |            "
echo "                      \  \                       \  \                    /  /                  \  \                  /  /    |  |               |  |            "
echo "                       \  \                       \  \                  /  /                    \  \                /  /     |  \               /  /            "
echo "                        \  \                       \  \                /  /                      \  \              /  /       \  \             /  /             "
echo "                         \  \_______________        \  \______________/  /                        \  \____________/  /         \  \___________/  /              "
echo "                          \_________________|        \__________________/                          \________________/           \_______________/               "
echo " "
echo " "
echo " "
echo "Installed and Configured Succesfully..!!"
echo " "
echo " "
