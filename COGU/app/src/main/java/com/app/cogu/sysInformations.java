package com.app.cogu;

public class sysInformations {
    private String heading;
    private String data;

    public sysInformations(String head, String data) {
        this.heading = head;
        this.data = data;
    }

    public String getName() {
        return heading;
    }

    public String getData() {
        return data;
    }
}
