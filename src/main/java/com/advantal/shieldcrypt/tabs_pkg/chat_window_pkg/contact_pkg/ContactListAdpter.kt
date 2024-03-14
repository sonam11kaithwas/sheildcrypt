package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.contact_pkg

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ContactItemViewBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.contact_pkg.contact_model.ContactModel


/**
 * Created by Sonam on 05-07-2022 18:12.
 */
class ContactListAdpter(
    val context: Context,
    val mList: ArrayList<ContactModel>?,
    var listener: ContactSelected
) :


    RecyclerView.Adapter<ContactListAdpter.ViewHolder>(), Filterable {
    var selectedItemPositionList = ArrayList<Int>()
    var searchList = ArrayList<ContactModel>()

    init {
        searchList = mList as ArrayList<ContactModel>
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ContactItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    @Throws(Exception::class)
    private fun getContactPhoto2(thumbnailUri: String): Bitmap? {
       var bitmap = MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            Uri.parse(thumbnailUri)
        )
        return bitmap

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactModel = searchList.get(position)
        with(holder) {
            with(contactModel) {
//                binding.contactImg.setImageDrawable(context.getDrawable(R.drawable  .ic_contact_profile))

                binding.txtName.text = this.contactNm
                binding.txtSize.text = this.contactNum

                if (this.contactPick.isNotEmpty() == true) {
                    this.let {
                        binding.contactImg.setImageBitmap(getContactPhoto2(it.contactPick))
                    }

                } else {
                    //getResources().getDrawable(R.drawable.imageView1);
                    binding.contactImg.setImageDrawable(context.getDrawable(R.drawable.ic_contact_profile))
                }
//                var bitmap = MediaStore.Images.Media .getBitmap(context.contentResolver, Uri.parse(this?.contactPick));
//
//                binding.contactImg.setImageBitmap(bitmap) ;

                binding.relativeMain.setOnClickListener {
                    if (!selectedItemPositionList.contains(position))
                        selectedItemPositionList.add(position)
                    else {
                        selectedItemPositionList.remove(position)
                    }
                    notifyDataSetChanged()

                    listener.getSelectMultipleCon(contactModel)
                }

                if (selectedItemPositionList.contains(position))
//                    binding.relativeMain.setBackgroundColor(R.color.card_back_color)
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#C2E9F8"))
                else
                    binding.relativeMain.setBackgroundColor(Color.parseColor("#F5F6F8"))

//                    binding.relativeMain.setBackgroundColor(R.color.card_color)
            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return searchList.size
    }

    interface ContactSelected {
        fun getSelectMultipleCon(contactModel: ContactModel)
    }

    class ViewHolder(val binding: ContactItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                searchList = if (charSearch.isEmpty()) {
                    mList as ArrayList<ContactModel>
                } else {
                    val resultList = ArrayList<ContactModel>()

                    if (mList != null) {
                        for (row in mList) {
                            Log.e("Name: ", row.contactNum)

                            if (row.contactNum.lowercase()
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
                searchList = results?.values as ArrayList<ContactModel>
                notifyDataSetChanged()
            }

        }
    }


}

