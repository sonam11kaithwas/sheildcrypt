package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.profilemodel

/**
 * Created by Sonam on 20-09-2022 13:47.
 */
data class ProfileReqModel(var id: Int) {
    constructor(id: Int, firstName: String, lastName: String) : this(id)
}