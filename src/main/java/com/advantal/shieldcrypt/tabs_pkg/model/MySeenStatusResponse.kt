package com.advantal.shieldcrypt.tabs_pkg.model

data class MySeenStatusResponse(
	val data: ArrayList<DataItemStatus>,
	val message: String? = null,
	val status: Boolean? = null
)

data class DataItemStatus(
	val seentime: String? = null,
	val contactName: String? = null,
	val mediadetails: String? = null
)

