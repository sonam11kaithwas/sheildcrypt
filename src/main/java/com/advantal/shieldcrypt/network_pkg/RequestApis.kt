package com.advantal.shieldcrypt.network_pkg

/**
 * Created by Sonam on 16-09-2022 17:42.
 */
object RequestApis {
    //    companion object {
    const val meetingBaseUrl = "https://92.204.128.15:7443/ofmeet/"
    //const val meetingBaseUrl = "https://192.168.8.78:7443/ofmeet/"
//    const val meetingBaseUrl = "https://157.23.156.141:7443/ofmeet/"
    val forgot_password: String = "shieldcryptUserManagementMobile/user/sendMailForForgetPassword"
    val userLogin: String = "userlogin"
    const val view_Profile_ById = "shieldcryptUserManagementMobile/user/viewProfileById"
    const val updateProfile = "shieldcryptUserManagementMobile/user/updateProfile"
    const val sendOtpUser = "sendOtpUser"
    const val getAllContacts = "shieldcryptUserManagementMobile/user/getAllContacts"
    const val changePassword = "shieldcryptUserManagementMobile/user/changePassword"
    const val passwordPolicy = "shieldcryptPasswordPolicy/passwordPolicy/getAll"
    const val uploadProfilePhoto = "shieldcryptUserManagementMobile/user/updateUserProfilePhoto"
    const val logoutUser = "shieldcryptUserManagementMobile/user/logout"
    const val callLogs = "shieldcryptAsterisk/callLogs/getAllCalllogsMobile"
    const val blocknunlock = "shieldcryptUserManagementMobile/user/blockunblockContact"
    const val default_status_list =
        "shieldcryptUserManagementMobile/user/profilestatus/getAllIndividualStatus"
    const val custon_status_save = "shieldcryptUserManagementMobile/user/profilestatus/save"
    const val my_status_list = "shieldcryptUserManagementMobile/user/mediastatus/getmediastatusbyId"
    const val my_status_list_delete =
        "shieldcryptUserManagementMobile/user/mediastatus/deletemediastatusbyid"
    const val get_all_media_status =
        "shieldcryptUserManagementMobile/user/mediastatus/getallmediastatus"
    const val other_save_status = "shieldcryptUserManagementMobile/user/mediastatus/statusupdate"
    const val pencil_save_status =
        "shieldcryptUserManagementMobile/user/mediastatus/createPencilStatus"
    const val get_all_meeting_list = "shieldcryptUserManagementMobile/user/meetinggetAll"
    const val get_meeting_recur = "shieldcryptUserManagementMobile/user/getAllRecurrence"
    const val get_meeting_unit = "shieldcryptAdminManagement/unit/getAll"
    const val get_meeting_location = "shieldcryptAdminManagement/location/getAll"
    const val get_meeting_designation = "shieldcryptAdminManagement/designation/getAll"
    const val meeting_detail_viewbyId = "shieldcryptUserManagementMobile/user/meetingviewById"
    const val save_meeting = "shieldcryptUserManagementMobile/user/meetingsave"
    const val addUserInMeeting = "shieldcryptUserManagementMobile/user/addMemberInMeeting"
    const val deleteUserInMeeting = "shieldcryptUserManagementMobile/user/removeMemberInMeeting"
    const val updateMeeting = "shieldcryptUserManagementMobile/user/meetingupdate"
    const val deleteMeeting = "shieldcryptUserManagementMobile/user/meetingdelete"

    const val get_meeting_attender_query_autotext =
        "shieldcryptGroupManagement/groupmanagement/searchByUserName"
    const val delete_profile =
        "shieldcryptUserManagementMobile/user/profilestatus/deleteProfilePhoto"
    const val addUserInGroup = "shieldcryptUserManagementMobile/user/saveGroupmanagement"
    const val updategroup = "shieldcryptUserManagementMobile/user/updateGroup"
    const val getAllGroupmanagement = "shieldcryptUserManagementMobile/user/getAllGroupmanagement"
    const val searchByUserId = "shieldcryptUserManagementMobile/user/searchByUserId"
    const val deleteGroupById = "shieldcryptUserManagementMobile/user/deleteGroupManagement"
    const val deleteGroupUser = "shieldcryptUserManagementMobile/user/removeUserFromGroup"
    const val addGroupuser = "shieldcryptUserManagementMobile/user/addUserInGroup"
    const val get_all_media_status_list =
        "shieldcryptUserManagementMobile/user/mediastatus/getAllmediastatusbyId"
    const val updateUserProfilePhoto = "shieldcryptUserManagementMobile/user/updateUserProfilePhoto"
    const val updateGroupImage = "shieldcryptUserManagementMobile/user/updateGroupImage"
    const val groupIconRemove = "shieldcryptUserManagementMobile/user/groupIconRemove"
    const val groupviewById = "shieldcryptUserManagementMobile/user/groupviewById"

    const val CHANGE_PASSWORD_REQ = 1
    const val FORGOT_PASSWORD_REQ = 2
    const val USER_LOGIN_REQ = 3
    const val UPDATE_PROFILE_REQ = 4
    const val SENT_OTP_USERD_REQ = 5
    const val PASSWORD_POLICY_REQ = 6
    const val VIEW_PROFILE_BY_ID_REQ = 7
    const val ALL_CONTACT = 8
    const val UPLOAD_PROFILE_PHOTO = 9
    const val VERIFY_OTP = 11
    const val LOGOUT_REQ = 10
    const val CALL_LOGS_APIS = 12
    const val BLOCK_N_UBLOCK = 13
    const val DEFAULT_STATUS_LIST = 14
    const val CUSTOM_STATUS_SAVE = 15
    const val MY_STATUS_LIST = 16
    const val MY_STATUS_LISTDELETE = 17
    const val GET_ALL_MEDIA_STATUS = 18
    const val OTHER_SAVE_STATUS = 19
    const val PENCIL_SAVE_STATUS = 20
    const val GET_ALL_MEETING_LIST = 21
    const val MEETING_DETAILS_VIEWBYID = 22
    const val GET_MEETING_RECUR = 23
    const val GET_MEETING_UNIT = 24
    const val GET_MEETING_LOCATION = 25
    const val GET_MEETING_DESIGNATION = 26
    const val GET_MEETING_ATTENDER_QUERY_AUTO_TEXT = 27
    const val SAVE_MEETING = 28
    const val DELETE_PROFILE = 29
    const val GET_USER_GROUP = 17
    const val ADD_USER_GROUP = 16

    const val MY_STATUS_LIST_All = 17
    const val DELETE_GRP = 30
    const val ADD_USER_IN_MEETING = 34
    const val DELETE_USER_IN_MEETING = 31
    const val UPDATE_MEETING = 32
    const val DELETE_MEETING = 33
    const val DELETE_GRP_USER = 36
    const val ADD_GRP_USER = 37
    const val EDIT_USER_GROUP = 38
    const val VIEW_GRP_USER = 39

    //Xmpp ip
    //Xmpp port
    //Sip ip
    //Sip port
    //Webrtc ip
    //Webrtc port
//    }
}