#!/bin/bash
if [ -e /etc/systemd/system/cogu.service ];then
  sudo rm /etc/systemd/system/cogu.service
fi

if [ -e /usr/local/bin/cogu_sub_algo.py ];then
  sudo rm /usr/local/bin/cogu_sub_algo.py
fi

if [ -e ./cogu_pub_runable.py ];then
  sudo rm ./cogu_pub_runable.py
fi

if [ -e /etc/rsyslog.d/cogu.conf ];then
  sudo rm /etc/rsyslog.d/cogu.conf
fi

if [ -e /var/local/cogu.log ];then
  sudo rm /var/local/cogu.log
fi

if [ -e /var/local/cogu_logging.log ];then
  sudo rm /var/local/cogu_logging.log
fi

if [ -e /cogu_logging.log ];then
  sudo rm /cogu_logging.log
fi

if [ -e /usr/local/bin/cogu_alert.sh ];then
  sudo rm /usr/local/bin/cogu_alert.sh
fi

if [ -e ./ANDROID_APP ];then
  sudo rm -r ./ANDROID_APP
fi

if [ -e /usr/local/bin/cogu_culprit_webcam.sh ];then
  sudo rm /usr/local/bin/cogu_culprit_webcam.sh
fi

if [ -e /usr/local/cogu_photos ];then
  sudo rm -rf /usr/local/cogu_photos
fi

if [ -e /etc/pam.d/.cogu_ULK_config.sh ];then
  sudo rm -rf /etc/pam.d/.cogu_ULK_config.sh
fi


#FINIALISING:--
sudo systemctl daemon-reload
sudo systemctl restart rsyslog.service
sudo systemctl stop cogu.service
echo "
"
echo "COGU REMOVED SUCESSFULLY...!!
"
