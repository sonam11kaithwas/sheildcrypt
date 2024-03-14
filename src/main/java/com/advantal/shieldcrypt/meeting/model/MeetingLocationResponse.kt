package com.advantal.shieldcrypt.meeting.model

data class MeetingLocationResponse(
	val data: List<DataItemL?>? = null,
	val message: String? = null,
	val status: Boolean? = null
)

data class DataItemL(
	val username: String? = null,
	val firstName: String? = null,
	val lastName: String? = null,
	val code: String? = null,
	val updationDate: Any? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val creationDate: String? = null
)

