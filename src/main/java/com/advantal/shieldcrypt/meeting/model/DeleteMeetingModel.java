package com.advantal.shieldcrypt.meeting.model;

public class DeleteMeetingModel {
    private MeetingOrganizerModel organizer;
    private int id;
    private int userid;

    public MeetingOrganizerModel getOrganizer() {
        return organizer;
    }

    public void setOrganizer(MeetingOrganizerModel organizer) {
        this.organizer = organizer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
