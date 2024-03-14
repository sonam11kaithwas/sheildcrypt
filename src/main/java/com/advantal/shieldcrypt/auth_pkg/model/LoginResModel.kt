package com.advantal.shieldcrypt.auth_pkg.model

import androidx.room.PrimaryKey

/**
 * Created by Sonam on 20-09-2022 11:00.
 */
class LoginResModel {
    var token: String? = ""

    var username: String? = ""
    var firstName: String? = ""
    var lastName: String? = ""
    lateinit var mobileNumber: String
    lateinit var profileUrl: String

    var password: String? = ""
    var designation: String? = ""
    var location: String? = ""
    var unit: String? = ""

    lateinit var userid: String
    var sipport: String? = ""
    var sipip: String? = ""
    var xmpport: String? = ""
    var xmpip: String? = ""
    var webrtcip: String? = ""
    var webrtcport: String? = ""


}