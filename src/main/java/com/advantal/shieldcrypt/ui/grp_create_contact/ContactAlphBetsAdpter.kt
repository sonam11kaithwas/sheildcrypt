package com.advantal.shieldcrypt.ui.grp_create_contact

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.ContactAplhaItemViewBinding

/**
 * Created by Sonam on 11-07-2022 16:19.
 */


class ContactAlphBetsAdpter(
    val context: Context,
    private val mList: List<String>?,val selectAlphbets: SelectAlphbets
) :


    RecyclerView.Adapter<ContactAlphBetsAdpter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ContactAplhaItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contactModel = mList?.get(position)


        with(holder) {
            with(contactModel) {
                binding.itemTextView.text = this.toString()

                binding.itemTextView.setOnClickListener(){
//                    if (selectAlphbets){
                    Log.e("TEXT",this.toString())
                        selectAlphbets.getSelectedAlphabets(this.toString())
//                    }
                }
            }
        }
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    interface SelectAlphbets {
        fun getSelectedAlphabets(alpha: String)
    }


    class ViewHolder(val binding: ContactAplhaItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


}