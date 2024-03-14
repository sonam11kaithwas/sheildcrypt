package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chats_histry_pkg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityChatsHistoryBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg.DocumentPickerAdapter
import com.advantal.shieldcrypt.utils_pkg.MyApp

class ChatsHistoryActivity : AppCompatActivity() , ChatsHistoryAdapter.ChatActionSelected {
    lateinit var binding: ActivityChatsHistoryBinding
    private var adapter: ChatsHistoryAdapter? = null
    private var chatHistList=ArrayList<ChatHistroryListModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatsHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatHistList.add(ChatHistroryListModel(R.drawable.ic_chat_export,"Export Chat"))
        chatHistList.add(ChatHistroryListModel(R.drawable.ic_archive,"Archive all Chats"))
        chatHistList.add(ChatHistroryListModel(R.drawable.ic_clear_chat,"Clear all Chats"))
        chatHistList.add(ChatHistroryListModel(R.drawable.ic_delete_account,"Delete all Chats"))
        viewsInitializes()
    }
    private fun viewsInitializes() {
        adapter = ChatsHistoryAdapter(applicationContext, chatHistList, this)

        binding.chatHistorRcyclerview.layoutManager = LinearLayoutManager(this)
        binding.chatHistorRcyclerview.adapter = adapter
    }

    override fun getSelectChatHistroyEvent() {
         MyApp.getAppInstance().showToastMsg("Selected")
    }
}