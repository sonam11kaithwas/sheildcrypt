package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.change_pass_pkg.change_pass_model

class PasswordPolicyResModel(var id: Int,
                             var passwordFor: Int,
                             var passwordDuration: Int,
                             var validTill: String,
                             var creationDate: String,
                             var updationDate: String,
                             var minPasswordLength: Int,
                             var maxPasswordLength: Int,
                             var upperCaseletters: Int,
                             var lowerCaseletters: Int,
                             var number: Int,
                             var specialCharacters: Int,
                             var lastPasswordCheck: Boolean,
                             var whiteSpaceCheck: Boolean) {
}