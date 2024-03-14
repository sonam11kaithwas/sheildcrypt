package com.advantal.shieldcrypt.xmpp_pkg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

/**
 * Created by Sonam on 16-08-2022 13:17.
 */
@Entity(tableName = "tb_chat")
class MyMessageModel : Serializable {

    constructor()
    constructor(mes: String) {
        ownerBareJid = mes
    }

    @NotNull
    @PrimaryKey
    @ColumnInfo(name = "messageIdNew")
    //@NotNull
  //  var msgId: String? = null
    var messageIdNew: String = ""
    var ownerBareJid: String? = null
    var threadBareJid: String? = null
    var rosterBareJid: String? = null
    var content: String? = null
    var contentType: Int = 0
    var isOutBound: Boolean = false
//    var isChatThreadTypes: String? = null
    var myType: String? = null
    var isMessageRead: Boolean = false

    //    var messageDate: Date? = null
    var messageDate: String? = null
    var messageTimestamp: Long? = null
    var deliveryStatus: Int = 0
    var localFilePath: String? = null
    var fileTransferStatus: Int = 0
//    var thumbOfMedia = listOf<ByteArray>()
//    var  thumbOfMedia: ByteArray?=null

    var latLong: String? = null
    var actionOnMessage: Int = 0
    var isMessageStared: Boolean = false
    var commentOnMedia: String? = null
    var commentOfOldMessage: String? = null
    var messageIdOfOldMessage: String? = null
    var priorityLevel: Int = 0
    var repliedOnWhichContent: Int = 0

    //    var thumbOfOldMedia: ByteArray?=null
    var evapExpireDuration: Int = 0
    var forwardedMessageCount: Int = 0
}