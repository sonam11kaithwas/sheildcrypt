package com.advantal.shieldcrypt.meeting.model;

public class SaveMeetingAttender {
    private int attenderid;
    private Boolean isAdmin;
    private int attendertype;

    public int getAttender_id() {
        return attenderid;
    }

    public void setAttender_id(int attenderid) {
        this.attenderid = attenderid;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public int getAttender_type() {
        return attendertype;
    }

    public void setAttender_type(int attendertype) {
        this.attendertype = attendertype;
    }
}
