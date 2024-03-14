package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

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
import com.advantal.sheildcrypt.network_pkg.StatusPictureUpload
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityStatusStoryBinding
import com.advantal.shieldcrypt.cropping_filter.ui.CropFilterActivity
import com.advantal.shieldcrypt.network_pkg.*
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.StoryAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity
import com.advantal.shieldcrypt.tabs_pkg.model.*
import com.advantal.shieldcrypt.tabs_pkg.model.UserDataModel
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.data.entity.MiUserStoryModel
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.ui.activity.MiStoryDisplayActivity
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.util.MiMediaType
import com.advantal.shieldcrypt.utils_pkg.*
import dagger.hilt.android.AndroidEntryPoint
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.log10

@AndroidEntryPoint
class StatusStoryActivity : AppCompatActivity(), UploadCallBack {
    lateinit var binding: ActivityStatusStoryBinding

    @Inject
    lateinit var networkHelper: NetworkHelper
    private lateinit var storyAdapter: StoryAdapter
    private val mainViewModel: MainViewModel by viewModels()
    private var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    private var photoFileName = "photo.jpg"
    private val APP_TAG = "ShieldCrypt"
    private var photoFile: File? = null
    private val mListOfUsers: ArrayList<MiUserStoryModel> = ArrayList()
    private var comingFromTextScreen = false
    private var dataMyStatus = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myStatusLay.setOnClickListener {
            if (dataMyStatus) {
                val intent = Intent(this, ActivityFragment::class.java)
                intent.putExtra("comingFrom", "MyStatusLayout")
                this.startActivity(intent)
            } else {
                withItems()
            }

        }

        binding.textstatusStory.setOnClickListener {
            val intent = Intent(this, ActivityFragment::class.java)
            intent.putExtra("comingFrom", "TextStatusStory")
            startForResult.launch(intent)
        }
        myStatusList()
        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.MY_STATUS_LIST_All -> {
                                    try {
                                        if (!comingFromTextScreen) {
                                            getAllStatus()
                                        } else {
                                            comingFromTextScreen = false
                                        }
                                        if (users.data != "{}") {
                                            binding.imgPickerBtn.visibility = View.GONE
                                            binding.msgView.visibility = View.VISIBLE
                                            dataMyStatus = true
                                            val listType =
                                                object :
                                                    TypeToken<List<MyStatusResponse?>?>() {}.type
                                            val contactDataList: List<MyStatusResponse> =
                                                Gson().fromJson(
                                                    users.data,
                                                    listType
                                                )
                                            contactDataList[0].mediadetails?.let { it1 ->
                                                setMyStatusByUrl(
                                                    it1
                                                )
                                                if (contactDataList[0].count!! > 1) {
                                                    binding.msgView.text =
                                                        "${contactDataList[0].count!!} views"
                                                } else {
                                                    binding.msgView.text =
                                                        "${contactDataList[0].count!!} view"
                                                }
                                            }
                                        } else {
                                            dataMyStatus = false
                                            binding.imgPickerBtn.visibility = View.VISIBLE
                                            binding.msgView.visibility = View.GONE
                                            setMyStatusByUrl(
                                                model.profileUrl
                                            )
                                        }
                                    } catch (_: Exception) {
                                    }
                                }

                                RequestApis.GET_ALL_MEDIA_STATUS -> {
                                    val dataType =
                                        object :
                                            TypeToken<ArrayList<GetAllMediaStatusResponse?>?>() {}.type
                                    val dataResponse =
                                        Gson().fromJson(
                                            it.data.data,
                                            dataType
                                        ) as ArrayList<GetAllMediaStatusResponse>
                                    val mainList: ArrayList<UserDataModel> = ArrayList()
                                    for (i in dataResponse.indices) {
                                        val dto = dataResponse[i]
                                        val userDTO = UserDataModel()
                                        userDTO.userName = dto.username.toString()
                                        userDTO.id = dto.userid.toString()

                                        val storyDataList: ArrayList<StoryDataModel> =
                                            ArrayList()

                                        for (j in dto.userStoryList!!.indices) {
                                            val storyDTO = dto.userStoryList!![j]
                                            val miStoryModel = StoryDataModel()
                                            miStoryModel.statusId = storyDTO.statusid
                                            miStoryModel.name = storyDTO.username
                                            miStoryModel.time =
                                                convertFBTime(storyDTO.statuscreationdatetime)
                                            miStoryModel.mediaUrl = storyDTO.mediastatusdetails
                                            miStoryModel.isStorySeen = storyDTO.viewstatus!!
                                            if (storyDTO.mediastatusdetails!!.uppercase()
                                                    .endsWith(".PNG") || storyDTO.mediastatusdetails!!.uppercase()
                                                    .endsWith(".JPG")
                                            ) {
                                                miStoryModel.mediaType = MiMediaType.IMAGE
                                                miStoryModel.isMediaTypeVideo = false
                                            } else {
                                                miStoryModel.mediaType = MiMediaType.VIDEO
                                            }
                                            storyDataList.add(miStoryModel)
                                            userDTO.userStoryList = storyDataList
                                        }
                                        mainList.add(userDTO)
                                    }
                                    initView(mainList)
                                }

