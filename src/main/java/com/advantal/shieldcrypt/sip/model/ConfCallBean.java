package com.advantal.shieldcrypt.sip.model;

import java.io.Serializable;

public class ConfCallBean implements Serializable {
    public int callId;
    public String number;
    public String name;
    public String callStatus;

    public ConfCallBean(int callId, String number, String name, String callStatus) {
        this.callId = callId;
        this.number = number;
        this.name = name;
        this.callStatus = callStatus;
    }

    public ConfCallBean() {
    }

    public int getCallId() {
        return callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }
}
