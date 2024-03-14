package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg

import androidx.room.Entity

/**
 * Created by Sonam on 04-08-2022 12:34.
 */
@Entity(tableName = "message_table")
class MessageModel(
    var sender: String,
    var receiver: String,
    var msg: String,
    var type: String,
    var isMine: Boolean, var msgIdl: Int
) {
//    private var sender: String? = null
//    private var receiver: String? = null
//    private var msg: String? = null
//    private var type: String? = null
//    private var isMine: Boolean? = null
//    private var msgIdl: Int? = null


//    constructor(
//        sender: String?,
//        receiver: String, msg: String,
//        type: String,
//        isMine: Boolean, msgIdl: Int
//    ){
//        this.sender = sender
//        this.receiver=receiver
//        this.msg=msg
//        this.type=type
//        this.isMine=isMine
//        this.msgIdl=msgIdl
//
//   }
//
//    constructor()
}