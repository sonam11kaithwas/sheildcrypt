package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.MsgRecieveLayoutBinding
import com.advantal.shieldcrypt.databinding.MsgSendLayoutBinding
import com.advantal.shieldcrypt.databinding.RecieveChatMsgsLayoutBinding
import com.advantal.shieldcrypt.databinding.SendMsgChatLayoutBinding
import com.advantal.shieldcrypt.utils_pkg.DateUtil
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.xmpp_pkg.MyMessageModel

/**
 * Created by Sonam on 04-08-2022 12:29.
 */
class MessageListAdapter(
    val context: Context, private var mMessageList: ArrayList<MyMessageModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val VIEW_TYPE_MESSAGE_SENT = 1
        private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    fun setTasks(chatList: List<MyMessageModel>, isNewChat: Boolean) {

        this.mMessageList.clear()
        this.mMessageList.addAll(chatList)
        Log.e("List update", "Update")
        if (isNewChat) {
            if (context is ChatsActivity) {
                context.scrollChatToLastPosition()
            }
        }
        notifyDataSetChanged()
    }

    fun updatelist(mMessageLists: ArrayList<MyMessageModel>) {
        this.mMessageList.addAll(mMessageLists)
        Log.e("List update", "Update")
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (MySharedPreferences.getSharedprefInstance()
                .getLoginData().mobileNumber == mMessageList[position].ownerBareJid
        ) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            val binding =
                MsgSendLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            SentMessageHolder(binding)
        } else {
            val binding =
                MsgRecieveLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ReceivedMessageHolder(binding)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var chatUserModel = mMessageList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                (holder as SentMessageHolder).binding.textGchatImgMessageRcve.text =
                    mMessageList[position].content
                (holder as SentMessageHolder).binding.textGchatImgTimestampRcve.text =
                    DateUtil.longToDate(mMessageList[position].messageTimestamp
                        ,"hh mm a")

            }

            VIEW_TYPE_MESSAGE_RECEIVED -> {
                (holder as ReceivedMessageHolder).binding.textGchatMessageSend.text = mMessageList[position].content
                (holder as ReceivedMessageHolder).binding.textGchatTimestampSend.text = DateUtil.longToDate(mMessageList[position].messageTimestamp
                    ,"hh mm a")
            }

        }
    }


    class SentMessageHolder(val binding: MsgSendLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ReceivedMessageHolder(val binding: MsgRecieveLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


}