                                RequestApis.OTHER_SAVE_STATUS -> {

                                }
                                else -> {}
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

        binding.imgPickerBtn.setOnClickListener {
            withItems()
        }
        binding.cameraicon.setOnClickListener {
            withItems()
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            try {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val str = data?.getStringExtra("comingFrom")
                    if (str == "ActivityFragment") {
                        myStatusList()
                        comingFromTextScreen = true
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                if (it.resultCode == Activity.RESULT_OK) {
                    val list = arrayListOf<MiUserStoryModel>()
                    val userStoryIndex =
                        it.data?.getIntExtra(MiStoryDisplayActivity.INDEX_OF_SELECTED_STORY, 0)
                    it.data?.hasExtra(MiStoryDisplayActivity.MI_LIST_OF_STORY)
                        ?.let { hasMiStoryList ->
                            if (hasMiStoryList) {
                                it.data?.getParcelableArrayListExtra<MiUserStoryModel>(
                                    MiStoryDisplayActivity.MI_LIST_OF_STORY
                                )?.let { listOfUserStories ->
                                    list.addAll(listOfUserStories)
                                }
                            }
                        }

                    if (!mListOfUsers.containsAll(list)) {
                        storyAdapter.setUserStoryData(list)
                        mListOfUsers.addAll(list)
                        val statusListSeen = mListOfUsers[userStoryIndex!!].userStoryList
                        val userSeenList = arrayListOf<UserSeenList>()
                        for (i in statusListSeen.indices) {
                            if (statusListSeen[i].isStorySeen) {
                                val dto = UserSeenList()
                                dto.userName = statusListSeen[i].name
                                dto.statusid = statusListSeen[i].statusId!!
                                dto.seen = true
                                userSeenList.add(dto)
                            }
                        }

                        mainViewModel.featchDataFromServerWithAuthArray(
                            Gson().toJson(userSeenList),
                            RequestApis.other_save_status,
                            RequestApis.OTHER_SAVE_STATUS
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    private fun initView(mainList: java.util.ArrayList<UserDataModel>) {
        val listType =
            object : TypeToken<List<MiUserStoryModel?>?>() {}.type

        val li = Gson().fromJson<List<MiUserStoryModel>>(
            Gson().toJson(mainList),
            listType
        ) as ArrayList<MiUserStoryModel>

        mListOfUsers.addAll(li)
        with(binding.rvStory) {
            setHasFixedSize(true)
            ContextCompat.getDrawable(this@StatusStoryActivity, R.drawable.divider)
                ?.let { DividerItemDecorator(it) }?.let {
                    addItemDecoration(it)
                }
            storyAdapter = StoryAdapter(mListOfUsers, { launcher }, { this@StatusStoryActivity })
            adapter = storyAdapter
            addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                startPostponedEnterTransition()
            }
        }

    }

    private fun myStatusList() {
        val hashMap: HashMap<String, Int> = HashMap<String, Int>()
        hashMap["userid"] = model.userid.toInt()
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(hashMap),
            RequestApis.get_all_media_status_list,
            RequestApis.MY_STATUS_LIST_All
        )
    }

    private fun getAllStatus() {
        val hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(hashMap),
            RequestApis.get_all_media_status,
            RequestApis.GET_ALL_MEDIA_STATUS
        )
    }

    private fun withItems() {
        val items =
            arrayOf(resources.getString(R.string.camera), resources.getString(R.string.gallery))
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
            }
            // setPositiveButton("OK", positiveButtonClick)
            show()
        }
    }

    private fun checkPermission(permissions: Array<String>): Boolean {

        var result = 0
        if (permissions != null && permissions.isNotEmpty()) {
            for (i in permissions.indices) {
                result += ContextCompat.checkSelfPermission(
                    this,
                    permissions[i]
                )
            }
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            this, permissions, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
        )
    }

    private fun checkAndroidVersionForRuntimePermission(
        arrayOfPermission: Array<String>,
        runtimePermissionRequestCode: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(arrayOfPermission)) {
                when (runtimePermissionRequestCode) {
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                        lunchCamera()
                    }
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                        onPickPhoto()
                        // onPickPhoto()
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

    private fun getPhotoFileUri(fileName: String): File? {
        val mediaStorageDir =
            File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun lunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFileName = System.currentTimeMillis().toString() + ".jpg"
        photoFile = getPhotoFileUri(photoFileName)
        val fileProvider = photoFile?.let {
            FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                it
            )
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (this.packageManager != null) {
            // startActivityForResult(intent, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA)
            lunchCameraListner.launch(intent)
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            this.packageManager?.also {
                lunchGalleryListener.launch(intent)
            }
        }
    }

    private fun onPickPhoto() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            //.setSelectedFiles(photoPaths)
            .setActivityTheme(R.style.FilePickerTheme)//R.style.FilePickerTheme
            .setActivityTitle("Please select media")
            .setImageSizeLimit(5)
            .setVideoSizeLimit(10)
            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
            .enableVideoPicker(false)
            .enableCameraSupport(false)
            .showGifs(false)
            .showFolderView(true)
            .enableSelectAll(true)
            .enableImagePicker(true)
            // .setCameraPlaceholder(R.drawable.ic_pick_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(this, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY)
    }

    private var lunchGalleryListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val data: Intent? = result.data
                Log.e("imagePath", "  data->> $data" + "  -> " + data?.data)
                try {
                    if (data != null && data.data != null) {
//                        Glide.with(this).load(data.data)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .skipMemoryCache(true)
//                            .override(1000, 1000)
//                            .into(binding.img)

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


    var lunchCameraListner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.e("imagePath", "  ->> $photoFile")
                try {
                    if (photoFile != null) {
                        val takenPhotoUri = Uri.fromFile(getPhotoFileUri(photoFileName))
//                        Glide.with(this).load(photoFile)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .skipMemoryCache(true)
//                            .override(1000, 1000)
//                            .into(binding.img)

                        val intent = Intent(this, CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", takenPhotoUri.toString())
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("Camera", e.toString())
                }

            }
        }


    override fun onResume() {
        super.onResume()
        try {
            if (ProfileActivity.MyObject.croppedBitmap != null && !ProfileActivity.MyObject.croppedBitmap.isEmpty()) {
                val myUri = Uri.parse(ProfileActivity.MyObject.croppedBitmap)
                //   val selectedFilePath = File(myUri.toString())
                //   Log.e("myFile", " selectedFilePath->> " + selectedFilePath)
                try {
                    val selectedFilePath: String? = FilePath().getPath(this, myUri)
                    val file = File(selectedFilePath)
                    Log.e(
                        "myFile", " selectedFilePath->> " + selectedFilePath + "  file-> " + file
                                + "  name-> " + file.name
                    )
                    uploadPhoto(file)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
//                Glide.with(this).load(myUri)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true)
//                    .override(1000, 1000)
//                    .into(binding.img)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {

        }
    }

    private fun uploadPhoto(file: File) {
        AppUtills.setProgressDialog(this)
        StatusPictureUpload().ProfilePhotoUpload(
            this,
            model.username,
            false,
            0,
            0,
            "",
            file
        )
    }


    private fun convertFBTime(fbTime: String?): String {
        var ret: String
        try {
            val fbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val eventTime: Date = fbFormat.parse(fbTime)
            val curTime = Date()
            val diffMillis: Long = curTime.getTime() - eventTime.getTime()
            val diffSeconds = diffMillis / 1000
            val diffMinutes = diffMillis / 1000 / 60
            val diffHours = diffMillis / 1000 / 60 / 60
            if (diffSeconds < 60) {
                ret = "$diffSeconds seconds ago"
            } else if (diffMinutes < 60) {
                ret = "$diffMinutes minutes ago"
            } else if (diffHours < 24) {
                ret = "$diffHours hours ago"
            } else if (diffHours < 48) {
                ret = "Yesterday"
            } else {
                var dateFormat = "MMM d"
                if (eventTime.getYear() < curTime.getYear()) {
                    dateFormat += ", yyyy"
                }
                dateFormat += "' at 'kk:mm"
                val calFormat = SimpleDateFormat(
                    dateFormat
                )
                ret = calFormat.format(eventTime)
            }
        } catch (ex: Exception) {
            ret = "error: $ex"
        }
        return ret
    }

    private fun setMyStatusByUrl(url: String) {
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
            .into(binding.img)
    }

    override fun imageUploadCallBack(url: String) {
        myStatusList()
        comingFromTextScreen = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> if (resultCode == RESULT_OK && data != null) {
                val dataList =
                    data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
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


}