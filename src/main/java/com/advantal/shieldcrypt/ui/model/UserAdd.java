package com.advantal.shieldcrypt.ui.model;

import java.io.Serializable;

public class UserAdd implements Serializable {
    private Integer userId;
    private boolean admin=false;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public UserAdd(int userId, boolean admin) {
        this.userId = userId;
        this.admin = admin;
    }
}
