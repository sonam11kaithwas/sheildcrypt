package com.advantal.shieldcrypt.meeting.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.ParseException
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.advantal.shieldcrypt.databinding.MeetingListRecycleBinding
import com.advantal.shieldcrypt.meeting.model.DataItem
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.GetAllMediaStatusAdapter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Prashant Lal on 16-11-2022 16:12.
 */
class MeetingMainAdapter (private val mList: ArrayList<DataItem>,var listener: MeetingMainAdapter.ItemClickListner) : RecyclerView.Adapter<MeetingMainAdapter.ViewHolder>() {
    var itemListStatus = ArrayList<DataItem>()
    init {
        itemListStatus = mList as ArrayList<DataItem>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            MeetingListRecycleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]

        with(holder) {
            with(myDataModel) {
//                holder.binding.displayDateList.text = this.start?.substring(0,10)

                val time1 = convertISOTimeToDate(this.start.toString())
                Log.e("onBindViewHolder: ", time1.toString())
                holder.binding.displayDateList.text = time1


                holder.binding.meetingTime.text = this.start?.substring(11,16) + "-" + this.end?.substring(11,16)
//                holder.binding.meetingendTime.text = this.end?.substring(11,16)
                holder.binding.displayMeetingTitle.text = this.title

            }
        }



//        if (selectedPosition == position)
//            holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))

        holder.binding.itemClickListner.setOnClickListener {
            listener.getItemSelected(itemListStatus!![position])


        }

        holder.binding.threeDotsImg.setOnClickListener{
            //Log.e("joinBtn", "  -->> " + Gson().toJson(itemListStatus!![position]))
            if (listener!=null){
                listener.onJoinMeetingClicked(itemListStatus!![position])
            }
        }
    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {

        fun getItemSelected(invListItem: DataItem)
        fun onJoinMeetingClicked(invListItem: DataItem)
    }

    class ViewHolder(val binding: MeetingListRecycleBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<DataItem>) {
        this.itemListStatus = itemListStatus
        notifyDataSetChanged()
    }

    fun convertISOTimeToDate(isoTime: String): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var convertedDate: Date? = null
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("MMM dd yyyy").format(convertedDate)
//            formattedDate = SimpleDateFormat("MMM dd,yyyy").format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formattedDate
    }

}