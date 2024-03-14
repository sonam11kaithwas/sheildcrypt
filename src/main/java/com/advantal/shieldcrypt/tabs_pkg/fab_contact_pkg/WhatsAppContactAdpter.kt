package com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.NewContactGrpLayoutBinding
import com.advantal.shieldcrypt.databinding.WhatsappContactItemViewBinding

/**
 * Created by Sonam on 15-07-2022 14:58.
 */
class WhatsAppContactAdpter(
    val context: Context,
    private var mList: List<ContactDataModel>?,
    var listener: ContactSelected,
    ) :


    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
   public var selectedItemPositionList = ArrayList<Int>()
    var contactFilterList = ArrayList<ContactDataModel>()
    var search = false

public fun clerseleItemPosi(){
    selectedItemPositionList.clear()
}
    companion object {
        val VIEW_TYPE_CONTACT = 1
        private val VIEW_TYPE_NEW_GRP_CONTACT = 2
    }

    init {
        contactFilterList = mList as ArrayList<ContactDataModel>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //        var view: View
        if (viewType == VIEW_TYPE_NEW_GRP_CONTACT) {
            val binding =
                NewContactGrpLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return NewGrpContactViewHolder(binding)
        } else {
            val binding =
                WhatsappContactItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ContactViewHolder(binding)
        }

    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ContactDataModel = contactFilterList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_NEW_GRP_CONTACT -> {
                with(holder) {
                    with(ContactDataModel) {

                        when (position) {
                            0 -> {
                                (holder as NewGrpContactViewHolder).binding.conNm.text = "New Group"
                                holder.binding.newBarcode.visibility =
                                    View.INVISIBLE
                                Glide.with(context)
                                    .load(this.image)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(holder.binding.newConGrp)
                                holder.binding.relativeMain.setOnClickListener {

                                    listener.getNewGrpraete()
                                }
                            }
//                            1 -> {
//
//                                (holder as NewGrpContactViewHolder).binding.conNm.text =
//                                    "New Contact"
//                                holder.binding.newBarcode.visibility = View.VISIBLE
//                                Glide.with(context)
//                                    .load(this.image)
//                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                    .skipMemoryCache(true)
//                                    .into(holder.binding.newConGrp)
//
//                                holder.binding.newBarcode.setOnClickListener {
//                                    listener.getQrCodeScanner()
//
//                                }
//                            }
                            else -> {

                            }
                        }
                    }
                }
            }
            VIEW_TYPE_CONTACT -> {
                with(holder) {
                    with(ContactDataModel) {
                        (holder as ContactViewHolder).binding.txtSize.visibility =
                            View.VISIBLE
                        holder.binding.barcodeImg.visibility = View.GONE
                        holder.binding.txtName.text = this.contactName.capitalize()
                        holder.binding.txtSize.text = this.mobileNumber

                        var v=this

                        if (this.image.isNotEmpty()) {
                            this.let {
                                Glide.with(context)
                                    .load(this.image)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(holder.binding.contactImg)



                                Glide.with(context)
                                    .load(this.image)
                                    .placeholder(R.drawable.one_person)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
//                                            binding.progressBar.visibility = View.GONE
                                            return false
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
//                                            binding.progressBar.visibility = View.GONE
                                            return false
                                        }

                                    })
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    // .override(1000, 1000)
                                    .into(holder.binding.contactImg)

                            }

                        } else {
                            Glide.with(context)
                                .load(context.getDrawable(R.drawable.one_person))
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(holder.binding.contactImg)
//                            holder.binding.contactImg.setImageDrawable(
//                                context.getDrawable(R.drawable.ic_contact_profile)
//                            )
                        }

                    }



                    (holder as ContactViewHolder).binding.relativeMain.setOnClickListener {

                        listener.getSelectMultipleCon(contactFilterList!![position])

                    }

                    if (selectedItemPositionList.contains(position))
                        holder.binding.relativeMain.setBackgroundColor(
                            Color.parseColor("#C2E9F8")
                        )
                    else
                        holder.binding.relativeMain.setBackgroundColor(
                            Color.parseColor("#F5F6F8")
                        )
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {

        return if (search) {
            VIEW_TYPE_CONTACT
        } else {
            if (position == 0) {
                VIEW_TYPE_NEW_GRP_CONTACT
            } else {
                // If some other user sent the message
                VIEW_TYPE_CONTACT
            }
        }
        // If the current user is the sender of the message

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return contactFilterList.size
    }

    fun listReload() {
        search = false
        contactFilterList = mList as ArrayList<ContactDataModel>
        notifyDataSetChanged()
    }

    fun addList(list: List<ContactDataModel>?) {
        contactFilterList = list as ArrayList<ContactDataModel>
        notifyDataSetChanged()
    }

    interface ContactSelected {
        fun getSelectMultipleCon(contactDataModel: ContactDataModel)
        fun getQrCodeScanner()
        fun getNewGrpraete()
    }

    class ContactViewHolder(val binding: WhatsappContactItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    class NewGrpContactViewHolder(val binding: NewContactGrpLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                contactFilterList = if (charSearch.isEmpty()) {
                    mList as ArrayList<ContactDataModel>
                } else {
                    val resultList = ArrayList<ContactDataModel>()
                    for (row in mList!!) {
                        if (row.contactName.lowercase().contains(
                                constraint.toString().lowercase()
                            ) || row.mobileNumber.contains(constraint.toString())
                        ) {
                            resultList.add(row)
                        }
                    }
                    search = true
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = contactFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                contactFilterList = results?.values as ArrayList<ContactDataModel>
                notifyDataSetChanged()
            }
        }
    }

}

