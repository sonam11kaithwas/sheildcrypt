package com.advantal.shieldcrypt.tabs_pkg.model

import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.util.MiMediaType

class UserDataModel {
    var id: String? = null
    var userName: String? = null
    var userStoryList: ArrayList<StoryDataModel>? = null
    var lastStoryPointIndex: Int = 0

}

class StoryDataModel {
    var mediaUrl: String? = null
    var name: String? = null
    var time: String? = null
    var isStorySeen: Boolean = false
    var mediaType: MiMediaType = MiMediaType.IMAGE
    var isMediaTypeVideo: Boolean = (mediaType == MiMediaType.VIDEO)
    var statusId: Int? = null

}

class UserSeenList {
    var userName: String? = null
    var statusid: Int = 0
    var seen: Boolean = true

}