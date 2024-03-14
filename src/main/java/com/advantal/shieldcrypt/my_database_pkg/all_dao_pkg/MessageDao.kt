package com.advantal.shieldcrypt.my_database_pkg.all_dao_pkg

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.advantal.shieldcrypt.xmpp_pkg.MyMessageModel

/**
 * Created by Sonam on 18-08-2022 16:05.
 */
@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(users: MyMessageModel)

    @Query("update tb_chat set deliveryStatus=:deliveryStatus where messageIdNew=:packetId")
    fun updateDeliveryStatus(packetId: String, deliveryStatus: Int)

    @Query("Update tb_chat set deliveryStatus = :xepStatus WHERE messageIdNew = :messageId")
    fun updateXepMessageStatus(messageId: String, xepStatus: Int)


    @Query("Select COUNT(*) FROM tb_chat WHERE messageIdNew = :messageId")
    fun checkMessageExist(messageId: String): Int

    @Query("Select * FROM tb_chat WHERE deliveryStatus = :xepDeliveryStatus")
    fun getAllOfflineMessages(xepDeliveryStatus: Int): List<MyMessageModel>

    @Query("Select * FROM tb_chat WHERE threadBareJid = :friendsJid and ownerBareJid=:ownerJid or threadBareJid=:ownerJid and ownerBareJid=:friendsJid")
    fun getChatsFromFriend(friendsJid: String, ownerJid: String): List<MyMessageModel>

    @Query("select * from tb_chat")
    fun getAllChats(): MutableList<MyMessageModel>?

    @Query("Update tb_chat set isMessageRead = 1 WHERE threadBareJid = :friendsJid")
    fun updateReadStatus(friendsJid: String)

    @Query(
        "Select * FROM tb_chat WHERE  threadBareJid = :friendsJid and ownerBareJid=:ownerJid or threadBareJid=:ownerJid and " +
                "ownerBareJid=:friendsJid"
    )
    fun getAllLiveChatByJid(friendsJid: String, ownerJid: String): LiveData<List<MyMessageModel>>

    @Query("delete from tb_chat")
    fun delete()
}