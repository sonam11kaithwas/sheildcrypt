package com.advantal.shieldcrypt.meeting.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.AddedAttenderShowItemBinding
import com.advantal.shieldcrypt.meeting.model.DataItemL
import kotlin.collections.ArrayList

class AddedAttenderShowAdapter (private val fromStatus: String, private val mList: List<DataItemL>, var listener: AddedAttenderShowAdapter.ItemClickListner)
    : RecyclerView.Adapter<AddedAttenderShowAdapter.ViewHolder>() {
    var itemListStatus = ArrayList<DataItemL>()
    init {
        itemListStatus = mList as ArrayList<DataItemL>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddedAttenderShowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = itemListStatus[position]
        Log.e("myDataModel", " -> " +myDataModel.lastName)
        with(holder) {
            with(myDataModel) {
                holder.binding.text.setText(""+myDataModel.firstName+" "+ myDataModel.lastName)
//                holder.binding.displayDateList.text = this.start?.substring(0,10)

              /*  val time1 = convertISOTimeToDate(this.start.toString())
                Log.e("onBindViewHolder: ", time1.toString())
                holder.binding.displayDateList.text = time1


                holder.binding.meetingTime.text = this.start?.substring(11,16) + "-" + this.end?.substring(11,16)
//                holder.binding.meetingendTime.text = this.end?.substring(11,16)
                holder.binding.displayMeetingTitle.text = this.title*/

            }
        }
        holder.binding.imgRemove.setOnClickListener{
            if (fromStatus.equals("add")){
                itemListStatus.remove(myDataModel)
                notifyDataSetChanged()
            } else if (fromStatus.equals("edit")){
                if (listener!=null){
                    listener.onDeleteClicked(myDataModel)
                }
            }
        }
//        if (selectedPosition == position)
//            holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
//        else
//            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))

        /*holder.binding.itemClickListner.setOnClickListener {
            listener.getItemSelected(itemListStatus!![position])
        }*/
    }

    override fun getItemCount(): Int {
        return itemListStatus.size
    }

    interface ItemClickListner {
        fun getItemSelected(invListItem: DataItemL)
        fun onDeleteClicked(invListItem: DataItemL)
    }

    class ViewHolder(val binding: AddedAttenderShowItemBinding) : RecyclerView.ViewHolder(binding.root)
}