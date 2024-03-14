package com.advantal.shieldcrypt.sip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SendMessageResponseBean implements Serializable {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private SendMessageObjectBean sendMessageObjectBean;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SendMessageObjectBean getSendMessageObjectBean() {
        return sendMessageObjectBean;
    }

    public void setSendMessageObjectBean(SendMessageObjectBean sendMessageObjectBean) {
        this.sendMessageObjectBean = sendMessageObjectBean;
    }
}
