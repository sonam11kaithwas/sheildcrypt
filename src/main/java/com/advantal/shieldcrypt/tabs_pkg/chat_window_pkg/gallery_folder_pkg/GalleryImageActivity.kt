package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.gallery_folder_pkg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityGalleryImageBinding
import com.advantal.shieldcrypt.utils_pkg.MyApp
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class GalleryImageActivity : AppCompatActivity(), GalleryImageAdpter.GalleryImageSelect,
    View.OnClickListener {
    lateinit var binding: ActivityGalleryImageBinding
    var adapter: GalleryImageAdpter? = null
    var pathList: ArrayList<String> = arrayListOf<String>()
    private var selecteDeselct = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pathList = intent.getStringArrayListExtra("pathlist")!!

        setDataInViews()
    }

    override fun onBackPressed() {

        this.finish()
    }

    private fun setDataInViews() {
        adapter = GalleryImageAdpter(applicationContext, this, pathList)

        binding.recycleGalleryImage.layoutManager = GridLayoutManager(this, 3)
        binding.recycleGalleryImage.adapter = adapter
    }

    override fun getSelectGalleryImage(selectionPath: ArrayList<String>) {

        if (selectionPath.size > 0) {
            binding.txtSend.visibility = View.VISIBLE
            var bitmap = BitmapFactory.decodeFile(selectionPath[0])

            saveToInternalStorage(bitmap)

        } else {
            binding.txtSend.visibility = View.GONE
        }

    }


    /*************Save gallery image in internal storage***************/
    private fun saveToInternalStorage(bitmapImage: Bitmap) {
        val direct = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + File.separator + "ShieldCrypt/images"
        )

        //If File is not present create directory
        if (!direct.exists()) {
            direct.mkdir()
            Log.e("TAG", "Directory Created.")
        }

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
        val now = Date()
        val fileName =  formatter.format(now).toString() + ".png"



        var file = File(direct, fileName)



        try {
            //Create New File if not present
            if (!file.exists()) {
                file.getParentFile()?.mkdirs()
                Log.e("TAG", "File Created")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }



        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 70, fos)
            MyApp.getAppInstance().showToastMsg("Store")
        } catch (c: Exception) {
            c.message
            MyApp.getAppInstance().showToastMsg(c.message!!)

        } finally {
            fos?.flush()
            fos?.close()
        }
    }



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                this.finish()
            }
            R.id.txt_send -> {
                MyApp.getAppInstance().showToastMsg("Upload Images")
            }
            R.id.select_all_img -> {
                selecteDeselct = if (!selecteDeselct) {
                    adapter?.selectDeselctAllImages(!selecteDeselct)
                    true
                } else {
                    adapter?.selectDeselctAllImages(!selecteDeselct)
                    false
                }
            }
        }
    }


}

