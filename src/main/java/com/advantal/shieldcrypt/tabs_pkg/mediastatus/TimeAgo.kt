package com.advantal.shieldcrypt.tabs_pkg.mediastatus

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Prashant Lal on 11-11-2022 14:35.
 */
class TimeAgo {

    fun convertFBTime(fbTime: String?): String {
        var ret: String
        try {
            val fbFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
            )
            val eventTime = fbFormat.parse(fbTime)
            val curTime = Date()
            val diffMillis = curTime.time - eventTime.time
            val diffSeconds = diffMillis / 1000
            val diffMinutes = diffMillis / 1000 / 60
            val diffHours = diffMillis / 1000 / 60 / 60
            if (diffSeconds < 60) {
                ret = "$diffSeconds seconds ago"
            } else if (diffMinutes < 60) {
                ret = "$diffMinutes minutes ago"
            } else if (diffHours < 24) {
                ret = "$diffHours hours ago"
            } else {
                var dateFormat = "MMMMM d"
                if (eventTime.year < curTime.year) {
                    dateFormat += ", yyyy"
                }
                dateFormat += "' at 'kk:mm"
                val calFormat = SimpleDateFormat(
                    dateFormat
                )
                ret = calFormat.format(eventTime)
            }
        } catch (ex: Exception) {
            ret = "error: $ex"
        }
        return ret
    }

    }