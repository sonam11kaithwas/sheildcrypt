package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.gallery_folder_pkg

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityGalleryBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity
import com.advantal.shieldcrypt.utils_pkg.MyApp
import java.io.File
import java.io.FileOutputStream

class GalleryFolderActivity : AppCompatActivity(), View.OnClickListener,
    GalleryImageFolderAdapter.GalleryFolderSelect {
    var adapter: GalleryImageFolderAdapter? = null

    lateinit var binding: ActivityGalleryBinding

    private fun setDataInViews() {
        adapter = GalleryImageFolderAdapter(applicationContext, this, this)

        binding.recycleGalleryFolder.layoutManager = GridLayoutManager(this, 2)
        binding.recycleGalleryFolder.adapter = adapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setDataInViews()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
            onBackPressed()
            }
        }
    }

    override fun getSelectGalleryFolder() {
         MyApp.getAppInstance().showToastMsg("Selected")
    }

    override fun onBackPressed() {

        startActivity(Intent(this@GalleryFolderActivity,ChatsActivity::class.java))
        this.finish()
    }
}