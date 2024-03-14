package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.GetAllMediaStatusBinding
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory
import com.advantal.shieldcrypt.tabs_pkg.model.DataItemMediaStatus
import com.advantal.shieldcrypt.tabs_pkg.model.GetAllMediaStatusResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Prashant Lal on 09-11-2022 15:46.
 */
class GetAllMediaStatusAdapter(
    private val mList: ArrayList<DataItemMediaStatus>,
    var listener: GetAllMediaStatusAdapter.ItemClickListner,
) : RecyclerView.Adapter<GetAllMediaStatusAdapter.ViewHolder>() {

    var itemListStatus = ArrayList<GetAllMediaStatusResponse>()
    init {
        itemListStatus = mList as ArrayList<GetAllMediaStatusResponse>
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            GetAllMediaStatusBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]
        val listSize = myDataModel.data?.size
        val lastIndex =  listSize!! -1
        with(holder) {
            with(myDataModel) {
                holder.binding.displayStatusTv.text = this.username
//                val time = this.statuscreationdatetime.toString()
                val time1 = convertFBTime(this.data!![lastIndex].statuscreationdatetime.toString())
                Log.e("onBindViewHolder: ", time1.toString())

                holder.binding.statusTime.text = time1


                if(this.data!![lastIndex].mediastatusdetails.equals("")){
                    holder.binding.displayPicImg.visibility = View.GONE
                    holder.binding.displayStatus.visibility = View.VISIBLE
                    holder.binding.displayStatus.text = this.data!![lastIndex].penciltext
                    holder.binding.displayStatus.setBackgroundColor(Color.BLUE)
                    val backgroundcolorint = this.data!![lastIndex].colorcode
                    val backgroundcolor = backgroundcolorint.toString()
                    val fontint = this.data!![lastIndex].fontstylecode
                    val font = fontint.toString()
                    if(backgroundcolor.equals("1")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.circular_bg)
                    }
                    else if(backgroundcolor.equals("2")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.twoeblue)
                    }
                    else if(backgroundcolor.equals("3")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.threeeorange)
                    }
                    else if(backgroundcolor.equals("4")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.fouredarkgreen)
                    }
                    else if(backgroundcolor.equals("5")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.fiveelightgreen)
                    }
                    else if(backgroundcolor.equals("6")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.sixblack_clr)
                    }
                    else if(backgroundcolor.equals("7")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.sevenenormalblue)
                    }
                    else if(backgroundcolor.equals("8")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.eightegreylight)
                    }
                    else if(backgroundcolor.equals("9")){
                        holder.binding.displayStatus.setBackgroundResource(R.drawable.eonegreenlight)
                    }
                    else{

                    }

                    if (font.equals("1"))
                    {
                        holder.binding.displayStatus.setTypeface(Typeface.DEFAULT)
                    }
                    else if (font.equals("2"))
                    {
                        holder.binding.displayStatus.setTypeface(Typeface.SANS_SERIF)
                    }
                    else if (font.equals("3"))
                    {
                        holder.binding.displayStatus.setTypeface(Typeface.DEFAULT_BOLD)
                    }
                    else if (font.equals("4"))
                    {
                        holder.binding.displayStatus.setTypeface(Typeface.SERIF)
                    }
                    else if (font.equals("5"))
                    {
                        holder.binding.displayStatus.setTypeface(Typeface.MONOSPACE)
                    }
                    else
                    {

                    }

                }

                else {

//                holder.binding.viewsDetails.text = this.mediadatetime
                    holder.binding.displayStatus.visibility = View.GONE
                    holder.binding.displayPicImg.visibility = View.VISIBLE
                    Glide.with(AuthViewModelFactory.context)
                        .load(this.data!![lastIndex].mediastatusdetails)
                        .into(holder.binding.displayPicImg)

                }


            }

        }

        holder.binding.itemClickListner.setOnClickListener {
            listener.getItemSelected(itemListStatus[position].data!!,itemListStatus[position].username)

        }

    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {

        fun getItemSelected(invListItem: ArrayList<DataItemMediaStatus>, username: String?)

    }

    class ViewHolder(val binding: GetAllMediaStatusBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<GetAllMediaStatusResponse>) {
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