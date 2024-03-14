package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.databinding.MyStatusListBinding
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory.Companion.context
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Prashant Lal on 02-11-2022 18:51.
 */
class MyStatusAdapter(private val mList: ArrayList<MyStatusResponse>,
                      var listener: ItemClickListner, ) : RecyclerView.Adapter<MyStatusAdapter.ViewHolder>() {
    var itemListStatus = ArrayList<MyStatusResponse>()
    init {
        itemListStatus = mList as ArrayList<MyStatusResponse>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            MyStatusListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]

        with(holder) {
            with(myDataModel) {
                if (this.count!! > 1) {
                    holder.binding.displayStatusTv.text =
                        "${this.count} views"
                } else {
                    holder.binding.displayStatusTv.text =
                        "${this.count} view"
                }
                val time1 = convertFBTime(this.creationDate.toString())
                holder.binding.viewsDetails.text = time1
                if(this.mediadetails.equals(null)){
                    holder.binding.displayPicImg.visibility = View.GONE
                    holder.binding.displayStatus.visibility = View.VISIBLE
                }
                else {
                    holder.binding.displayStatus.visibility = View.GONE
                    holder.binding.displayPicImg.visibility = View.VISIBLE
                    Glide.with(context)
                        .load(mediadetails)
                        .into(holder.binding.displayPicImg)
                }

            }

        }

        holder.binding.itemClickListner.setOnClickListener {
            listener.getItemSelected(itemListStatus[position])

        }

    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {

        fun getItemSelected(invListItem: MyStatusResponse)

    }

    class ViewHolder(val binding: MyStatusListBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<MyStatusResponse>) {
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



