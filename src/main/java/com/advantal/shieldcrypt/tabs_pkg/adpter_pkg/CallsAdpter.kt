package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.CallsListItemBinding
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.ContentItem
import com.advantal.shieldcrypt.utils_pkg.MyApp
import database.my_database_pkg.db_table.MyAppDataBase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sonam on 17-06-2022 13:58.
 */
class CallsAdpter(private val mList: List<ContentItem>, var listener: CallsSelectCallBack) :
    RecyclerView.Adapter<CallsAdpter.ViewHolder>(), Filterable {
    var callsFilterList = ArrayList<ContentItem>()

    init {
//        callsFilterList = mList as ArrayList<ContentItem>
        callsFilterList.addAll(mList)


        //as ArrayList<ContentItem>


    }

    fun setTaskData(mList: List<ContentItem>) {
        this.callsFilterList.addAll(mList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CallsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myDataModel = callsFilterList[position]

        with(holder) {
            with(myDataModel) {
                if (this.src_cnam != null && !this.src_cnam.isEmpty()) {
                    binding.displayNameTv.text = this.src_cnam
                } else {
                    if (this.src != null) {
                        var nm = MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                            ?.contactDao()?.getUserNameByPhone(this.src)
                        if (nm != null && !nm.isEmpty()) binding.displayNameTv.text = nm
                        else binding.displayNameTv.text = this.src
                    } else binding.displayNameTv.text = this.src
                }
                if (this.src != null && !this.src.isEmpty()) {
                    holder.itemView.visibility = View.VISIBLE
                    binding.root.visibility = View.VISIBLE
                } else {
                    holder.itemView.visibility = View.GONE
                    binding.root.visibility = View.GONE
                }

                if (this.type.toString().equals("OUTGOING")) {
                    holder.binding.callImg.setImageResource(R.drawable.ic_out_going_call)
                } else if (this.type.toString().equals("INCOMING")) {
                    holder.binding.callImg.setImageResource(R.drawable.ic_in_coming_call)
                } else if (this.type.toString().equals("MISSED")) {
                    holder.binding.callImg.setImageResource(R.drawable.ic_miss_call)
                } else {
                    holder.binding.callImg.visibility = View.GONE
                }

                if (this.calldate != null && this.calldate.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm")
                    try {
                        val date = stringToDate(this.calldate)
                        val dateTime = dateFormat.format(date)
                        Log.e("itemListStatus ", " CurrentDateTime--> " + dateTime)
                        binding.callDetails.text = dateTime
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }

                binding.audioVedioCallImg.setOnClickListener {
                    if (listener != null) {
                        listener.clickedOnCall(callsFilterList[position])
                    }
                }

            }
        }
    }

    private fun stringToDate(dtStart: String): Date {
        var date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            if (dtStart != null && dtStart.isNotEmpty()) {
                date = format.parse(dtStart)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return callsFilterList.size
    }

    interface CallsSelectCallBack {
        fun getSelectedUser()
        fun clickedOnCall(selectedRow: ContentItem)
    }

    // Holds the views for adding it to image and text
    class ViewHolder(val binding: CallsListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                callsFilterList = if (charSearch.isEmpty()) {
                    callsFilterList
                } else {
                    val resultList = ArrayList<ContentItem>()
                    for (row in callsFilterList) {
                        if (row.src_cnam != null && !row.src_cnam.isEmpty()) {
                            if (row.src_cnam.lowercase()
                                    .contains(constraint.toString().lowercase()) == true
                            ) {
                                resultList.add(row)
                            }
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = callsFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                callsFilterList = results?.values as ArrayList<ContentItem>
                notifyDataSetChanged()
            }
        }
    }

    fun setTasks(chatList: List<ContentItem>, page: Int) {
        if (page == 0) {
            this.callsFilterList.clear()
        }
        this.callsFilterList.addAll(chatList)
        notifyDataSetChanged()
    }
}