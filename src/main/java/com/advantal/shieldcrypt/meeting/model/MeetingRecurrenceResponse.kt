package com.advantal.shieldcrypt.meeting.model

data class MeetingRecurrenceResponse(
	val data: List<DataItemM?>? = null,
	val message: String? = null,
	val status: Boolean? = null
)

data class DataItemM(
	val name: String? = null,
	val id: Int? = null
)

