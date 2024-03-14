package com.advantal.shieldcrypt.utils_pkg

import java.util.regex.Pattern

/**
 * Created by Sonam on 23-06-2022 18:44.
 */

class AppConstants {
    companion object {

        var runContactSyncing=false

        const val RUNTIME_PERMISSION_REQUEST_CODE = 5000
        const val RUNTIME_PERMISSION_REQUEST_CODE_DOCUMENT = 1000
        const val RUNTIME_PERMISSION_REQUEST_CODE_CAMERA = 1001
        const val RUNTIME_PERMISSION_REQUEST_CODE_GALLERY = 1002
        const val RUNTIME_PERMISSION_REQUEST_CODE_AUDIO = 1003
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 1004
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1005
        const val PERMISSION_REQUEST_CODE = 1006
        const val uploaded = 2
        const val STATUS_IMAGE = "image"
        const val STATUS_VIDEO = "video"
        const val SEPERATOR = "##:<A>O<B>:##"


        const val CACHE_LIMIT = 100
        const val PHONEERRORMSG = "Please enter valid phone number."
        const val NAMEERRORMSG = "Please enter user name"
        const val EMAILERRORMSG = "Please enter mail id"
        const val PLEASE_ENTER_VALID_EMAIL_ID = "Please enter valid mail id"
        const val PASSERRORMSG = "Please enter password"
        const val NAMEPASSERRORMSG = "Please enter user name and password"
        const val ARE_YOU_SURE_YOU_WANT_LOGOUT = "Are you sure you want to logout?"
        const val ARE_YOU_SURE_YOU_WANT_BLOCK = "Are you sure you want to block this User?"
        const val ARE_YOU_SURE_YOU_WANT_UNBLOCK = "Are you sure you want to Unblock this User?"


        const val successResponse = "1"
        const val failResponse = "0"
        const val primaryMobile = "primaryMobile"
        const val primaryCountryCode = "primaryCountryCode"
        const val callStartTimeStamp = "callStartTimeStamp"
        const val notificationIdForCall = "notificationIdForCall"
        const val isAppForeground = "isAppForeground"
        const val headsUpNotificationId = "headsUpNotificationId"
        // Basic authentication UserName and password
        const val basicAuthUsername = "admin"
        const val basicAuthPassword = "password"
        const val isAppKilled = "isAppKilled"
        const val trueValue = "true"
        const val sipIpDynamic = "sipIpDynamic"
        const val sipPortDynamic = "sipPortDynamic"
        const val loggedInUserNumber = "loggedInUserNumber"
        const val userName = "userName"
        const val userPassword = "userPassword"
        const val ithubContactIntentTag = "ithubContactIntentTag"

        /******XAMPP Server*********/
        const val xmppWorkManagerTag = "12345"
        const val CODE_TEXT = 1
        const val INDIVIDUAL = 1
        const val xepMessageSent = 1
        const val xepMessageNotSent = 0


        /******Message Type Code*********/
        const val GROUP_CODE_TEXT = 51
        const val CODE_IMAGE = 101
        const val GROUP_CODE_IMAGE = 151
        const val CODE_VIDEO = 201
        const val GROUP_CODE_VIDEO = 251
        const val CODE_AUDIO = 301
        const val GROUP_CODE_AUDIO = 351
        const val CODE_LOCATION = 401
        const val GROUP_CODE_LOCATION = 451
        const val CODE_DOCUMENT = 501
        const val GROUP_CODE_DOCUMENT = 551
        const val CODE_CONTACT = 601
        const val CODE_STICKER = 701
        const val GROUP_CODE_STICKER = 751
        const val GROUP_CODE_CONTACT = 651
        const val GROUP_CODE_CREATE_GROUP = 2050
        var TYPE = ""
        const val falseValue = "false"
        const val autoLogin = "autoLogin"
        const val xepMessageDelivered = 2
        const val GROUP = 2
        const val  CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        var MYOTOP = "123"
        const val  atTheRateBroadcast = "@broadcast."
        const val SEPERATOR_GROUP_JID = "#::#"
        var MYPHONENO = "124"
        var MYPROFILESTATUS = "126"
        var MYSTATUS = "127"

//        const val GROUP_CODE_CONTACT = 651

        val EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )

        fun isValidString(str: String): Boolean{
            return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
        }
    }

}