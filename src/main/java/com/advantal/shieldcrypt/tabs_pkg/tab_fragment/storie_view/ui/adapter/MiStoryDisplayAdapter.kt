package com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity.MiUserStoryModel
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.ui.fragment.MiStoryDisplayFragment

class MiStoryDisplayAdapter(
    fa: FragmentActivity,
    private val listOfUserStory: ArrayList<MiUserStoryModel>,
    private val invokeNextStory: (Int) -> Unit,
    private val invokePreviousStory: (Int) -> Unit
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = listOfUserStory.count()

    override fun createFragment(position: Int): Fragment = MiStoryDisplayFragment.newInstance(
        position, invokeNextStory, invokePreviousStory
    )
}