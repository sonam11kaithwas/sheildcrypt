package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chats_histry_pkg

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.ChatHistoryItemViewBinding

/**
 * Created by Sonam on 22-07-2022 16:08.
 */
class ChatsHistoryAdapter(
    val context: Context,
    private val mList: List<ChatHistroryListModel>,
    var listener: ChatActionSelected

) :
    RecyclerView.Adapter<ChatsHistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ChatHistoryItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val documentModel = mList[position]

        with(holder) {
            with(documentModel) {

                binding.img.setImageResource(0)

                binding.txtName.text = this.chatHistNm
                binding.img.setImageResource(this.chatHisIc)


                binding.relativeMain.setOnClickListener {
                    listener.getSelectChatHistroyEvent()
                }

//                binding.img.setImageResource(0)

            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    interface ChatActionSelected {
        fun getSelectChatHistroyEvent()
    }

    class ViewHolder(val binding: ChatHistoryItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

}


