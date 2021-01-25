package com.app.cogu;
public class chat_messages_class {
    private String message;
    private boolean isMe;

    public chat_messages_class(String message, boolean isme){
        this.message = message;
        this.isMe = isme;
    }

    public String getMessage() {
        return message;
    }

    public boolean isMe() {
        return isMe;
    }

}

