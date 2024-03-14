package com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel

data class CallLogsResponse(
	/*val number: Int? = null,
	val last: Boolean? = null,
	val size: Int? = null,
	val numberOfElements: Int? = null,
	val totalPages: Int? = null,
	val pageable: Pageable? = null,
	val sort: Sort? = null,*/
	val clmp: List<ContentItem>? = null,
	/*val first: Boolean? = null,
	val totalElements: Int? = null,
	val empty: Boolean? = null*/
)

data class ContentItem(
	val duration: Int? = null,
	val calldate: String? = null,
	val type: String? = null,
	//val dst: String? = null,
	val src: String? = null,
	//val cnam: String? = null,
	val src_cnam: String? = null,
//	val calldate: String? = null
)

data class Pageable(
	val paged: Boolean? = null,
	val pageNumber: Int? = null,
	val offset: Int? = null,
	val pageSize: Int? = null,
	val unpaged: Boolean? = null,
	val sort: Sort? = null
)

data class Sort(
	val unsorted: Boolean? = null,
	val sorted: Boolean? = null,
	val empty: Boolean? = null
)

