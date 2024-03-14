package com.advantal.shieldcrypt.meeting.model

data class MeetingDetailsResponse(
	val data: ArrayList<Data>,
	val message: String? = null,
	val status: Boolean? = null
)

data class User(
	val firstName: Any? = null,
	val lastName: Any? = null,
	val id: Int? = null,
	val username: Any? = null
)

data class Data(
	val recurrence: Recurrencemd? = null,
	val meetingforvalue: String? = null,
	val attenders: List<AttendersItem?>? = null,
	val organizer: OrganizerItem? = null,
	//val organizer: List<AttendersItem?>? = null,
	val meetingfor: String? = null,
	val start: String? = null,
	val description: String? = null,
	val end: String? = null,
	val id: Int? = null,
	val title: String? = null,
	val status: Boolean? = null
)

data class AttendersItem(
	val admin: Admin? = null,
	val user: User? = null
)

data class Recurrencemd(
	val name: String? = null,
	val id: Int? = null
)

data class OrganizerItem(
	val firstname: String? = null,
	val id: Int? = null,
	val username: String? = null,
	val lastname: String? = null,
	val type: String? = ""
)

data class Admin(
	val firstName: String? = null,
	val lastName: String? = null,
	val id: Int? = null,
	val username: String? = null
)

