package com.advantal.shieldcrypt.meeting.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.ParseException
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.MeetingListRecycleBinding
import com.advantal.shieldcrypt.databinding.MeetingRecrLayBinding
import com.advantal.shieldcrypt.meeting.model.DataItemM

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Prashant Lal on 19-11-2022 16:19.
 */
class MeetingRecrAdapter (private val mList: ArrayList<DataItemM>, var listener: MeetingRecrAdapter.ItemClickListner) : RecyclerView.Adapter<MeetingRecrAdapter.ViewHolder>() {
    var itemListStatus = ArrayList<DataItemM>()
    init {
        itemListStatus = mList as ArrayList<DataItemM>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            MeetingRecrLayBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]

        with(holder) {
            with(myDataModel) {
              holder.binding.meetingrecr.text = this.name


            }
        }


//        holder.binding.itemClickListner.setOnClickListener {
//            listener.getItemSelected(itemListStatus!![position])
//
//        }
    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {

        fun getItemSelected(invListItem: DataItemM)

    }

    class ViewHolder(val binding: MeetingRecrLayBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<DataItemM>) {
        this.itemListStatus = itemListStatus
        notifyDataSetChanged()
    }



}