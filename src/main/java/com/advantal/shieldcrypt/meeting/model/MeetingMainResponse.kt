package com.advantal.shieldcrypt.meeting.model

data class MeetingMainResponse(
	val data: ArrayList<DataItem>,
	val message: String? = null,
	val status: Boolean? = null
)

data class DataItem(
	val recurrence: Recurrence? = null,
	val start: String? = null,
	val description: String? = null,
	val end: String? = null,
	val id: Int? = null,
	val title: String? = null,
	val meetingUrl: Any? = null,
	val status: Boolean? = null,
	val roomName: String? = null
)

data class Recurrence(
	val name: String? = null,
	val id: Int? = null
)

