package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg


import android.Manifest
import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityChatsBinding
import  com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity.Companion.friendJid
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.audio_pkg.AudioActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.camera_pkg.CameraXActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.contact_pkg.ContactListActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.document_pkg.DocumentPickerActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.gallery_folder_pkg.GalleryFolderActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg.ViewContactActivity
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel
import com.advantal.shieldcrypt.ui.LocationActivity
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences.Companion.getSharedprefInstance
import com.advantal.shieldcrypt.xmpp_pkg.MyMessageModel
import com.advantal.shieldcrypt.xmpp_pkg.RoosterConnectionService
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener.Companion.roster
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase
import net.gotev.sipservice.SipAccount
import net.gotev.sipservice.SipServiceCommand
import org.jivesoftware.smack.packet.Presence
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import javax.inject.Inject

@AndroidEntryPoint
class ChatsActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityChatsBinding
    private var locationPermissionGranted = false
    private var isNewMessageSend = false
    private val READ_CONTACTS_PERMISSIONS_REQUEST = 1
    lateinit var mBroadcastReceiver: BroadcastReceiver
    lateinit var presenceBroadcastReceiver: BroadcastReceiver
    var lastPacketId: String = ""
    lateinit var mMessageAdapter: MessageListAdapter

    var share: SharedPrefrence? = null

    //    private var allChat = List<MyMessageModel>()
    var allChat = ArrayList<MyMessageModel>()
    var number: String? = ""


    @Inject
    lateinit var networkHelper: NetworkHelper
    lateinit var myDataModel: ContactDataModel
    var ownerJid = ""
    var isMessageDeleting = false
    var isDeleteMessagePerform = false

    companion object {
        var friendJid = ""

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var strUser: String? = intent.getStringExtra("chatUser") // 2
        myDataModel = Gson().fromJson(strUser, ContactDataModel::class.java)

        if (myDataModel.mobileNumber == null) {
            var str = (Gson().fromJson(strUser, MyMessageModel::class.java).ownerBareJid)

            if (str != null) {
                myDataModel =
                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.contactDao()
                        ?.getContactByThreadId(str)!!
            }
        }

        number = myDataModel.mobileNumber
        share = SharedPrefrence.getInstance(this)
        friendJid = myDataModel.mobileNumber
        ownerJid = MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
//        allChat = MyAppDataBase.getUserDataBaseAppinstance(this)?.messageDao()
//            ?.getChatsFromFriend(myDataModel.mobileNumber, ownerJid) as ArrayList<MyMessageModel>


        initialieViews()

    }

    private fun initialieViews() {
        mMessageAdapter = MessageListAdapter(this, allChat)
        binding.mMessageRecycler.layoutManager = LinearLayoutManager(this)
        scrollChatToLastPosition()
        binding.mMessageRecycler.adapter = mMessageAdapter

        var mMessageList = ArrayList<MessageModel>()
        var messageModel: MessageModel

        var mMessageAdapter = MessageListAdapter(this, allChat)
        binding.mMessageRecycler.layoutManager = LinearLayoutManager(this)
        binding.mMessageRecycler.adapter = mMessageAdapter

        scrollChatToLastPosition()

        binding.chatusername.text = myDataModel.contactName
        Glide.with(this).load(myDataModel.image).diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true).into(binding.userImg)
    }


    fun getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(
                this, permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    shouldShowRequestPermissionRationale(
                            permission.READ_CONTACTS
                        )
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(permission.READ_CONTACTS), READ_CONTACTS_PERMISSIONS_REQUEST
                )
            }
        }
    }


    override fun onBackPressed() {
        this.finish()
    }

    private fun sendMessage() {
        isNewMessageSend = true
        var chatModel = MyMessageModel()
        val randomCode = AppUtills.getRandomCode()
        val currentTime = System.currentTimeMillis()
//        chatModel = AppUtills.getChatModelObjectFromChatType(
//            "$randomCode$currentTime",
//            ownerJid,
//            friendJid,
//            null,
//            binding.etMessage.text.toString().trim(),
//            AppConstants.CODE_TEXT,
//            true,
//            AppConstants.INDIVIDUAL,
//            false,
//            null,
//            System.currentTimeMillis(),
//            AppConstants.xepMessageSent,
//            null,
//            0,
//            null,
//            null,
//            0,
//            false,
//            null,
//            null,
//            null,
//            0,
//            0,
//            null,
//            0,
//            0
//        )!!

// && networkHelper.isNetworkConnected()
//        if(networkHelper.isNetworkConnected()){
//            chatModel.deliveryStatus = AppConstants.xepMessageSent
//        }else{
//            chatModel.deliveryStatus = AppConstants.xepMessageNotSent
//
//        }

        if (AppUtills.isXmppWorkScheduled(this)) {
//
            if(networkHelper.isNetworkConnected()) {
                chatModel.deliveryStatus = AppConstants.xepMessageSent
                var intent = Intent(RoosterConnectionService.SEND_MESSAGE)
                intent.putExtra("chatModel", chatModel)
                AppUtills.insertChatToDb(chatModel, 0, false)
                /*****Send the message to the server*******/
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                binding.etMessage.setText("")
                binding.mMessageRecycler.scrollToPosition(mMessageAdapter.itemCount - 1)
            } else {
                chatModel.deliveryStatus = AppConstants.xepMessageNotSent
                AppUtills.insertChatToDb(chatModel, 0, false)
                binding.etMessage.setText("")
            }
        } else {
            chatModel.deliveryStatus = AppConstants.xepMessageNotSent
            AppUtills.insertChatToDb(chatModel, 0, false)
            binding.etMessage.setText("")
        }

//        MyAppDataBase.Companion.getUserDataBaseAppinstance(this)?.messageDao()?.getAllOfflineMessages(AppConstants.xepMessageNotSent);
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_send -> {
                if (!binding.etMessage.text.toString().trim().equals("", ignoreCase = true)) {
                    sendMessage()
                }
            }

            R.id.call_iv -> {
                /*if (Utils.checkIsBlockedUser(this, destUsername)
                       .equalsIgnoreCase(AppConstants.oneValue)
               ) {
                   showUnblockUserAlert()
               } else {
                   initiateCall()
               }*/

                initiateCall()
            }
            R.id.camera_img -> {
                MyApp.getAppInstance().showToastMsg("Camera")
            }
            R.id.video_call_iv -> {
//                MyApp.getAppInstance().showToastMsg("Vedio call")
                if (checkValidation()) {
                    if (SipAccount.activeCalls.size > 0) {
                        Toast.makeText(
                            this@ChatsActivity,
                            getString(R.string.you_have_already_on_call),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        //MyApp.getAppInstance().showToastMsg("Video call")
                        onVideoCall()
                    }
                }
            }

            R.id.nav_back_iv -> {
                this.finish()
            }
            R.id.layoutLocation -> {
                openLocationViews()
            }
            R.id.layoutContact -> {
                checkAndroidVersionForRuntimePermission(
                    arrayOf(
                        permission.READ_CONTACTS
                    ), AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS
                )
            }
            R.id.layoutAudio -> {
                checkAndroidVersionForRuntimePermission(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, permission.RECORD_AUDIO
                    ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_AUDIO
                )

            }
            R.id.layoutGallery -> {
//                hideAttachmentView()
                checkAndroidVersionForRuntimePermission(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
                    ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
                )

            }
            R.id.menu_iv -> {
                showCustomPopUp()
            }
            R.id.layoutCamera -> {
                checkAndroidVersionForRuntimePermission(
                    arrayOf(
                        WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, permission.CAMERA
                    ), AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA
                )
            }
            R.id.attchments_img -> {
                showHideAttachmentView()
            }
            R.id.layoutDocument -> {
                checkAndroidVersionForRuntimePermission(
                    arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_DOCUMENT
                )
            }
        }

    }

    // make VideoCall when user press the Video button
    fun onVideoCall() {
        AppConstants.TYPE = "CALL"
        val id: String? = share?.getValue(SharedPrefrence.SIPACCOUNTID)
        //        String number = et_mobile.getText().toString().trim();
//        createJsonToAddCallLogs(mobileRegistered, number);
        if (number!!.isEmpty()) {
            number = "*9000"
        }
        //number = "000000038" + number;
        SipServiceCommand.makeCall(this@ChatsActivity, id, number, true, false)
    }

    // internet check and check validation
    fun initiateCall() {
        Utils.hideKeyBoard(this)
        if (!Utils.checkInternetConn(this)) {
            Toast.makeText(
                this@ChatsActivity, getString(R.string.internet_check), Toast.LENGTH_SHORT
            ).show()
        } else
        {
            if (checkValidation()) {
                if (SipAccount.activeCalls.size > 0) {
                    Toast.makeText(
                        this@ChatsActivity,
                        getString(R.string.you_have_already_on_call),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //MyApp.getAppInstance().showToastMsg("Audio call")
                    onCall()
                }
            }
            //finish()
        }
    }

    // check validation for the call
    fun checkValidation(): Boolean {
        if (number.equals("", ignoreCase = true)) {
            Toast.makeText(
                this@ChatsActivity, getString(R.string.please_enter_mobile), Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            return true
        }
    }

    // make call when user press the call button
    fun onCall() {
        val id: String? = share?.getValue(SharedPrefrence.SIPACCOUNTID)
        AppConstants.TYPE = "CALL"
        if (number == null && number!!.isEmpty()) {
            number = "*9000"
        }
        Log.e("go id", "dsda $id")
        Log.e("c", "no $number")
        SipServiceCommand.makeCall(this, id, number)
        // number = ""
    }

    private fun openDocumentViews() {
        val intent = Intent(this, DocumentPickerActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun openContactListViews() {
        val intent = Intent(this, ContactListActivity::class.java)
        resultLauncher.launch(intent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val value = data?.getStringExtra("product")

                Toast.makeText(this@ChatsActivity, value, Toast.LENGTH_SHORT).show()
            }
        }

    var cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
            }
        }


    private fun checkAndroidVersionForRuntimePermission(
        arrayOfPermission: Array<String>, runtimePermissionRequestCode: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(arrayOfPermission)) {
                when (runtimePermissionRequestCode) {
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_DOCUMENT -> openDocumentViews()
                    AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS -> openContactListViews()
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                        openCameraViews()
                    }
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                        openGalleryViews()
                    }
                    AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_AUDIO -> {
                        openAudioViews()
                    }
                }
            } else requestPermission(arrayOfPermission)

        } else {
            when (runtimePermissionRequestCode) {
                AppConstants.PERMISSIONS_REQUEST_READ_CONTACTS -> openContactListViews()

                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_DOCUMENT -> openDocumentViews()
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_CAMERA -> {
                    openCameraViews()
                }
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY -> {
                    openGalleryViews()
                }
                AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_AUDIO -> {
                    openAudioViews()
                }
            }
        }
    }

    private fun openAudioViews() {
        var intent = Intent(this@ChatsActivity, AudioActivity::class.java)
        lunchAudioListner.launch(intent)
    }

    var lunchAudioListner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

            }
        }
    var lunchCameraListner =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

            }
        }

    private fun openGalleryViews() {
        var intent = Intent(this@ChatsActivity, GalleryFolderActivity::class.java)
        lunchCameraListner.launch(intent)
    }

    private fun openLocationViews() {
        var intent = Intent(this@ChatsActivity, LocationActivity::class.java)
        startActivity(intent)
    }

    private fun openCameraViews() {
        var intent = Intent(this@ChatsActivity, CameraXActivity::class.java)
        lunchCameraListner.launch(intent)
    }


    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(
            this@ChatsActivity, permissions, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE_GALLERY
        )
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


    fun showHideAttachmentView() {
        binding.llAttachmentView.visibility =
            if (binding.llAttachmentView.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }
    }

    //    dispatchTouchEvent
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val x = ev!!.x.toInt()
        val y = ev.y.toInt()

        // If user click on the add attchment button or buttons inside attachment view then below condition check
        if ((ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreen(binding.attchmentsImg)!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutDocument
            )!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutCamera
            )!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutAudio
            )!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutGallery
            )!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutContact
            )!!.contains(
                x, y
            ))) && (ev.action == MotionEvent.ACTION_DOWN && (!getLocationOnScreenForAttachmentView(
                binding.layoutLocation
            )!!.contains(x, y)))
        ) {
            hideAttachmentView()
        }

        return super.dispatchTouchEvent(ev)
    }

    fun registerBroadcasts() {
      /*  mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    RoosterConnectionService.NEW_MESSAGE -> {
                        var packetId = ""
                        if (intent.hasExtra(RoosterConnectionService.BUNDLE_MESSAGE_ID)) {
                            packetId =
                                intent.getStringExtra(RoosterConnectionService.BUNDLE_MESSAGE_ID) + ""
                            if (packetId.equals(lastPacketId, ignoreCase = true)) {
                                return
                            }
                        }
                        isNewMessageSend = true

//                        MyAppDataBase.getUserDataBaseAppinstance(context)?.messageDao()
//                            ?.updateReadStatus(friendJid)
                        lastPacketId = packetId
//                        val chatModel: MyMessageModel? = intent.getSerializableExtra("chatmodel")
//                        if (chatModel?.threadBareJid.equals(friendJid, ignoreCase = true))
                        {
                            mMessageAdapter.updatelist(
                                MyAppDataBase.getUserDataBaseAppinstance(
                                    MyApp.getAppInstance()
//                                )?.messageDao()?.getChatsFromFriend(
                                    myDataModel.mobileNumber, ownerJid
                                ) as ArrayList<MyMessageModel>
                            )
                        }
                        return
                    }
                }
            }*/
        }
        /*  mfiledownloadBroadcast = object : BroadcastReceiver() {
              override fun onReceive(context: Context, intent: Intent) {
                  val action = intent.action
                  when (action) {
                      RoosterConnectionService.FILE_DOWNLOAD_RECEIVER ->
                          return
                  }
              }
          }
          presenceBroadcastReceiver = object : BroadcastReceiver() {
              override fun onReceive(context: Context, intent: Intent) {
                  val action = intent.action
                  when (action) {
                      RoosterConnectionService.PRESENCE_CHANGE -> {
                          //                        Toast.makeText(ChatWindowActivity.this, "Presence change", Toast.LENGTH_SHORT).show();
                          updatePresence()
                          return
                      }
                  }
              }
          }*/
