#!/usr/bin/python2
'''
You can use this file to debug or test your pubnub service.
'''

from pubnub.callbacks import SubscribeCallback
from pubnub.enums import PNStatusCategory
from pubnub.pnconfiguration import PNConfiguration
from pubnub.pubnub import PubNub
from time import sleep

DEVICE_NAME = "ENTER_YOUR_DEVICE_NAME"                                           # Unique Device name
CHANNEL = "ENTER_YOUR_CHANNEL_NAME"                                              # Channel name
the_update = None

pnconfig = PNConfiguration()
pnconfig.publish_key = "ENTER_YOUR_PUBLISH_KEY"                                  # PubNub's Publish key:-
pnconfig.subscribe_key = "ENTER_YOUR_SUBSCRIBE_KEY"                              # PubNub's Subscribe key
pnconfig.uuid = DEVICE_NAME                                                      # Unique identifier name over channel

pubnub = PubNub(pnconfig)

while the_update != "DISSMISS":
    print
    the_update = raw_input("Enter an command for BONY: ")
    the_message = {"comm": the_update, "DEVICE_NAME": DEVICE_NAME}
    envelope = pubnub.publish().channel(CHANNEL).message(the_message).sync()

    if envelope.status.is_error():
        print("[PUBLISH: fail]")
        print("error: %s" % status.error)
        pass
    else:
        print("[PUBLISH: sent]")
        print("timetoken: %s" % envelope.result.timetoken)
        pass
