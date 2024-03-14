package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.sheildcrypt.network_pkg.StatusPictureUpload
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentStatusBinding
import com.advantal.shieldcrypt.cropping_filter.ui.CropFilterActivity
import com.advantal.shieldcrypt.network_pkg.*
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.GetAllMediaStatusAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity.MyObject.croppedBitmap
import com.advantal.shieldcrypt.tabs_pkg.model.DataItem
import com.advantal.shieldcrypt.tabs_pkg.model.DataItemMediaStatus
import com.advantal.shieldcrypt.tabs_pkg.model.GetAllMediaStatusResponse
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusModel
import com.advantal.shieldcrypt.utils_pkg.*
import dagger.hilt.android.AndroidEntryPoint
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class StatusFragment : Fragment(), GetAllMediaStatusAdapter.ItemClickListner {
    lateinit var binding: FragmentStatusBinding
    var adapter: GetAllMediaStatusAdapter? = null
    var statuslist = ArrayList<DataItemMediaStatus>()


    @Inject
    lateinit var networkHelper: NetworkHelper
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var photoFileName = "photo.jpg"
    val APP_TAG = "ShieldCrypt"
    private var photoFile: File? = null
    private var mainList = ArrayList<GetAllMediaStatusResponse>()


//    icon_editstatus   icon_statuscamera

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatusBinding.inflate(inflater, container, false)

        binding.mystatusLayout.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_statusfragment_to_mystatusfragment)
        }

        binding.textstatusStory.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_statusfragment_to_textstatus_fragment)
        }
        myStatusList()
        getAllStatus()

        mainViewModel.responceCallBack.observe(
            requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {

                                RequestApis.MY_STATUS_LIST -> {
//

                                    val listType =
                                        object : TypeToken<List<DataItem?>?>() {}.type
                                    val contactDataList: List<DataItem> =
                                        Gson().fromJson<List<DataItem>>(
                                            users.data.toString(),
                                            listType
                                        )

                                    Glide.with(this).load(contactDataList[0].mediaurl)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .override(1000, 1000)
                                        .into(binding.img)

//                                    var str =
//                                        Gson().fromJson(it.data.data, MyStatusResponse::class.java).data
//                                    Log.e("statusAboutResModel: ", contactDataList[0].mediastatusid.toString())


//                                    adapter?.updateStatuslist(Gson().fromJson<List<DataItem>>(users.data.toString(), listType) as ArrayList<DataItem>)


                                }

                                RequestApis.GET_ALL_MEDIA_STATUS -> {
//

                                    val listType =
                                        object : TypeToken<List<DataItemMediaStatus?>?>() {}.type
                                    val contactDataList: List<DataItemMediaStatus> =
                                        Gson().fromJson<List<DataItemMediaStatus>>(
                                            users.data.toString(),
                                            listType
                                        )


//                                    var str =
//                                        Gson().fromJson(it.data.data, MyStatusResponse::class.java).data
                                    Log.e(
                                        "statusAboutResModel: ",
                                        contactDataList[0].username.toString()
                                    )


//                                    adapter?.updateStatuslist(
//                                        Gson().fromJson<List<DataItemMediaStatus>>(
//                                            users.data.toString(),
//                                            listType
//                                        ) as ArrayList<DataItemMediaStatus>
//                                    )
                                    val listOfData = Gson().fromJson<List<DataItemMediaStatus>>(
                                        users.data.toString(),
                                        listType
                                    ) as ArrayList<DataItemMediaStatus>

                                    val map: HashMap<String, Int> = HashMap()

                                    for (i in listOfData.indices) {
                                        if (map.containsKey(listOfData[i].userid.toString())) {
                                            for (j in mainList.indices) {
                                                if (mainList[j].userid == listOfData[i].userid) {
                                                    var list1 = ArrayList<DataItemMediaStatus>()
                                                    list1 = mainList[j].data!!
                                                    list1.add(listOfData[i])
                                                    mainList[j].data = list1
                                                }
                                            }
                                        } else {
                                            val list1 = ArrayList<DataItemMediaStatus>()
                                            map[listOfData[i].userid.toString()] = 1
                                            val dto = GetAllMediaStatusResponse()
                                            dto.username = listOfData[i].username
                                            dto.userid = listOfData[i].userid
                                            list1.add(listOfData[i])
                                            dto.data = list1
                                            if (dto != null)
                                                mainList.add(dto)
                                        }
                                    }
                                    adapter?.updateStatuslist(
                                        mainList
                                    )
                                }


                                else -> {}
                            }
                        }

                    }


                    Status.LOADING -> {
                        AppUtills.setProgressDialog(requireActivity())
                    }
                    Status.ERROR -> {
                        MyApp.getAppInstance().showToastMsg(it.message.toString())
                    }
                }
            })

        adapter = GetAllMediaStatusAdapter(statuslist, this)


        binding.getAllStatusRecyclerView.adapter = adapter
        binding.getAllStatusRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        if (model.profileUrl != null && !model.profileUrl.isEmpty()) {
//
//            binding.progressBar.visibility = View.VISIBLE
//
//            Glide.with(this)
//                .load(model.profileUrl)
//                .placeholder(R.drawable.one_person)
//                .listener(object : RequestListener<Drawable> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: com.bumptech.glide.request.target.Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        binding.progressBar.visibility = View.GONE
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: com.bumptech.glide.request.target.Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        binding.progressBar.visibility = View.GONE
//                        return false
//                    }
//
//                })
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                // .override(1000, 1000)
//                .into(binding.img)
//        }

        binding.imgPickerBtn.setOnClickListener {
            withItems()
        }
        binding.cameraicon.setOnClickListener {
            withItems()
        }


//        binding.statusStory.setOnClickListener {
//            val intent = Intent(requireContext(),MediaStoryStatus::class.java)
//            startActivity(intent)
//        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        try {
            if (croppedBitmap != null && !croppedBitmap.isEmpty()) {
                val myUri = Uri.parse(croppedBitmap)
                //   val selectedFilePath = File(myUri.toString())
                //   Log.e("myFile", " selectedFilePath->> " + selectedFilePath)
                try {
                    val selectedFilePath: String? = FilePath().getPath(requireContext(), myUri)
                    val file = File(selectedFilePath)
                    Log.e(
                        "myFile", " selectedFilePath->> " + selectedFilePath + "  file-> " + file
                                + "  name-> " + file.name
                    )
                    uploadPhoto(file)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                croppedBitmap = ""
                Glide.with(this).load(myUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .override(1000, 1000)
                    .into(binding.img)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {

        }
    }

    fun uploadPhoto(file: File) {
        /* mainViewModel.uploadProfilePhotoWithToken(
            file, "",
             RequestApis.uploadProfilePhoto,
             RequestApis.UPLOAD_PROFILE_PHOTO
         )*/
        AppUtills.setProgressDialog(requireContext())
        StatusPictureUpload().ProfilePhotoUpload(
            requireActivity(),
            model.username,
            false,
            0,
            0,
            "",
            file
        )
    }

    fun withItems() {
        val items =
            arrayOf(resources.getString(R.string.camera), resources.getString(R.string.gallery))
        val builder = AlertDialog.Builder(requireContext())
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
                    requireContext(),
                    permissions[i]
                )
            }
        }
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            requireActivity(), permissions, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
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
                        openGallery()
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

    private fun lunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFileName = System.currentTimeMillis().toString() + ".jpg"
        photoFile = getPhotoFileUri(photoFileName)
        val fileProvider = photoFile?.let {
            FileProvider.getUriForFile(
                requireContext(),
                BuildConfig.APPLICATION_ID + ".fileprovider",
                it
            )
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (requireContext().packageManager != null) {
            // startActivityForResult(intent, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA)
            lunchCameraListner.launch(intent)
        }
    }

    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            requireContext().packageManager?.also {
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
                        Glide.with(this).load(data.data)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(1000, 1000)
                            .into(binding.img)

                        val intent = Intent(requireContext(), CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", data.data.toString())
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to load", Toast.LENGTH_SHORT).show()
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
                        Glide.with(this).load(photoFile)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(1000, 1000)
                            .into(binding.img)

                        val intent = Intent(requireContext(), CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", takenPhotoUri.toString())
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Failed to load", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("Camera", e.toString())
                }

            }
        }


    fun getPhotoFileUri(fileName: String): File? {
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    fun onPickPhoto() {
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

    fun getAllStatus() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(hashMap),
            RequestApis.get_all_media_status,
            RequestApis.GET_ALL_MEDIA_STATUS
        )
    }


    object MyObject {
        @kotlin.jvm.JvmField
        var croppedBitmap: String = ""
    }

    override fun getItemSelected(invListItem: ArrayList<DataItemMediaStatus>, username: String?) {

//        val intent = Intent(getActivity(), OthersMediaStoryFullActivity::class.java)
//        intent.putExtra("otherstatusimage", invListItem.mediastatusdetails)
//        intent.putExtra("othermediastatusid", invListItem.statusid.toString())
//        intent.putExtra("otherMediausername", invListItem.username)
//        intent.putExtra("penciltext", invListItem.penciltext.toString())
//        intent.putExtra("background", invListItem.colorcode.toString())
//        intent.putExtra("font", invListItem.fontstylecode.toString())
//        getActivity()?.startActivity(intent)
//        Log.e("otherMediausername: ", invListItem.username.toString())
//        Log.e("getallmediaselect: ", invListItem.statusid.toString())
//        Log.e("getallmediaimge: ", invListItem.mediastatusdetails.toString())


    }

    fun myStatusList() {
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                MyStatusModel(model.username, "0", false)
            ), RequestApis.my_status_list, RequestApis.MY_STATUS_LIST
        )
    }



}