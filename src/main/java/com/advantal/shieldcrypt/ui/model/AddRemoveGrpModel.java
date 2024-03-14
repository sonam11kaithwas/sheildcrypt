package com.advantal.shieldcrypt.ui.model;

import java.util.ArrayList;

public class AddRemoveGrpModel {
    private int groupId;
    private ArrayList<UsersModel> users=new ArrayList<>();

    public AddRemoveGrpModel(int groupId, ArrayList<UsersModel> users) {
        this.groupId = groupId;
        this.users = users;
    }
}

