package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.ItemUserStoryBinding
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity.MiUserStoryModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StoryAdapter(
    private var listOfUseStory: ArrayList<MiUserStoryModel>,
    private val launcherCallBack: () -> ActivityResultLauncher<Intent>,
    private val activityCallBack: () -> AppCompatActivity
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StoryViewHolder(
            ItemUserStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is StoryViewHolder) {
            holder.mBinding.apply {
                root.setOnTouchListener { view, motionEvent ->
                    msvStory.dispatchTouchEvent(motionEvent)
                    true
                }
                msvStory.apply {
                    setActivity(activityCallBack.invoke())
                    setLauncher(launcherCallBack.invoke())
                    if (listOfUseStory.isNotEmpty()) {
                        setImageUrls(listOfUseStory, holder.adapterPosition)
                    }
                }
                tvUserName.text = listOfUseStory[holder.adapterPosition].userName!!.capitalize()
                val listSize = listOfUseStory[holder.adapterPosition].userStoryList.size
                val time1 =
                    listOfUseStory[holder.adapterPosition].userStoryList[listSize - 1].time.toString()
                statusTime.text = time1
            }
        }
    }

    override fun getItemCount(): Int = listOfUseStory.size

    fun setUserStoryData(mDataList: ArrayList<MiUserStoryModel>) {
        this.listOfUseStory.clear()
        this.listOfUseStory = mDataList
        notifyDataSetChanged()
    }

    inner class StoryViewHolder(itemView: ItemUserStoryBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val mBinding: ItemUserStoryBinding = itemView
    }

}