//        val filter = IntentFilter(RoosterConnectionService.NEW_MESSAGE)
//        registerReceiver(mBroadcastReceiver, filter)
//        val fileDownloadFilter = IntentFilter(RoosterConnectionService.FILE_DOWNLOAD_RECEIVER)
//        registerReceiver(mfiledownloadBroadcast, fileDownloadFilter)
//        val presenceBroadcastFilter = IntentFilter(RoosterConnectionService.PRESENCE_CHANGE)
//        registerReceiver(presenceBroadcastReceiver, presenceBroadcastFilter)
//    }

    override fun onResume() {
        Log.e("onResume", "onResume")

        MySharedPreferences.getSharedprefInstance().setChatWindowOpen(true)
        registerBroadcasts()
        hideAttachmentView()
        callObserverForLiveDate()
        registerBroadcastsnew()
        try {
            if (friendJid.isNotEmpty() && MySharedPreferences.getSharedprefInstance()
                    .getXSip() != null
            ) {
                getLocalPrefrence()
            }
        } catch (e: Exception) {

        }
//        MyAppDataBase.getUserDataBaseAppinstance(this)?.recentChatMessageDao()?.updateReadStatus(
//            friendJid
//        )
        super.onResume()
    }

    private fun getLocalPrefrence() {
        try {
            var typeOfStatus = roster?.getPresence(
                JidCreate.entityBareFrom(
                    friendJid + "@" + MySharedPreferences.getSharedprefInstance().getXSip()
                )
            )?.type
            when (typeOfStatus) {
                Presence.Type.available -> {
                    setUserStatus(2)
                }
                Presence.Type.unavailable -> {
                    setUserStatus(1)
                }
                else -> {
                    setUserStatus(2)
                }
            }
//            XMPPConnectionListener.roster?.getAllPresences(JidCreate.entityBareFrom(ownerJid + "@" + getSharedprefInstance().getLoginData().xmpip))
        } catch (e: Exception) {
        }
    }

    fun scrollChatToLastPosition() {
        binding.mMessageRecycler.scrollToPosition(mMessageAdapter.itemCount - 1)
    }

    fun callObserverForLiveDate() {
        /*MyAppDataBase.getUserDataBaseAppinstance(this)?.messageDao()
            ?.getAllLiveChatByJid(friendJid, ownerJid)
            ?.observe(this, Observer<List<MyMessageModel?>?> {
                if (!isMessageDeleting) {
                    allChat.clear()
                    allChat = it as ArrayList<MyMessageModel>
                    Log.e("", "")
                    if (isDeleteMessagePerform) {
                        isDeleteMessagePerform = false
                        binding.mMessageRecycler.layoutManager?.removeAllViews()
                    }
                }
                mMessageAdapter.setTasks(it as ArrayList<MyMessageModel>, isNewMessageSend)
                if (isNewMessageSend) {
                    isNewMessageSend = false
                }
            })
*/

    }


    override fun onPause() {
        MySharedPreferences.getSharedprefInstance().setChatWindowOpen(false)
        unregisterReceiver(mBroadcastReceiver)

        unregisterReceiver(presenceBroadcastReceiver)


        super.onPause()
    }

    private fun hideAttachmentView() {
        binding.llAttachmentView.visibility = View.GONE
    }

    // Get the location of the add attachment button on the screen if user click on the add button condition inside dispatchtouchevent works
    private fun getLocationOnScreenForAttachmentView(linearLayout: LinearLayout): Rect? {
        val mRect = Rect()
        val location = IntArray(2)
        linearLayout.getLocationOnScreen(location)
        mRect.left = location[0]
        mRect.top = location[1]
        mRect.right = location[0] + linearLayout.width
        mRect.bottom = location[1] + linearLayout.height
        return mRect
    }


    // Get the location of the add attachment button on the screen if user click on the add button condition inside dispatchtouchevent works
    protected fun getLocationOnScreen(imageView: ImageView): Rect? {
//        var v=getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val mRect = Rect()
        val location = IntArray(2)
        imageView.getLocationOnScreen(location)
        mRect.left = location[0]
        mRect.top = location[1]
        mRect.right = location[0] + imageView.width
        mRect.bottom = location[1] + imageView.height
        return mRect
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                AppConstants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {


        var allPermissionGranted = true
        if (grantResults.size > 0) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionGranted = false
                }
            }
            if (!allPermissionGranted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var isAnyPermissionNotGranted = false

                    var i = 0
                    for (i in 0..permissions.size - 1) {
                        if (this.shouldShowRequestPermissionRationale(permissions[i]!!)) {


                            (ActivityCompat.shouldShowRequestPermissionRationale(
                                this@ChatsActivity, Manifest.permission.READ_CONTACTS
                            ))
                            isAnyPermissionNotGranted = true
                        }
                    }

                    if (!isAnyPermissionNotGranted) {
                        MyApp.getAppInstance()
                            .showToastMsg("For continue you need to enable permissions from the Settings-> Permissions-> Enable permission.")

                    }
                }
            }
        }
    }


    private fun showCustomPopUp() {
        var mypopupWindow: PopupWindow
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: ViewGroup = inflater.inflate(
            R.layout.chat_menu_layout_popup, null, false
        ) as ViewGroup


        mypopupWindow = PopupWindow(
            view, 420, RelativeLayout.LayoutParams.WRAP_CONTENT, true
        )


        mypopupWindow.showAtLocation(
            binding.callIv, Gravity.TOP, 280, 220
        )


        var view_contact_lable =
            view.findViewById(com.advantal.shieldcrypt.R.id.view_contact_lable) as TextView


        view_contact_lable.setOnClickListener {

            val intents = Intent(this, ViewContactActivity::class.java)
            intents.putExtra("chatUser", intent.getStringExtra("chatUser"))
            resultLauncher.launch(intents)
            mypopupWindow.dismiss()
        }
    }

    fun registerBroadcastsnew() {
        presenceBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                when (action) {
                    RoosterConnectionService.PRESENCE_CHANGE -> {
                        updatePresence()
                        return
                    }
                }
            }
        }
        val presenceBroadcastFilter = IntentFilter(RoosterConnectionService.PRESENCE_CHANGE)
        registerReceiver(presenceBroadcastReceiver, presenceBroadcastFilter)
