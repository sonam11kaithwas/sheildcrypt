package com.advantal.shieldcrypt.sip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SendMessageObjectBean implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("user")
    @Expose
    private SendMessageUserObjectBean sendMessageUserObjectBean;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("smsDate")
    @Expose
    private long smsDate;
    @SerializedName("type")
    @Expose
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SendMessageUserObjectBean getSendMessageUserObjectBean() {
        return sendMessageUserObjectBean;
    }

    public void setSendMessageUserObjectBean(SendMessageUserObjectBean sendMessageUserObjectBean) {
        this.sendMessageUserObjectBean = sendMessageUserObjectBean;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSmsDate() {
        return smsDate;
    }

    public void setSmsDate(long smsDate) {
        this.smsDate = smsDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
