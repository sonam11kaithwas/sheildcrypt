package com.advantal.shieldcrypt.ui

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * Created by Sonam on 04-11-2022 15:14.
 */
@Entity(tableName = "tb_recent_chat")

class RecentChatMessageModel(@PrimaryKey
                             @ColumnInfo(name = "threadBareJid") val
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
): Serializable {

}