//        updatePresence()
    }

    fun updatePresence() {
        val presence: Presence
        var jid: EntityBareJid? = null
        try {
            jid = JidCreate.entityBareFrom(
                friendJid + "@" + getSharedprefInstance().getXSip()//"151.106.34.159"
            )
            if (XMPPConnectionListener.roster != null) {
                presence = XMPPConnectionListener.roster!!.getPresence(jid)
                getpresence(presence)
            }
        } catch (e: XmppStringprepException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
//                "6267192998" 8109383638@shieldcrypt.co.in

    private fun getpresence(presence: Presence) {
        var status = "Offline"
        val prsnc: String = presence.type.toString()


        if (!presence.isAvailable) {
            status = "Offline"
            setUserStatus(1)
        } else if (presence.mode === Presence.Mode.away) {
            status = "Away"
            setUserStatus(1)
        } else if (presence.mode === Presence.Mode.available) {
            status = "Online"

            setUserStatus(2)

        }

    }

    private fun setUserStatus(i: Int) {
        when (i) {
            1 -> {
                binding.batchIcon.visibility = View.GONE
                binding.chatuserStatus.text = ""
            }
            2 -> {
                binding.batchIcon.visibility = View.VISIBLE
                binding.chatuserStatus.text = "Online"
            }
        }
    }
}





