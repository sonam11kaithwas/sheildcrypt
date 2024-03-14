package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.databinding.MyStatusListBinding
import com.advantal.shieldcrypt.databinding.MyStatusViewedByBinding
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory
import com.advantal.shieldcrypt.tabs_pkg.model.DataItemStatus
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Prashant Lal on 09-11-2022 19:32.
 */
class MyStatusViewbyAdapter(private val mList: ArrayList<DataItemStatus>) : RecyclerView.Adapter<MyStatusViewbyAdapter.ViewHolder>()
{
    var itemListStatus = ArrayList<DataItemStatus>()
    init {
        itemListStatus = mList as ArrayList<DataItemStatus>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            MyStatusViewedByBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]

        with(holder) {
            with(myDataModel) {
                holder.binding.idTVCourseName.text = this.contactName
//                holder.binding.idTseentime.text = this.seentime

                val time1 = convertFBTime(this.seentime.toString())
                Log.e("onBindViewHolder: ", time1.toString())

                holder.binding.idTseentime.text = time1

                Glide.with(AuthViewModelFactory.context)
                    .load(mediadetails)
                    .into(holder.binding.idIVCourse)

//
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

    //        if (selectedPosition == position)
//            holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
//    interface ItemClickListner {
//
//        fun getItemSelected(invListItem: DataItem)
//
//    }

    class ViewHolder(val binding: MyStatusViewedByBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<DataItemStatus>) {
        this.itemListStatus = itemListStatus
        notifyDataSetChanged()
    }

    fun convertFBTime(fbTime: String?): String {
        var ret: String
        try {
            val fbFormat = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss"
            )
            val eventTime: Date = fbFormat.parse(fbTime)
            val curTime = Date()
            val diffMillis: Long = curTime.getTime() - eventTime.getTime()
            val diffSeconds = diffMillis / 1000
            val diffMinutes = diffMillis / 1000 / 60
            val diffHours = diffMillis / 1000 / 60 / 60
            if (diffSeconds < 60) {
                ret = "$diffSeconds seconds ago"
            } else if (diffMinutes < 60) {
                ret = "$diffMinutes minutes ago"
            } else if (diffHours < 24) {
                ret = "$diffHours hours ago"
            }
            else if (diffHours < 48) {
                ret = "Yesterday"
            }
            else {
                var dateFormat = "MMM d"
                if (eventTime.getYear() < curTime.getYear()) {
                    dateFormat += ", yyyy"
                }
                dateFormat += "' at 'kk:mm"
                val calFormat = SimpleDateFormat(
                    dateFormat
                )
                ret = calFormat.format(eventTime)
            }
        } catch (ex: Exception) {
            ret = "error: $ex"
        }
        return ret
    }


}