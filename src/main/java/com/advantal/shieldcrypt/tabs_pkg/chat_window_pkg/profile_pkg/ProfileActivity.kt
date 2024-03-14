package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityProfileBinding
import com.advantal.shieldcrypt.auth_pkg.model.DeleteProfilePhotoModel
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.advantal.shieldcrypt.cropping_filter.ui.CropFilterActivity
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.ProfilePictureUpload
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity.MyObject.croppedBitmap
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.profilemodel.ProfileReqModel
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.profilemodel.ProfileUpdateReqModel
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.ProfileAboutActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.Data
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.InvListItem
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.SettingFragment.MyObject.profileChangedStatus
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.UploadCallBack
import com.advantal.shieldcrypt.utils_pkg.*
import com.advantal.shieldcrypt.utils_pkg.AppConstants.Companion.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
import dagger.hilt.android.AndroidEntryPoint
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_MEDIA
import droidninja.filepicker.FilePickerConst.SPAN_TYPE
import java.io.File


@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(),UploadCallBack {
    val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityProfileBinding
    var photoFileName = "photo.jpg"
    val APP_TAG = "ShieldCrypt"
    private var photoFile: File? = null
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        viewProfile(1)


        binding.updateProfile.setOnClickListener {
            viewProfile(2)
        }

        binding.icBackarrow.setOnClickListener {
            onBackPressed()
        }

        binding.imgPickerBtn.setOnClickListener {

            withItems()
        }

        binding.editAbout.setOnClickListener {
            val intent = Intent(this, ProfileAboutActivity::class.java)
            startForResult.launch(intent)
        }



        mainViewModel.responceCallBack.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.VIEW_PROFILE_BY_ID_REQ -> {
                                model = Gson().fromJson(users.data, LoginResModel::class.java)
                                model.token =
                                    MySharedPreferences.getSharedprefInstance().getLoginData().token
                                MySharedPreferences.getSharedprefInstance()
                                    .setLoginData(Gson().toJson(model))

                                MySharedPreferences.getSharedprefInstance()
                                    .setChatip(model.xmpip.toString())


                                binding.nameEdt.setText(model.firstName ?: "")
                                binding.lastnameEdt.setText(model.lastName ?: "")
                                binding.phoneNumEdt.setText(model.mobileNumber)

                                if (model.profileUrl != null && !model.profileUrl.isEmpty()) {
                                    binding.progressBar.visibility = View.VISIBLE

                                    Glide.with(this).load(model.profileUrl)
                                        .placeholder(R.drawable.one_person)
                                        .listener(object : RequestListener<Drawable> {
                                            override fun onLoadFailed(
                                                e: GlideException?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                binding.progressBar.visibility = View.GONE
                                                return false
                                            }

                                            override fun onResourceReady(
                                                resource: Drawable?,
                                                model: Any?,
                                                target: com.bumptech.glide.request.target.Target<Drawable>?,
                                                dataSource: DataSource?,
                                                isFirstResource: Boolean
                                            ): Boolean {
                                                binding.progressBar.visibility = View.GONE
                                                return false
                                            }

                                        }).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        // .override(1000, 1000)
                                        .into(binding.userImg)
                                }
                                defaultStatuslist()
                            }
                            RequestApis.UPDATE_PROFILE_REQ -> {
                                profileChangedStatus = true
                            }
                            RequestApis.UPLOAD_PROFILE_PHOTO -> {

                            }
                            RequestApis.DELETE_PROFILE -> {
                                binding.userImg.setImageResource(R.drawable.one_person)
                            }
                            RequestApis.DEFAULT_STATUS_LIST -> {

                                var statusAboutResModel = Gson().fromJson(it.data.data, Data::class.java)
                                var strFinalList = ArrayList<InvListItem>()
                                strFinalList.addAll(statusAboutResModel.invList!!)
                                for (i in strFinalList.indices) {
                                    if(strFinalList[i].active == true){
                                        binding.aboutStatusEdt.setText(strFinalList[i].defaultstatus?.name.toString())
                                        break
                                    } else{
                                        binding.aboutStatusEdt.setText("")
                                    }
                                }

                            }




                        }
                    }

                }
                Status.LOADING -> {
                    AppUtills.setProgressDialog(this)
                }
                Status.ERROR -> {
                    MyApp.getAppInstance().showToastMsg(it.message.toString())
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        try {
            if (croppedBitmap != null && !croppedBitmap.isEmpty()) {
                val myUri = Uri.parse(croppedBitmap)
                //   val selectedFilePath = File(myUri.toString())
                //   Log.e("myFile", " selectedFilePath->> " + selectedFilePath)
                try {
                    val selectedFilePath: String? = FilePath().getPath(this, myUri)
                    val file = File(selectedFilePath)
                    Log.e(
                        "myFile",
                        " selectedFilePath->> " + selectedFilePath + "  file-> " + file + "  name-> " + file.name
                    )
                    uploadPhoto(file)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                croppedBitmap = ""
                Glide.with(this).load(myUri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).override(1000, 1000).into(binding.userImg)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {

        }
    }

    fun uploadPhoto(file: File) {
        AppUtills.setProgressDialog(this)
        ProfilePictureUpload().ProfilePhotoUpload(this@ProfileActivity, file, model.username
        ,RequestApis.updateUserProfilePhoto,"")
    }

    fun withItems() {
        val items =
            arrayOf(resources.getString(R.string.camera), resources.getString(R.string.gallery),resources.getString(R.string.remove))
        val builder = AlertDialog.Builder(this)
        with(builder) {
            setTitle(resources.getString(R.string.select_photo))
            setItems(items) { dialog, which ->
                //Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                if (which == 0) {
                    checkAndroidVersionForRuntimePermission(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA
                    )
                } else if (which == 1) {
                    checkAndroidVersionForRuntimePermission(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
                    )
                }
                else if(which==2){
                    deleteProfile()
                }
            }
            // setPositiveButton("OK", positiveButtonClick)
            show()
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                lunchGalleryListener.launch(intent)
            }
        }
    }

    private var lunchGalleryListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data: Intent? = result.data
                Log.e("imagePath", "  data->> $data" + "  -> " + data?.data)
                try {
                    if (data != null && data.data != null) {

                        val intent = Intent(this, CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", data.data.toString())
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
                    Log.e("Camera", e.toString())
                }

            }
        }

    private fun lunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFileName = System.currentTimeMillis().toString() + ".jpg"
        photoFile = getPhotoFileUri(photoFileName)
        val fileProvider = photoFile?.let {
            FileProvider.getUriForFile(
                this@ProfileActivity, BuildConfig.APPLICATION_ID + ".fileprovider", it
            )
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (intent.resolveActivity(packageManager) != null) {
            // startActivityForResult(intent, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA)
            lunchCameraListner.launch(intent)
        }
    }

    var lunchCameraListner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.e("imagePath", "  ->> $photoFile")
                try {
                    if (photoFile != null) {
                        val takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName))
                        val intent = Intent(this, CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", takenPhotoUri.toString())
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
                    Log.e("Camera", e.toString())
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> if (resultCode == RESULT_OK && data != null) {
                val dataList = data.getParcelableArrayListExtra<Uri>(KEY_SELECTED_MEDIA)
                if (dataList != null && dataList.size > 0 && dataList.get(0) != null) {
                    try {
                        val intent = Intent(this, CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", dataList.get(0).toString())
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show()
                        Log.e("Camera", e.toString())
                    }
                } else {
                    // show this if no image is selected
                    Toast.makeText(this, "You haven't picked File", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun getPhotoFileUri(fileName: String): File? {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun checkAndroidVersionForRuntimePermission(
        arrayOfPermission: Array<String>, runtimePermissionRequestCode: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(arrayOfPermission)) {
                when (runtimePermissionRequestCode) {
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                        lunchCamera()
                    }
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                        // openGallery()
                        onPickPhoto()
                    }
                }
            } else requestPermission(arrayOfPermission)

        } else {
            when (runtimePermissionRequestCode) {
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                    lunchCamera()
                }
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                    onPickPhoto()
                }
            }
        }
    }

    fun onPickPhoto() {
        FilePickerBuilder.instance.setMaxCount(1)
            //.setSelectedFiles(photoPaths)
            .setActivityTheme(R.style.FilePickerTheme)//R.style.FilePickerTheme
            .setActivityTitle("Please select media").setImageSizeLimit(5).setVideoSizeLimit(10)
            .setSpan(SPAN_TYPE.FOLDER_SPAN, 3).setSpan(SPAN_TYPE.DETAIL_SPAN, 4)
            .enableVideoPicker(false).enableCameraSupport(false).showGifs(false)
            .showFolderView(true).enableSelectAll(true).enableImagePicker(true)
            // .setCameraPlaceholder(R.drawable.ic_pick_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(this, RUNTIME_PERMISSION_REQUEST_CODE_GALLERY)
    }

    private fun checkPermission(permissions: Array<String>): Boolean {

        var result = 0
        if (permissions != null && permissions.isNotEmpty()) {
            for (i in permissions.indices) {
                result += ContextCompat.checkSelfPermission(
                    applicationContext, permissions[i]
                )
            }
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            this@ProfileActivity, permissions, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
        )
    }

    private fun viewProfile(i: Int) {

        when (i) {
            1 -> {
                binding.updateProfile.visibility = View.VISIBLE
                binding.updateBtn.visibility = View.GONE
                binding.lastnameLayout.visibility = View.GONE
                binding.lastTxt.visibility = View.GONE
                binding.nameEdt.isCursorVisible = false
                binding.phoneNumEdt.isFocusable = false
                binding.toolbar.text = resources.getString(R.string.profile_view)
                binding.phoneLayout.setBackgroundDrawable(resources.getDrawable(R.drawable.background_phone_view))

                mainViewModel.featchDataFromServerWithAuth(
                    Gson().toJson(
                        ProfileReqModel(
                            (MySharedPreferences.getSharedprefInstance()
                                .getLoginData().userid).toInt()
                        )
                    ), RequestApis.view_Profile_ById, RequestApis.VIEW_PROFILE_BY_ID_REQ
                )
            }
            2 -> {
                binding.updateBtn.setOnClickListener {
                    if (binding.nameEdt.text.toString().trim().equals("")) {
                        MyApp.getAppInstance()
                            .showToastMsg(resources.getString(R.string.please_enter_first_name))
                    } else if (binding.lastnameEdt.text.toString().trim().equals("")) {
                        MyApp.getAppInstance()
                            .showToastMsg(resources.getString(R.string.please_enter_last_name))
                    } else {
                        mainViewModel.featchDataFromServerWithAuth(
                            Gson().toJson(
                                ProfileUpdateReqModel(
                                    (MySharedPreferences.getSharedprefInstance()
                                        .getLoginData().userid).toInt(),
                                    binding.nameEdt.text.toString().trim(),
                                    binding.lastnameEdt.text.toString().trim()
                                )
                            ), RequestApis.updateProfile, RequestApis.UPDATE_PROFILE_REQ
                        )
                    }
                }
                binding.updateProfile.visibility = View.GONE
                binding.toolbar.text = resources.getString(R.string.profile_edit)
                binding.nameEdt.isCursorVisible = true
                binding.nameEdt.isFocusable = true
                binding.phoneNumEdt.setBackgroundDrawable(resources.getDrawable(R.drawable.button_rectangle_grey))
                binding.updateBtn.visibility = View.VISIBLE
                binding.lastTxt.visibility = View.VISIBLE
                binding.phoneLayout.setBackgroundDrawable(resources.getDrawable(R.drawable.disable_phone_lay))
                binding.lastnameLayout.visibility = View.VISIBLE
                binding.editAbout.visibility = View.GONE
                binding.statusLayout.visibility = View.GONE
            }
        }
    }


    private fun defaultStatuslist() {
        mainViewModel.getDataFromAutoQueryServerWithAuth(
            model.userid.toString().toInt(), RequestApis.default_status_list, RequestApis.DEFAULT_STATUS_LIST
        )
    }

    object MyObject {
        @kotlin.jvm.JvmField
        var croppedBitmap: String = ""
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    var str = data?.getStringExtra("aboutprofile")

                    Log.e(": 1111=>>>", str.toString())

                    if(str!!.isNotEmpty()){

                        binding.aboutStatusEdt.setText(str)
                        AppConstants.MYSTATUS = str.toString()




                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    private fun deleteProfile() {
        mainViewModel.deleteFromAutoQueryServerWithAuth(
            Gson().toJson(
                DeleteProfilePhotoModel(model.username)
            ),
            RequestApis.delete_profile, RequestApis.DELETE_PROFILE
        )
    }

    override fun imageUploadCallBack(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.one_person)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            // .override(1000, 1000)
            .into(binding.userImg)

    }
}