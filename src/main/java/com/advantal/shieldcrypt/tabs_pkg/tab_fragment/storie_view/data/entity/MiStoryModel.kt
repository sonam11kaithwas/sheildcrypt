package com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity

import android.os.Parcelable
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.util.MiMediaType
import kotlinx.parcelize.Parcelize

@Parcelize
data class MiStoryModel(
    var mediaUrl: String?,
    var name: String?,
    var time: String?,
    var isStorySeen: Boolean = false,
    var mediaType: MiMediaType = MiMediaType.IMAGE,
    var isMediaTypeVideo: Boolean = (mediaType == MiMediaType.VIDEO),
    var statusId:Int?
) : Parcelable