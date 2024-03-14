package com.advantal.shieldcrypt.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AddNewGrpBean implements Serializable {
    private String groupName;
    private int id;
    private int groupId;
    private String groupDescription;
    private ArrayList<UserAdd> users = new ArrayList<>();

    public AddNewGrpBean(int id) {
        this.id = id;
    }

    public AddNewGrpBean(String groupName, String groupDescription, ArrayList<UserAdd> users) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.users = users;
    }

    public AddNewGrpBean(int groupId, ArrayList<UserAdd> users) {
        this.groupId = groupId;
        this.users = users;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public ArrayList<UserAdd> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserAdd> users) {
        this.users = users;
    }


    public AddNewGrpBean(String groupName, int groupId, String groupDescription, ArrayList<UserAdd> users) {
        this.groupName = groupName;
        this.id = groupId;
        this.groupDescription = groupDescription;
        this.users = users;
    }


}
