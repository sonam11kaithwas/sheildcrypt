package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.ChatsListItemBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chat_pkg.RecentChatMessageModel
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import database.my_database_pkg.db_table.MyAppDataBase

/**
 * Created by Sonam on 17-06-2022 12:39.
 */
class ChatAdapter(
    private val mList: List<RecentChatMessageModel>,
    var listener: ChatSelectCallBack,
    var context: Context
) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>(), Filterable {
    var chatFilterList = ArrayList<RecentChatMessageModel>()


    init {
        chatFilterList = mList as ArrayList<RecentChatMessageModel>
    }

    class ViewHolder(val binding: ChatsListItemBinding) : RecyclerView.ViewHolder(binding.root)


    // create new views
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.chats_list_item, parent, false)
        val binding =
            ChatsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    fun setTasks(chatList: List<RecentChatMessageModel>) {

        this.chatFilterList.clear()
        this.chatFilterList.addAll(chatList)


        notifyDataSetChanged()
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myDataModel = chatFilterList[position]


        with(holder) {
            with(myDataModel) {
                if (!this.threadName.isEmpty())
                    binding.displayNameTv.text = this.threadName
                binding.contactStatusTv.text = this.content

                try {
                    var img =
                        MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                            ?.contactDao()
                            ?.getUser(this.threadBareJid)
                    if (img?.isNotEmpty()!!)
                        Glide.with(context)
                            .load(img)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.includeLay.userImg)
                } catch (e: Exception) {
                    e.message
                }

                if (this.unreadCount != 0) {
                    binding.tvChatCount.visibility = View.VISIBLE
                    binding.tvChatCount.text = this.unreadCount.toString()
                } else {
                    binding.tvChatCount.visibility = View.INVISIBLE
                }

                binding.constraintLayout.setOnClickListener {
                    listener.getSelectedUser(this)
                }
            }


        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return chatFilterList.size
    }

    interface ChatSelectCallBack {
        fun getSelectedUser(myDataModel: RecentChatMessageModel)
    }

    // Holds the views for adding it to image and text


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                chatFilterList = if (charSearch.isEmpty()) {
                    mList as ArrayList<RecentChatMessageModel>
                } else {
                    val resultList = ArrayList<RecentChatMessageModel>()
                    for (row in mList) {
                        if (row.threadName.lowercase()
                                .contains(constraint.toString().lowercase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = chatFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                chatFilterList = results?.values as ArrayList<RecentChatMessageModel>
                notifyDataSetChanged()
            }
        }
    }

}
