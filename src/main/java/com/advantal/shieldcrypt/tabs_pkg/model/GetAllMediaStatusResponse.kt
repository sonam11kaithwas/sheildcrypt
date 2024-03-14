package com.advantal.shieldcrypt.tabs_pkg.model

class GetAllMediaStatusResponse():java.io.Serializable{
	var userid: Int? = null
	var username: String? = null
	var data: ArrayList<DataItemMediaStatus>?=null
	var userStoryList: ArrayList<DataItemMediaStatus>?=null
}

data class DataItemMediaStatus(
	var statuscreationdatetime: String? = null,
	var statusid: Int? = null,
	var mediastatusdetails: String? = null,
	var userid: Int? = null,
	var username: String? = null,
	var colorcode: String? = null,
	var fontstylecode: String? = null,
	var penciltext: String? = null,
	var viewstatus:Boolean?=null
):java.io.Serializable

