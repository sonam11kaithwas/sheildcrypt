package com.advantal.shieldcrypt.sip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SendMessageUserObjectBean implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("lastName")
    @Expose
    private String lastName;
    @SerializedName("creationDate")
    @Expose
    private Object creationDate;
    @SerializedName("updationDate")
    @Expose
    private Object updationDate;
    @SerializedName("active")
    @Expose
    private boolean active;
    @SerializedName("emailId")
    @Expose
    private String emailId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("facebookId")
    @Expose
    private String facebookId;
    @SerializedName("googleId")
    @Expose
    private String googleId;
    @SerializedName("dndStatus")
    @Expose
    private boolean dndStatus;
    @SerializedName("verificationStatus")
    @Expose
    private boolean verificationStatus;
    @SerializedName("callForwardingStatus")
    @Expose
    private boolean callForwardingStatus;
    @SerializedName("callRecordingStatus")
    @Expose
    private boolean callRecordingStatus;
    @SerializedName("virtualNumber")
    @Expose
    private String virtualNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Object getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Object creationDate) {
        this.creationDate = creationDate;
    }

    public Object getUpdationDate() {
        return updationDate;
    }

    public void setUpdationDate(Object updationDate) {
        this.updationDate = updationDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public boolean isDndStatus() {
        return dndStatus;
    }

    public void setDndStatus(boolean dndStatus) {
        this.dndStatus = dndStatus;
    }

    public boolean isVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public boolean isCallForwardingStatus() {
        return callForwardingStatus;
    }

    public void setCallForwardingStatus(boolean callForwardingStatus) {
        this.callForwardingStatus = callForwardingStatus;
    }

    public boolean isCallRecordingStatus() {
        return callRecordingStatus;
    }

    public void setCallRecordingStatus(boolean callRecordingStatus) {
        this.callRecordingStatus = callRecordingStatus;
    }

    public String getVirtualNumber() {
        return virtualNumber;
    }

    public void setVirtualNumber(String virtualNumber) {
        this.virtualNumber = virtualNumber;
    }
}
