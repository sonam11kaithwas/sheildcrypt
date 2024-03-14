package com.advantal.shieldcrypt.sip.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "tb_ithub_contact")
public class IthubContactsModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @NotNull
    private int id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "mobileNumber")
    private String mobileNumber;

    @ColumnInfo(name = "firstName")
    private String firstName;

    @ColumnInfo(name = "lastName")
    private String lastName;

    @ColumnInfo(name = "emailId")
    private String emailId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "isBlocked")
    private String isBlocked;

    @ColumnInfo(name = "displayName")
    private String displayName;

    @ColumnInfo(name = "presenceStatus")
    private String presenceStatus;

    @ColumnInfo(name = "presenceLastSeen")
    private String presenceLastSeen;

    @NotNull
    public int getId() {
        return id;
    }

    public void setId(@NotNull int id) {
        this.id = id;
    }

    public IthubContactsModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getPresenceStatus() {      // 0 offline 1 away 2 online
        return presenceStatus;
    }

    public void setPresenceStatus(String presenceStatus) {
        this.presenceStatus = presenceStatus;
    }

    public String getPresenceLastSeen() {
        return presenceLastSeen;
    }

    public void setPresenceLastSeen(String presenceLastSeen) {
        this.presenceLastSeen = presenceLastSeen;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
