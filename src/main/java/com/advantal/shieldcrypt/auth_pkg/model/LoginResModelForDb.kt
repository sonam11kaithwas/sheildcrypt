package com.advantal.shieldcrypt.auth_pkg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_login_table")
data class LoginResModelForDb(
    var token: String,

    var username: String,
    var firstName: String,
    var lastName: String,
    var mobileNumber: String,
    var profileUrl: String,

    var password: String,
    var designation: String,
    var location: String,
    var unit: String,
    @PrimaryKey var userid: String,
    var sipport: String,
    var sipip: String,
    var xmpport: String,
    var xmpip: String,
    var webrtcip: String,
    var webrtcport: String,
) : java.io.Serializable