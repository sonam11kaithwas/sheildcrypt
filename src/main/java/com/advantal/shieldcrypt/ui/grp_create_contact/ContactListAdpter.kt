package com.advantal.shieldcrypt.ui.grp_create_contact

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ContactItemViewBinding
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


/**
 * Created by Sonam on 05-07-2022 18:12.
 */
class ContactListAdpter(
    val context: Context,
    val mList: MutableList<ContactDataModel>?,
    var listener: ContactSelected
) :


    RecyclerView.Adapter<ContactListAdpter.ViewHolder>(), Filterable {
    var selectedItemPositionList = ArrayList<Int>()
    var searchList = ArrayList<ContactDataModel>()
    fun getSelecteContact(): ArrayList<Int> {
        return selectedItemPositionList
    }

    init {
        searchList = mList as ArrayList<ContactDataModel>
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ContactItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactModel = searchList[position]
        with(holder) {
            with(contactModel) {

                binding.txtName.text = this.contactName
                binding.txtSize.text = this.mobileNumber


                holder.binding.contactImg.setImageDrawable(
                    context.resources.getDrawable(R.drawable.one_person, null)
                )
                if (!this.image.equals("")) {
                    this.let {
                        Glide.with(context).load(this.image)
                            .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                            .into(holder.binding.contactImg)
                    }

                } else {

                    Glide.with(context).load(holder.binding.contactImg)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(holder.binding.contactImg)
                }



                binding.relativeMain.setOnClickListener {
                    if (!selectedItemPositionList.contains(contactModel.id))
                        selectedItemPositionList.add(contactModel.id)
                    else {
                        selectedItemPositionList.remove(contactModel.id)
                    }
                    /*if (!selectedItemPositionList.contains(contactModel.mobileNumber))
                        selectedItemPositionList.add(contactModel.mobileNumber)
                    else {
                        selectedItemPositionList.remove(contactModel.mobileNumber)
                    }*/
                    notifyDataSetChanged()

                    listener.getSelectMultipleCon(contactModel)
                }

                if (selectedItemPositionList.contains(contactModel.id))
//                if (selectedItemPositionList.contains(contactModel.mobileNumber))
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#C2E9F8"))
                else
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#F5F6F8"))

            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return searchList.size
    }

    interface ContactSelected {
        fun getSelectMultipleCon(contactModel: ContactDataModel)
    }

    class ViewHolder(val binding: ContactItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                searchList = if (charSearch.isEmpty()) {
                    mList as ArrayList<ContactDataModel>
                } else {
                    val resultList = ArrayList<ContactDataModel>()

                    if (mList != null) {
                        for (row in mList) {
                            Log.e("Name: ", row.contactName)

                            if (row.contactName.lowercase()
                                    .contains(constraint.toString().lowercase())
                            ) {
                                resultList.add(row)
                            }
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = searchList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchList = results?.values as ArrayList<ContactDataModel>
                notifyDataSetChanged()
            }

        }
    }


}

