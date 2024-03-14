package com.advantal.shieldcrypt.utils_pkg

import java.util.regex.Pattern

/**
 * Created by Sonam on 13-07-2022 17:14.
 */
class AppValidation {
    companion object {
        val regexStrForPhoneNum = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{09}\$"


        fun isValidPhoneNumber(countryCode: String, number: String): Boolean {
            var PATTERN: Pattern = Pattern.compile(regexStrForPhoneNum)
            fun CharSequence.isPhoneNumber(): Boolean = PATTERN.matcher(this).find()

            return "$countryCode $number".isPhoneNumber()
        }
    }
}