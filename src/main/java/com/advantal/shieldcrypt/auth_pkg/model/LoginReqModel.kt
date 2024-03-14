package com.advantal.shieldcrypt.auth_pkg.model

/**
 * Created by Sonam on 15-09-2022 11:47.
 */
class LoginReqModel(var username: String, var password: String) {
    constructor(username: String) : this(username, "")
}