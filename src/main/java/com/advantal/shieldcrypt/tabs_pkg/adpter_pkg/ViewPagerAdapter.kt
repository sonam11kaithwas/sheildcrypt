package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.CallListFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.ChatListFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.NewCallLogFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.SettingFragment
import com.advantal.shieldcrypt.ui.ConversationsOverviewFragment


/**
 * Created by Sonam on 29-07-2022 11:57.
 */

class ViewPagerAdapter(fragmentActivity: FragmentManager?, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentActivity!!, lifecycle) {
//    (fragmentActivity: FragmentActivity?, private var totalCount: Int) :
//    FragmentStateAdapter(fragmentActivity!!) {
     var chatListFragment: ConversationsOverviewFragment?=null
//     var chatListFragment: ChatListFragment?=null
     var callListFragment: NewCallLogFragment?=null
     var settingFragment: SettingFragment?=null

    init {
//        chatListFragment = ChatListFragment()
        chatListFragment = ConversationsOverviewFragment()
        callListFragment = NewCallLogFragment()
        settingFragment = SettingFragment()
    }

    override fun getItemCount(): Int {
        return 3
    }


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> chatListFragment!!//ChatListFragment()
            1 -> callListFragment!!
            2 -> settingFragment!!
            else -> chatListFragment!!
        }
    }
}