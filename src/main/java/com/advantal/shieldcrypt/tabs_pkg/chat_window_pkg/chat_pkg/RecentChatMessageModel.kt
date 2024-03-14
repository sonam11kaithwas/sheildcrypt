package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chat_pkg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by Sonam on 27-09-2022 15:59.
 */
@Entity(tableName = "tb_recent_chat")
class RecentChatMessageModel(@PrimaryKey
                             @ColumnInfo(name = "threadBareJid") var
                             threadBareJid: String,
                             val content: String,
                             val contentType: Int
                             , val timestamp: Long?,
                             val unreadCount: Int,
                             val deliveryStatus: Int,
                             val messageId: String,
                             val isChatThreadType: String?,
                             val isOutBound: Boolean,
                             val threadName: String
                             ):Serializable {

}