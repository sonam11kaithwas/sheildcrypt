package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ContactItemViewBinding
import com.advantal.shieldcrypt.ui.model.UsersItem
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import database.my_database_pkg.db_table.MyAppDataBase

class GroupUsertListAdpter(
    val context: Context, var searchList: MutableList<UsersItem>?, var listener: UserSelected
) :


    RecyclerView.Adapter<GroupUsertListAdpter.ViewHolder>() {


    init {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        val binding =
            ContactItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    fun updateGrpUserDataList(searchList: MutableList<UsersItem>?) {
        this.searchList = searchList
        notifyDataSetChanged()
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactModel = searchList?.get(position)
        with(holder) {
            with(contactModel) {

                binding.txtName.text = this?.username ?: ""
                binding.txtSize.text = this?.mobileNumber ?: ""


                holder.binding.contactImg.setImageDrawable(
                    context.resources.getDrawable(R.drawable.one_person, null)
                )

                var img = ""
                try {
                    img = MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                        ?.contactDao()?.getUserProfilePick(this?.mobileNumber ?: "")!!
                } catch (e: Exception) {
                }
                if (img != null && !img.equals("")) {
                    this.let {
                        Glide.with(context).load(img).diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).into(holder.binding.contactImg)
                    }

                } else {

                    Glide.with(context).load(holder.binding.contactImg)
                        .diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                        .into(holder.binding.contactImg)
                }



                binding.relativeMain.setOnClickListener {
                    (searchList?.get(position) ?: null)?.let { it1 ->
                        listener.getSelectMultipleCon(
                            it1
                        )
                    }
                }
            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return searchList?.size ?: 0
    }

    interface UserSelected {
        fun getSelectMultipleCon(contactModel: UsersItem)
    }

    class ViewHolder(val binding: ContactItemViewBinding) : RecyclerView.ViewHolder(binding.root)


}

