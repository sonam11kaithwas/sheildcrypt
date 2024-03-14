package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg

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
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.R.drawable
import com.advantal.shieldcrypt.R.id
import com.advantal.shieldcrypt.auth_pkg.model.DeleteGroupProfilePhotoModel
import com.advantal.shieldcrypt.cropping_filter.ui.CropFilterActivity
import com.advantal.shieldcrypt.databinding.ActivityViewGroupDetailsBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.ProfilePictureUpload
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg.ViewGroupDetailsActivity.MyObject.croppedBitmap
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.UploadCallBack
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.ui.grp_create_contact.ContactListActivity2
import com.advantal.shieldcrypt.ui.model.*
import com.advantal.shieldcrypt.ui.util.SoftKeyboardUtils
import com.advantal.shieldcrypt.utils_pkg.*
import com.advantal.shieldcrypt.utils_pkg.MyApp.Companion.getAppInstance
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences.Companion.getSharedprefInstance
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase
import database.my_database_pkg.db_table.MyAppDataBase.Companion.getUserDataBaseAppinstance
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import java.io.File


@AndroidEntryPoint
class ViewGroupDetailsActivity : XmppActivity(), View.OnClickListener,
    GroupUsertListAdpter.UserSelected, UploadCallBack {
    val APP_TAG = "ShieldCrypt"
    lateinit var binding: ActivityViewGroupDetailsBinding
    lateinit var myDataModel: ResponseItem
    val mainViewModel: MainViewModel by viewModels()
    var uUid = ""
    private var photoFile: File? = null
    var photoFileName = "photo.jpg"
    lateinit var selectedMessage: UsersItem
    var groupUsertListAdpter: GroupUsertListAdpter? = null
    var usrAdmin = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var strUser: String? = intent.getStringExtra("chatUser")
        uUid = intent.getStringExtra("conversationId").toString()
        myDataModel = Gson().fromJson(strUser, ResponseItem::class.java)

        registerForContextMenu(binding.recycleContact)

        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(myDataModel.id?.let { it1 -> AddNewGrpBean(it1) }),
            RequestApis.groupviewById,
            RequestApis.VIEW_GRP_USER
        )


        filterGrpList()



        for (dataModel in myDataModel.users!!) {
            if (dataModel?.admin == true) {

                var v = "${"Created by "}${dataModel.username}${", "}${
                    myDataModel.creationDate?.let {
                        changeDateFormatString(it)
                    }
                }"

                binding.createdByView.text = v.toString()
                break
            }
        }

        binding.updateProfile.setOnClickListener {
            updateGroupName()
        }
        binding.imgPickerBtn.setOnClickListener {
            withItems()
        }

        binding.chatLay.setOnClickListener {
            onBackPressed()
        }
        binding.addLay.setOnClickListener {
            addNewUserInGrp()
        }
        binding.deleteGrpUsr.setOnClickListener {
            val uList = ArrayList<UsersModel>()
            uList.add(
                UsersModel(
                    MySharedPreferences.Companion.getSharedprefInstance()
                        .getLoginData().userid.toInt()
                )
            )
            val grpdatastr = Gson().toJson(myDataModel.id?.let { AddRemoveGrpModel(it, uList) })

            mainViewModel.featchDataFromServerWithAuth(
                grpdatastr, RequestApis.deleteGroupUser, RequestApis.DELETE_GRP_USER
            )



        }
        if (groupUsertListAdpter == null) {
            groupUsertListAdpter = GroupUsertListAdpter(
                this, myDataModel.users as MutableList<UsersItem>?, this
            )
        } else {
            groupUsertListAdpter!!.updateGrpUserDataList(myDataModel.users as MutableList<UsersItem>?)
        }

        notifyAllData()



        mainViewModel.responceCallBack.observe(
            this
        ) {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.DELETE_GRP -> {
                                MyApp.getAppInstance().showToastMsg("Group Deleted Successfully !")
                                redirectOnMainWindow()
                            }
                            RequestApis.DELETE_GRP_USER -> {
                                MyApp.getAppInstance()
                                    .showToastMsg("Members Removed Successfully !")

                                mainViewModel.featchDataFromServerWithAuth(
                                    Gson().toJson(myDataModel.id?.let { it1 -> AddNewGrpBean(it1) }),
                                    RequestApis.groupviewById,
                                    RequestApis.VIEW_GRP_USER
                                )
                            }

                            RequestApis.EDIT_USER_GROUP -> {
                                MyApp.getAppInstance().showToastMsg("Group update successfully")
                                mainViewModel.featchDataFromServerWithAuth(
                                    Gson().toJson(myDataModel.id?.let { it1 -> AddNewGrpBean(it1) }),
                                    RequestApis.groupviewById,
                                    RequestApis.VIEW_GRP_USER
                                )
                            }
                            RequestApis.ADD_GRP_USER -> {
                                MyApp.getAppInstance()
                                    .showToastMsg("Group Member is Added Successfully!")
                                mainViewModel.featchDataFromServerWithAuth(
                                    Gson().toJson(myDataModel.id?.let { it1 -> AddNewGrpBean(it1) }),
                                    RequestApis.groupviewById,
                                    RequestApis.VIEW_GRP_USER
                                )
                            }
                            RequestApis.VIEW_GRP_USER -> {
                                var grpModel = Gson().fromJson(users.data, ResponseItem::class.java)
                                getUserDataBaseAppinstance(getAppInstance())!!.groupDao()
                                    .deleteModelById(grpModel)
                                getUserDataBaseAppinstance(getAppInstance())!!.groupDao()
                                    .addGroupMember(grpModel)
                                myDataModel = grpModel

                                notifyAllData()
                            }
                            else -> {
                                Log.e("", "")
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
        }

    }

    private fun userExitVisibility() {
        var usrFoun = false
        for (userRem in myDataModel.users!!) {
            if (userRem?.id == MySharedPreferences.Companion.getSharedprefInstance()
                    .getLoginData().userid.toInt()
            ) {
                usrFoun = true
                break
            }
        }
        if (usrFoun) binding.deleteGrpUsr.visibility = View.VISIBLE
        else binding.deleteGrpUsr.visibility = View.GONE
    }

    private fun filterGrpList() {
        for (dataModel in myDataModel.users!!) {
            if (dataModel?.id == MySharedPreferences.Companion.getSharedprefInstance()
                    .getLoginData().userid.toInt() && dataModel.admin == true
            ) {
                usrAdmin = true
                break
            }
        }

        var userlist = ArrayList<UsersItem>()

        for (model in myDataModel.users!!) {
            if (model != null && model.id != MySharedPreferences.getSharedprefInstance()
                    .getLoginData().userid.toInt()
            ) {
                userlist.add(model)
            }
        }

        if (userlist.size > 0) {
            myDataModel.users!!.isEmpty()
            myDataModel.users = userlist
        }
    }

    private fun notifyAllData() {
        try {

            userExitVisibility()

            for (dataModel in myDataModel.users!!) {
                if (dataModel?.id == MySharedPreferences.Companion.getSharedprefInstance()
                        .getLoginData().userid.toInt() && dataModel.admin == true
                ) {
                    usrAdmin = true
                    break
                }
            }

            var userlist = ArrayList<UsersItem>()

            for (model in myDataModel.users!!) {
                if (model != null && model.id != MySharedPreferences.getSharedprefInstance()
                        .getLoginData().userid.toInt()
                ) {
                    userlist.add(model)
                }
            }

            if (userlist != null) {
                myDataModel.users!!.isEmpty()
                myDataModel.users = userlist
            }

            binding.nameEdt.setText(myDataModel.groupName)

            var grpPar = "${"Group "}${groupUsertListAdpter!!.itemCount}${" participants"}"
            binding.grpPart.text = grpPar

            binding.grpDescTxt.text = myDataModel.groupDescription

            if (groupUsertListAdpter == null) {
                groupUsertListAdpter = GroupUsertListAdpter(
                    this, myDataModel.users as MutableList<UsersItem>?, this
                )
            } else {
                groupUsertListAdpter!!.updateGrpUserDataList(myDataModel.users as MutableList<UsersItem>?)
            }

            binding.recycleContact.adapter = groupUsertListAdpter

            if (myDataModel.groupImage != null && !myDataModel.groupImage!!.isEmpty()) {
                Glide.with(this).load(myDataModel.groupImage).placeholder(drawable.groupavatar)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                    }).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                    .into(binding.userImg)
            }

            if (groupUsertListAdpter != null) {
                binding.participat.text = "Participants : " + groupUsertListAdpter!!.itemCount
            }
        } catch (e: Exception) {
        }


    }

    private fun updateGroupName() {
        binding.updateBtn.setOnClickListener {
            if (binding.nameEdt.text.toString().trim().equals("")) {
                MyApp.getAppInstance()
                    .showToastMsg(resources.getString(R.string.please_enter_first_name))
            } else {

                val uList = java.util.ArrayList<UserAdd>()
                for (model in myDataModel.users!!) {
                    if (model != null) {
                        uList.add(UserAdd(model.id!!, model.admin!!))
                    }
                }

                val grpdatastr = Gson().toJson(
                    AddNewGrpBean(
                        binding.nameEdt.text.toString(),
                        myDataModel.id!!,
                        myDataModel.groupName,
                        uList
                    )
                )
                mainViewModel.featchDataFromServerWithAuth(
                    grpdatastr, RequestApis.updategroup, RequestApis.EDIT_USER_GROUP
                )

            }
        }
        binding.updateBtn.visibility = View.VISIBLE
        binding.updateProfile.visibility = View.GONE
    }

    private fun redirectOnMainWindow() {
        myDataModel.groupJid?.let {
            MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.groupDao()
                ?.deleteModelById(myDataModel)
        }
        xmppConnectionService.removeConversationById(uUid)
        var intent = Intent()
        intent.putExtra("grpdelete", true)
        setResult(RESULT_OK, intent)
        this.finish()
    }


    override fun onBackPressed() {
        var intent = Intent()
        intent.putExtra("grpdataupdate", true)
        setResult(RESULT_OK, intent)
        this.finish()
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            id.delete_grp -> {
                if (usrAdmin) {
                    mainViewModel.deleteDataFromserver(Gson().toJson(myDataModel.id?.let {
                        DeleteGrp(
                            it
                        )
                    }), RequestApis.deleteGroupById, RequestApis.DELETE_GRP)
                } else {
                    var usrFoun = false
                    for (userRem in myDataModel.users!!) {
                        if (userRem?.id == MySharedPreferences.Companion.getSharedprefInstance()
                                .getLoginData().userid.toInt()
                        ) {
                            usrFoun = true
                            break
                        }
                    }
                    if (!usrFoun) {
                        redirectOnMainWindow()
                    } else {
                        MyApp.getAppInstance().showToastMsg("You are not remove group")
                    }

                }
            }
            id.ic_backarrow -> {
                onBackPressed()
            }

        }
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        v.dispatchTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0f, 0f, 0))
        groupUsertListAdpter?.searchList?.let {
            synchronized(it) {
                super.onCreateContextMenu(menu, v, menuInfo)
                val acmi = menuInfo as AdapterContextMenuInfo
                selectedMessage = groupUsertListAdpter?.searchList!!.get(acmi.position)
                populateContextMenu(menu)
            }
        }
    }

    private fun populateContextMenu(menu: ContextMenu) {
        val m: UsersItem = this.selectedMessage

    }


    private fun changeDateFormatString(strCurrentDate: String): String {
        return strCurrentDate.split(" ")[0]
    }

    override fun refreshUiReal() {

    }

    override fun onBackendConnected() {

    }

    override fun getSelectMultipleCon(contactModel: UsersItem) {
        showPopMenuforAddRemoveUser(contactModel)
    }

    private fun showPopMenuforAddRemoveUser(contactModel: UsersItem) {
        val builder = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.usr_grp_view_layout, null)
        builder.setView(view)
        val add_usr = view.findViewById<TextView>(R.id.add_usr)
        val remove_usr = view.findViewById<TextView>(R.id.remove_usr)

        add_usr.setOnClickListener {
            viewUser(contactModel)
            builder.dismiss()
        }
        remove_usr.setOnClickListener {
            removeUserInGrp(contactModel)
            builder.dismiss()
        }
        builder.setCanceledOnTouchOutside(true)
        builder.show()
    }

    private fun viewUser(contactModel: UsersItem) {

        if (contactModel != null) {
            val intent = Intent(this, ViewContactActivity::class.java)
            intent.putExtra("chatUser", Gson().toJson(contactModel))
            startActivity(intent)
        }
    }

    private fun removeUserInGrp(contactModel: UsersItem) {
        val uList = ArrayList<UsersModel>()
        contactModel.id?.let { UsersModel(it) }?.let { uList.add(it) }
        val grpdatastr = Gson().toJson(myDataModel.id?.let { AddRemoveGrpModel(it, uList) })

        mainViewModel.featchDataFromServerWithAuth(
            grpdatastr, RequestApis.deleteGroupUser, RequestApis.DELETE_GRP_USER
        )

    }

    private fun addNewUserInGrp() {
        val intent = Intent(this, ContactListActivity2::class.java)
        intent.putExtra("add_member_in_grp", "ADD MEMBER")
        intent.putExtra(
            "contacts", Gson().toJson(
                getUserDataBaseAppinstance(getAppInstance())!!.contactDao()
                    .getAllNotes(getSharedprefInstance().getLoginData().mobileNumber)
            )
        )
        startActivity(intent)
    }


    private fun lunchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFileName = System.currentTimeMillis().toString() + ".jpg"
        photoFile = getPhotoFileUri(photoFileName)
        val fileProvider = photoFile?.let {
            FileProvider.getUriForFile(
                this@ViewGroupDetailsActivity, BuildConfig.APPLICATION_ID + ".fileprovider", it
            )
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (intent.resolveActivity(packageManager) != null) {
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
            AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> if (resultCode == RESULT_OK && data != null) {
                val dataList =
                    data.getParcelableArrayListExtra<Uri>(FilePickerConst.KEY_SELECTED_MEDIA)
                if (dataList != null && dataList.size > 0 && dataList.get(0) != null) {
                    try {
                        val intent = Intent(this, CropFilterActivity::class.java)
                        intent.putExtra("imageUriPath", dataList.get(0).toString())
                        intent.putExtra("ACTION", "group_profile")
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
            .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
            .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4).enableVideoPicker(false)
            .enableCameraSupport(false).showGifs(false).showFolderView(true).enableSelectAll(true)
            .enableImagePicker(true)
            // .setCameraPlaceholder(R.drawable.ic_pick_camera)
            .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .pickPhoto(this, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY)
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
            this@ViewGroupDetailsActivity,
            permissions,
            AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
        )
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

    fun withItems() {
        val items = arrayOf(
            resources.getString(R.string.camera),
            resources.getString(R.string.gallery),
            resources.getString(R.string.remove)
        )
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
                } else if (which == 2) {
                    deleteProfile()
                }
            }
            // setPositiveButton("OK", positiveButtonClick)
            show()
        }
    }

    fun uploadPhoto(file: File) {
        AppUtills.setProgressDialog(this)
        ProfilePictureUpload().ProfilePhotoUpload(this@ViewGroupDetailsActivity,
            file,
            "",
            RequestApis.updateGroupImage,
            myDataModel.id.toString(),
            object : ProfilePictureUpload.ImageUploadSuccesFully {
                override fun uploadProfileSyccesFully() {
                    AppUtills.closeProgressDialog()
                    mainViewModel.featchDataFromServerWithAuth(
                        Gson().toJson(myDataModel.id?.let { it1 -> AddNewGrpBean(it1) }),
                        RequestApis.groupviewById,
                        RequestApis.VIEW_GRP_USER
                    )

                }
            })
    }

    private fun deleteProfile() {
        mainViewModel.deleteFromAutoQueryServerWithAuth(Gson().toJson(myDataModel.id?.let {
            DeleteGroupProfilePhotoModel(
                it
            )
        }), RequestApis.groupIconRemove, RequestApis.DELETE_PROFILE)
    }


    override fun imageUploadCallBack(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        Glide.with(this).load(url).placeholder(R.drawable.one_person)
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
            }).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(binding.userImg)

    }

    object MyObject {
        @kotlin.jvm.JvmField
        var croppedBitmap: String = ""
        var chatUuid: String = ""
    }

    override fun onResume() {
        super.onResume()
        Log.e("", "")

        if (MySharedPreferences.Companion.getSharedprefInstance()
                .getChatGrpData() != null && !MySharedPreferences.Companion.getSharedprefInstance()
                .getChatGrpData().equals("")
        ) {
            SoftKeyboardUtils.hideSoftKeyboard(this)

            var grpCreateData =
                MySharedPreferences.Companion.getSharedprefInstance().getChatGrpData()
            MySharedPreferences.Companion.getSharedprefInstance().setChatGrpData("")
            var grpModel: AddNewGrpBean = Gson().fromJson(grpCreateData, AddNewGrpBean::class.java)


            mainViewModel.featchDataFromServerWithAuth(
                Gson().toJson(
                    AddNewGrpBean(
                        myDataModel.id!!, grpModel.users
                    )
                ), RequestApis.addGroupuser, RequestApis.ADD_GRP_USER
            )
        }
        try {
            if (MyObject.croppedBitmap != null && !MyObject.croppedBitmap.isEmpty()) {
                val myUri = Uri.parse(croppedBitmap)

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

                MyObject.croppedBitmap = ""
                Glide.with(this).load(myUri).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).override(1000, 1000).into(binding.userImg)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }
}

data class DeleteGrp(var id: Int)