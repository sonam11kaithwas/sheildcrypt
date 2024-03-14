package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel

data class ProfileAboutResponse(
	val countstatus: Int? = null,
	val httpstatus: String? = null,
	val data: Data? = null,
	val message: String? = null,
	val status: Boolean? = null
)

data class Defaultstatus(
	val name: String? = null,
	val id: Int? = null,
	val isdefault: Boolean? = null,
	val creationDate: String? = null
)


data class Data(
	val invList: List<InvListItem>? = null
)

data class InvListItem(
	val updationDate: Any? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null,
	val defaultstatus: Defaultstatus? = null,
	var check: Boolean= false
)

