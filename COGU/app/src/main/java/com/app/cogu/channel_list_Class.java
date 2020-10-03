package com.app.cogu;

public class channel_list_Class {
    private String channelName;

    public channel_list_Class(){
        // empty cuonstructor needed for firebse.
    }

    public channel_list_Class(String channel_name){
        this.channelName = channel_name;
    }

    public String getChannelName() {
        return channelName;
    }
}
