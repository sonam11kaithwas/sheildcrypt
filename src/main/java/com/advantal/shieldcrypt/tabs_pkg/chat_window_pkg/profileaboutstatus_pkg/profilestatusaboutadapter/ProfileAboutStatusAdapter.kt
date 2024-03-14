package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profilestatusaboutadapter

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.AboutStatusProfileBinding
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.InvListItem
import okhttp3.internal.notify

/**
 * Created by Prashant Lal on 20-10-2022 16:37.
 */
class ProfileAboutStatusAdapter(private val mList: ArrayList<InvListItem>, var listener: ItemClickListner, ) : RecyclerView.Adapter<ProfileAboutStatusAdapter.ViewHolder>() {
    var itemListStatus = ArrayList<InvListItem>()
    init {
        itemListStatus = mList as ArrayList<InvListItem>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding =
            AboutStatusProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]

        with(holder) {
            with(myDataModel) {
                holder.binding.statusTextView.text = this.defaultstatus?.name
            }
        }

//        if(itemListStatus!![position].check){
//            holder.binding.callImg.visibility=View.VISIBLE
//        }
//        else{
//            holder.binding.callImg.visibility=View.GONE
//
//        }


        if (itemListStatus!![position].active == true){
            listener.getItemSelected(itemListStatus!![position], 1)
            holder.binding.callImg.visibility=View.VISIBLE
        } else{
            holder.binding.callImg.visibility=View.GONE
        }


        holder.binding.itemClickListner.setOnClickListener {

//            itemListStatus!![position].active == true
//            notifyDataSetChanged()
//            notifyItemChanged(position)
//            if(itemListStatus!![position].active == true){
//                notifyDataSetChanged()
//                holder.binding.callImg.visibility=View.VISIBLE
//            }else{
//                notifyDataSetChanged()
//                holder.binding.callImg.visibility=View.GONE
//            }


            listener.getItemSelected(itemListStatus!![position], 2)

//            Glide.with(AuthViewModelFactory.context)
//                .load(this.itemListStatus)
//                .placeholder(R.drawable.ic_action_name)
//                .into(holder.binding.callImg)

        }

//        if (selectedPosition == position)
//            holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))


    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {

        fun getItemSelected(invListItem: InvListItem, apiLoginStatus: Int)

    }

    class ViewHolder(val binding: AboutStatusProfileBinding) : RecyclerView.ViewHolder(binding.root)

    public fun updateStatuslist(itemListStatus: ArrayList<InvListItem>) {
        this.itemListStatus = itemListStatus
        notifyDataSetChanged()
    }

}