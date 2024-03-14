package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.gallery_folder_pkg

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.advantal.shieldcrypt.databinding.GalleryFolderLayoutBinding
import com.advantal.shieldcrypt.utils_pkg.AppConstants

/**
 * Created by Sonam on 08-07-2022 13:34.
 */
class GalleryImageFolderAdapter(
    val context: Context,
    var listener: GalleryFolderSelect,
    var galleryFolderActivity: Activity

) :
    RecyclerView.Adapter<GalleryImageFolderAdapter.ViewHolder>() {
    val folderNameList: ArrayList<String> = arrayListOf<String>()
    var arrMap: HashMap<String, ArrayList<String>> = HashMap()

    init {
        populateMap()
    }

    private fun populateMap() {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)

        val selection =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE

        val cursorLoader = CursorLoader(
            context, MediaStore.Files.getContentUri("external"),
            columns,
            selection,
            null,
            MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        )

        val cursor = cursorLoader.loadInBackground()
        val count = cursor!!.count

        for (i in 0 until count) {
            cursor.moveToPosition(i)
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            val filePath = cursor.getString(dataColumnIndex)
            val folderList: MutableList<String> = java.util.ArrayList()
            var name = ""
            for (j in 1 until filePath.length) {
                if (filePath[j] == '/') {
                    folderList.add(name)
                    name = ""
                } else {
                    name += filePath[j]
                }
            }
            val folderName = folderList[folderList.size - 1]
            if (arrMap.containsKey(folderName)) {
                arrMap[folderName]?.add(filePath)
            } else {
                arrMap[folderName] = java.util.ArrayList()
                arrMap[folderName]?.add(filePath)
                folderNameList.add(folderName)
            }
        }

    }

    var selectedItemPositionList = ArrayList<Int>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            GalleryFolderLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filesOfFolder: List<String>? = arrMap.get(folderNameList[position])

        with(holder) {
            with(folderNameList[position]) {
                binding.folderName.text = " " +this.toString()
                binding.folderItemCount.text = filesOfFolder?.size.toString()


                if (position > AppConstants.CACHE_LIMIT) {
                    Glide.with(context).load(filesOfFolder?.get(0))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(1000, 1000)
                        .into(binding.thumbnailImage)
                } else {
                    Glide.with(context).load(filesOfFolder?.get(0))
                        .override(1000, 1000)
                        .into(binding.thumbnailImage)
                }


                binding.thumbnailImage.setOnClickListener {
                    var intent =
                        Intent(context.applicationContext, GalleryImageActivity::class.java)
                    intent.putStringArrayListExtra(
                        "pathlist",
                        filesOfFolder as java.util.ArrayList<String>?
                    )
                    galleryFolderActivity.startActivity(intent)
                }
            }
        }


    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        return folderNameList.size
    }

    interface GalleryFolderSelect {
        fun getSelectGalleryFolder()
    }

    class ViewHolder(val binding: GalleryFolderLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)


}