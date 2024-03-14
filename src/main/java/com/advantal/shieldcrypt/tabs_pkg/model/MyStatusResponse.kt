package com.advantal.shieldcrypt.tabs_pkg.model

data class MyStatusResponse(
	val mediadetails: String? = null,
	val creationDate: String? = null,
	val count: Int? = null,
	val mediastatusid: Int? = null
)

data class DataItem(
	val mediaurl: String? = null,
	val username: String? = null,
	val mediastatusid: String? = null,
	val mediadatetime: String? = null,
	val colorcode: String? = null,
	val fontcode: String? = null,
	val penciltext: String? = null
)

