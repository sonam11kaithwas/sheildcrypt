package com.advantal.shieldcrypt.meeting.model;

import java.util.ArrayList;

public class SaveMeetingModel {
    private String title;
    private String description;
    private String start;
    private String end;
    private Boolean status;
    private Boolean allUserSelected;
    private String meetingFor;
    private String recurrenceid;
    private String meetingForValue;
    private ArrayList<SaveMeetingAttender> attenders;
    private MeetingOrganizerModel organizer;
    private int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getAllUserSelected() {
        return allUserSelected;
    }

    public void setAllUserSelected(Boolean allUserSelected) {
        this.allUserSelected = allUserSelected;
    }

    public String getMeetingFor() {
        return meetingFor;
    }

    public void setMeetingFor(String meetingFor) {
        this.meetingFor = meetingFor;
    }

    public String getRecurrence_id() {
        return recurrenceid;
    }

    public void setRecurrence_id(String recurrenceid) {
        this.recurrenceid = recurrenceid;
    }

    public String getMeetingForValue() {
        return meetingForValue;
    }

    public void setMeetingForValue(String meetingForValue) {
        this.meetingForValue = meetingForValue;
    }

    public ArrayList<SaveMeetingAttender> getAttenders() {
        return attenders;
    }

    public void setAttenders(ArrayList<SaveMeetingAttender> attenders) {
        this.attenders = attenders;
    }

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

}
