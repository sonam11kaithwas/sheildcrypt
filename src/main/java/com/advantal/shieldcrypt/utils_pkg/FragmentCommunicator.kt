package com.advantal.shieldcrypt.utils_pkg

import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.entities.Conversation

interface FragmentCommunicator {
    fun passData()
    fun openChatWindow(uuid: String?)
    fun accountNotify(account: Account)
    fun firstLoadGrpList()
//    fun openChatWindowForGrp(conversation: Conversation)
}

interface FragmentCommunicatorForMsgs {
    fun passUpdateMsgData()
}
