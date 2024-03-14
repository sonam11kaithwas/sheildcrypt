package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.databinding.FragmentChatListBinding
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.ChatAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.chat_pkg.RecentChatMessageModel
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.xmpp_pkg.RoosterConnectionService
import com.google.gson.Gson
import database.my_database_pkg.db_table.MyAppDataBase


class ChatListFragment : Fragment(), ChatAdapter.ChatSelectCallBack {
    lateinit var binding: FragmentChatListBinding
    var adapter: ChatAdapter? = null
    var recentChatList: List<RecentChatMessageModel> = arrayListOf<RecentChatMessageModel>()
    lateinit var mBroadcastReceiver: BroadcastReceiver

    fun filterChatsList(query: String) {
        adapter?.filter?.filter(query)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_chat_list, container, false)
        binding = FragmentChatListBinding.inflate(inflater, container, false)


        adapter = ChatAdapter(
            recentChatList, this, requireActivity()
        )

        // Setting the Adapter with the recyclerview
        binding.chatRecyclerView.adapter = adapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(activity)



        return binding.root

    }

    private fun featchDbData() {
//        MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.recentChatMessageDao()
//            ?.getAllNotes()
//        recentChatList =
//            MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.recentChatMessageDao()
//                ?.getAllRecentsMessage(
//                    MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
//                ) as List<RecentChatMessageModel>
        adapter?.setTasks(recentChatList)
        if (recentChatList!=null && recentChatList.size>0){
            binding.msg.visibility = View.GONE
        } else {
            binding.msg.visibility = View.VISIBLE
        }

    }

    override fun onPause() {
        activity?.unregisterReceiver(mBroadcastReceiver)
        super.onPause()
    }

    override fun onResume() {
        featchDbData()
        registerBroadcasts()
//        recentChatList =
//            MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.recentChatMessageDao()
//                ?.getThreadExist(
//                    MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
//                ) as List<RecentChatMessageModel>
//        adapter?.setTasks(recentChatList )
        super.onResume()
    }

    override fun getSelectedUser(myDataModel: RecentChatMessageModel) {
        val intent = Intent(activity, ChatsActivity::class.java)
        intent.putExtra(
            "chatUser", Gson().toJson(
                MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.contactDao()
                    ?.getContactDataByThreadId(myDataModel.threadBareJid)
            )
        )
        startActivity(intent)
    }

    fun registerBroadcasts() {
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    RoosterConnectionService.NEW_MESSAGE -> {
                        featchDbData()
                        return
                    }
                }
            }
        }
        val filter = IntentFilter(RoosterConnectionService.NEW_MESSAGE)
        activity?.registerReceiver(mBroadcastReceiver, filter)
    }
}