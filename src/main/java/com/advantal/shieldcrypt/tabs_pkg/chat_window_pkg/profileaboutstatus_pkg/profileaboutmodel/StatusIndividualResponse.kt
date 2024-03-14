package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel

data class StatusIndividualResponse(
	val data: Data? = null,
	val message: String? = null,
	val status: Boolean? = null
)

data class User(
	val googleId: String? = null,
	val lastName: String? = null,
	val callRecordingStatus: Boolean? = null,
	val passwordStatus: Int? = null,
	val mobileNumber: String? = null,
	val callForwardingStatus: Boolean? = null,
	val admin: Boolean? = null,
	val emailId: String? = null,
	val profilePhoto: String? = null,
	val blocked: Boolean? = null,
	val updationDate: Long? = null,
	val id: Int? = null,
	val verificationStatus: Boolean? = null,
	val otpEnable: Boolean? = null,
	val facebookId: String? = null,
	val active: Boolean? = null,
	val otp: String? = null,
	val creationDate: Long? = null,
	val virtualNumber: String? = null,
	val firstName: String? = null,
	val dndStatus: Boolean? = null,
	val unit: Unit? = null,
	val deleted: Boolean? = null,
	val location: Location? = null,
	val designation: Designation? = null,
	val username: String? = null
)

data class DefaultstatusItem(
	val updationDate: String? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null,
	val status: Boolean? = null
)

data class Dataold(
	val invList: ArrayList<InvListItem>,
	val defaultstatus: ArrayList<InvListItem>
)

data class Location(
	val code: String? = null,
	val updationDate: String? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null
)

data class InvListItemold(
	val updationDate: Any? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null,
	val user: Any? = null,
	val status: Boolean? = null,
	var check: Boolean= false

)

data class Unit(
	val code: String? = null,
	val updationDate: String? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null
)

data class Designation(
	val code: String? = null,
	val updationDate: String? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null
)

