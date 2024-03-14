package com.advantal.shieldcrypt.my_database_pkg.all_dao_pkg

import androidx.room.*
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chat_pkg.RecentChatMessageModel

/**
 * Created by Sonam on 27-09-2022 16:03.
 */
@Dao
interface RecentChatMessageDAO {
    @Insert
    fun insertRecentChat(users: RecentChatMessageModel)

    @Query("select * from tb_recent_chat order by threadName Asc")
    fun getAllNotes(): MutableList<RecentChatMessageModel>?

    @Query("select * from tb_recent_chat where threadBareJid=:threadBareJid")
    fun getThreadExist(threadBareJid: String): List<RecentChatMessageModel>


    @Delete()
    fun deleteRecentChatById(recentChatMessageModel: RecentChatMessageModel)

    @Update()
    fun updateRecentsChat(recentChatMessageModel: RecentChatMessageModel)

    @Query("Select * FROM tb_recent_chat ORDER BY timestamp DESC")//WHERE threadBareJid!=:id
    fun getAllRecentsMessage(
//        id: String
    ): List<RecentChatMessageModel?>?


    @Query("update  tb_recent_chat set unreadCount=0  WHERE threadBareJid = :threadBareJid")
    fun updateReadStatus(threadBareJid: String)

    @Query("delete from tb_recent_chat")
    fun delete()
}