package com.advantal.shieldcrypt.auth_pkg.model

data class SendOtpResponse(
	val countstatus: Int? = null,
	val httpstatus: String? = null,
	val data: Data? = null,
	val message: String? = null,
	val status: Boolean? = null
)

data class Data(
	val messageotp: Any? = null
)

