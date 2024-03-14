package com.advantal.shieldcrypt.tabs_pkg.adpter_pkg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advantal.shieldcrypt.databinding.SettingListBinding
import com.advantal.shieldcrypt.tabs_pkg.model.SettingsDataModel

/**
 * Created by Sonam on 17-06-2022 14:07.
 */
class SettingsAdapter(
    private val mList: List<SettingsDataModel>,
    val settingCallBack: ChatSelectCallBack
) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsAdapter.ViewHolder {
        val binding =
            SettingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SettingsAdapter.ViewHolder(binding)
    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val myDataModel = mList[position]


        with(holder) {
            with(myDataModel) {

                when (position) {
                     4 -> {
                        binding.nameLable.visibility = View.GONE

                        binding.arrow.visibility = View.GONE
//                        binding.switchBtn.visibility = View.VISIBLE
                        binding.switchBtn.visibility = View.GONE
                    }
                    0 -> {
                        binding.switchBtn.visibility = View.GONE

                        binding.nameLable.visibility = View.VISIBLE
                        binding.nameLable.text = "Account settings"
                    }
                    2 -> {
                        binding.switchBtn.visibility = View.GONE
                        binding.nameLable.visibility = View.VISIBLE
                        binding.nameLable.text = "App settings"
                    }
                    else -> {
                        binding.nameLable.visibility = View.GONE
                        binding.arrow.visibility = View.VISIBLE
                        binding.switchBtn.visibility = View.GONE
                    }
                }
                binding.name.text = this.mName
                binding.arrow.setImageResource(this.mArrow)
                binding.img.setImageResource(this.mImg)
                binding.settingViewLayout.setOnClickListener {
                    settingCallBack.getSelectedUser(position)
                }
            }
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    interface ChatSelectCallBack {
        fun getSelectedUser(position: Int)
    }

    //    // Holds the views for adding it to image and text
//    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val arrowImage: ImageView = itemView.findViewById(R.id.arrow)
//        val imageView: ImageView = itemView.findViewById(R.id.img)
//        val name: TextView = itemView.findViewById(R.id.name)
////        val relativeLayout: RelativeLayout = itemView.findViewById(R.id.relativeLayout)
//
//
//    }
    class ViewHolder(val binding: SettingListBinding) : RecyclerView.ViewHolder(binding.root)

}