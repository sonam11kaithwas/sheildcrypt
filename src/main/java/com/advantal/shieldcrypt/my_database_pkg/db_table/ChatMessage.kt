package com.advantal.shieldcrypt.my_database_pkg.db_table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Sonam on 18-08-2022 16:06.
 */
@Entity(tableName = "chatMsgs_table")
data class ChatMessage(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
)