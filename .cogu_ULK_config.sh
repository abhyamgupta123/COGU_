#!/bin/bash

# NAME: lightdm-auto-login

main() {
    # If the file '/etc/lightdm/lightdm.conf' exists create a backup copy
    [[ -f /etc/lightdm/lightdm.conf ]] && mv /etc/lightdm/lightdm.conf{,.bak}

    # Create autologin configuration for the current $USER = $1
    echo -e "[Seat:*]\nautologin-user=$1" > /etc/lightdm/lightdm.conf

    # Restart 'lightdm' while autologin option is enabled
    systemctl restart lightdm.service

    # Wait for a moment to complete the login process and remove the conf file
    sleep 30 && rm /etc/lightdm/lightdm.conf

    # Restore the backup if exists
    [[ -f /etc/lightdm/lightdm.conf.bak ]] && mv /etc/lightdm/lightdm.conf{.bak,}
}

# Execute the 'main()' function with root privileges in the background 'sudo -b'
# Pass the curent $USER as arg (https://unix.stackexchange.com/a/269080/201297)
sudo -b bash -c "$(declare -f main); main $USER"
