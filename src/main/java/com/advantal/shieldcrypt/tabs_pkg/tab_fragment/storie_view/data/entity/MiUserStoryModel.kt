package com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity

import android.os.Parcelable
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity.MiStoryModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MiUserStoryModel(
    val id: String?,
    val userName: String?,
    val userStoryList: ArrayList<MiStoryModel>,
    var lastStoryPointIndex: Int = 0
) : Parcelable