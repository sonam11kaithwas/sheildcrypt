package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.gallery_folder_pkg

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.GalaryItemViewBinding
import com.advantal.shieldcrypt.utils_pkg.AppConstants

/**
 * Created by Sonam on 08-07-2022 16:57.
 */
class GalleryImageAdpter(
    val context: Context,
    var listener: GalleryImageSelect,
    private val imagePathList: List<String>,
) :

    RecyclerView.Adapter<GalleryImageAdpter.ViewHolder>() {

    val selectionPath: ArrayList<String> = arrayListOf<String>()
    var MaxSelectionLimit = 30
    private var selecteDeselct = false

    val selectedState = BooleanArray(imagePathList.size) // create IntArray via constructor

    init {
        java.util.Arrays.fill(selectedState, false)
    }


    var selectedItemPositionList = ArrayList<Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            GalaryItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var folderNm = imagePathList[position]
        var pos = position
        with(holder) {
            with(folderNm) {
                if (position > AppConstants.CACHE_LIMIT) {
                    Glide.with(context).load(folderNm)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(1000, 1000)
                        .into(binding.thumbnailImage)
                } else {
                    Glide.with(context).load(folderNm)
                        .override(1000, 1000)
                        .into(binding.thumbnailImage)
                }

                if (selecteDeselct) {
                    // A tick is displayed over the image
                    binding.imgTick.setImageResource(R.drawable.ic_selecte_tick)

                    // opacity is set to 60%
                    binding.imgTick.setBackgroundColor(Color.parseColor("#99000000"))
                } else {
                    binding.imgTick.setImageResource(0)
                    binding.imgTick.setBackgroundResource(0)
                }

                binding.thumbnailImage.setOnClickListener {
                    if (selectionPath.size > 0) {
                        setState(pos, binding.imgTick)
                    } else {
                        Log.e("pos", "" + pos + "")
                    }
                }


                binding.thumbnailImage.setOnLongClickListener {
                    Log.e("pos", "" + pos + "")
                    return@setOnLongClickListener setState(pos, binding.imgTick)
                }

            }
        }
    }


    fun selectDeselctAllImages(select: Boolean) {
        selecteDeselct = if (select) {
            selectionPath.addAll(imagePathList)
            select
        } else {
            selectionPath.clear()
            select
        }

        notifyDataSetChanged()
        listener.getSelectGalleryImage(selectionPath)


    }

    private fun setState(position: Int, isSelected: ImageView): Boolean {

        // When this position is not selected in the selectedState Array
        if (!selectedState[position]) {

            // Number of selections is more than max
            if (selectionPath.size == MaxSelectionLimit) {
                Toast.makeText(context, "Can't select more than 10", Toast.LENGTH_SHORT).show()
                return true
            }

            // path of the current Image is added to the List
            selectionPath.add(imagePathList[position])

            // A tick is displayed over the image
            isSelected.setImageResource(R.drawable.ic_selecte_tick)

            // opacity is set to 60%
            isSelected.setBackgroundColor(Color.parseColor("#99000000"))
        } else {

            // path is removed from the selectionPath array
            selectionPath.remove(imagePathList[position])

            // Styling is restored
            isSelected.setImageResource(0)
            isSelected.setBackgroundResource(0)
        }

        // The GalleryViewFragment is notified of all the changes in selectionPath array
        listener.getSelectGalleryImage(selectionPath)

        // state is toggled in the selectedState Array
        selectedState[position] = !selectedState[position]// = !selectedState[position]
        return true
    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return imagePathList.size
    }

    interface GalleryImageSelect {
        fun getSelectGalleryImage(selectionPath: ArrayList<String>)
    }

    class ViewHolder(val binding: GalaryItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


}