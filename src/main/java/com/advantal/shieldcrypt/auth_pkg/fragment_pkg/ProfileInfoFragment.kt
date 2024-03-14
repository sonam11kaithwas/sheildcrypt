package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentProfileInfoBinding
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity
import com.advantal.shieldcrypt.auth_pkg.model.DeleteProfilePhotoModel
import com.advantal.shieldcrypt.cropping_filter.ui.CropFilterActivity
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.ProfilePictureUpload
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.profilemodel.ProfileUpdateReqModel
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.SettingFragment
import com.advantal.shieldcrypt.utils_pkg.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ProfileInfoFragment : Fragment() {

    lateinit var binding: FragmentProfileInfoBinding
    val mainViewModel: MainViewModel by viewModels()
    var photoFileName = "photo.jpg"
    val APP_TAG = "ShieldCrypt"
    private var photoFile: File? = null
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileInfoBinding.inflate(inflater, container, false)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }


        binding.imgPickerBtn.setOnClickListener {
            withItems()
        }

        binding.profileInfo.setOnClickListener {

            initiateVerification()

        }


//        mainViewModel.featchDataFromServerWithAuth(
//            Gson().toJson(
//                ProfileReqModel(
//                    (MySharedPreferences.getSharedprefInstance()
//                        .getLoginData().userid).toInt()
//                )
//            ), RequestApis.view_Profile_ById,RequestApis.VIEW_PROFILE_BY_ID_REQ
//        )

        binding.nameEdt.setText(model.firstName ?: "")
        binding.lastnameEdt.setText(model.lastName ?: "")
        binding.phoneNumEdt.setText(model.mobileNumber ?: "")

        if (model.profileUrl != null && !model.profileUrl.isEmpty()) {

            binding.progressBar.visibility = View.VISIBLE

            Glide.with(this)
                .load(model.profileUrl)
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

        mainViewModel.responceCallBack.observe(
            requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.UPDATE_PROFILE_REQ -> {
                                    SettingFragment.MyObject.profileChangedStatus = true
                                    Navigation.findNavController(binding.profileInfo)
                                        .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)

                                }
                                RequestApis.UPLOAD_PROFILE_PHOTO -> {

                                }
                                RequestApis.DELETE_PROFILE -> {
                                    binding.userImg.setImageResource(R.drawable.one_person)
                                }

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


        binding.nameEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        clickListners()
        return binding.root
        // Inflate the layout for this fragment
    }

    private fun clickListners() {
        binding.toolbarBar.icBackarrow.visibility = View.GONE
        binding.toolbarBar.toolbar.text = getString(R.string.profile_info)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*clean Current FRAGMENT*/
                    requireActivity().finish()
                }
            })

    }

    fun getWind() {
        val intent = Intent(activity, AuthenticationActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        try {
            if (ProfileActivity.MyObject.croppedBitmap !=null && !ProfileActivity.MyObject.croppedBitmap.isEmpty()){
                val myUri = Uri.parse(ProfileActivity.MyObject.croppedBitmap)
                //   val selectedFilePath = File(myUri.toString())
                //   Log.e("myFile", " selectedFilePath->> " + selectedFilePath)
                try {
                    val selectedFilePath: String? = FilePath().getPath(requireActivity(), myUri)
                    val file = File(selectedFilePath)
                    Log.e("myFile", " selectedFilePath->> " + selectedFilePath + "  file-> " +file
                            + "  name-> " + file.name)
                    uploadPhoto(file)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                ProfileActivity.MyObject.croppedBitmap = "";
                Glide.with(this).load(myUri)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .override(1000, 1000)
                    .into(binding.userImg)
            }
        }catch (e: NullPointerException){
            e.printStackTrace()
        } finally {

        }
    }

    fun uploadPhoto(file: File){
        AppUtills.setProgressDialog(requireActivity())
        ProfilePictureUpload().ProfilePhotoUpload(requireActivity(), file, model.username,
            RequestApis.updateUserProfilePhoto,"")
    }

    fun withItems() {
        val items = arrayOf(resources.getString(R.string.camera), resources.getString(R.string.gallery),resources.getString(R.string.remove))
        val builder = AlertDialog.Builder(requireActivity())
        with(builder) {
            setTitle(resources.getString(R.string.select_photo))
            setItems(items) { dialog, which ->
                //Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                if (which==0){
                    checkAndroidVersionForRuntimePermission(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA
                    )
                } else if (which==1){
                    openGallery()
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
            requireContext().packageManager?.also {
                lunchGalleryListener.launch(intent)
            }
        }
    }

    private var lunchGalleryListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data!=null) {
                val data: Intent? = result.data
                Log.e("imagePath", "  data->> $data" + "  -> " + data?.data)
                try {
                    if (data != null && data.data!=null) {
                        Glide.with(this).load(data.data)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .override(1000, 1000)
                            .into(binding.userImg)

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

    private fun lunchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFileName = System.currentTimeMillis().toString() + ".jpg"
        photoFile = getPhotoFileUri(photoFileName)
        val fileProvider = photoFile?.let {
            FileProvider.getUriForFile(requireActivity(),
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
                            .into(binding.userImg)

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
                        // openGalleryViews()
                    }
                }
            } else requestPermission(arrayOfPermission)

        } else {
            when (runtimePermissionRequestCode) {
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                    lunchCamera()
                }
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                    // openGalleryViews()
                }
            }
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

    private fun initiateVerification() {
        if (!binding.nameEdt.text.toString().trim().isEmpty() &&
            !binding.lastnameEdt.text.toString().trim().isEmpty()) {

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





        } else {
            if( binding.nameEdt.text.toString().trim().isEmpty()){
                Toast.makeText(requireContext(), "Please Enter First name", Toast.LENGTH_SHORT).show()
            }
            else if( binding.lastnameEdt.text.toString().trim().isEmpty()){
                Toast.makeText(requireContext(), "Please Enter Last name", Toast.LENGTH_SHORT).show()
            }


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

}