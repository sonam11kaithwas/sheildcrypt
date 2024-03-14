package com.advantal.shieldcrypt.auth_pkg

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.advantal.shieldcrypt.Config
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityAuthenticationBinding
import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.entities.Conversation
import com.advantal.shieldcrypt.network_pkg.*
import com.advantal.shieldcrypt.service.BackgroundConnectionService
import com.advantal.shieldcrypt.services.XmppConnectionService.OnConversationUpdate
import com.advantal.shieldcrypt.services.XmppConnectionService.OnRosterUpdate
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.ui.ConversationFragment
import com.advantal.shieldcrypt.ui.StartConversationActivity
import com.advantal.shieldcrypt.ui.UiCallback
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.ui.interfaces.OnConversationRead
import com.advantal.shieldcrypt.ui.interfaces.OnConversationSelected
import com.advantal.shieldcrypt.ui.interfaces.OnConversationsListItemUpdated
import com.advantal.shieldcrypt.ui.model.ResponseItem
import com.advantal.shieldcrypt.ui.util.SoftKeyboardUtils
import com.advantal.shieldcrypt.utils_pkg.*
import com.advantal.shieldcrypt.utils_pkg.AppConstants.Companion.PERMISSION_REQUEST_CODE
import com.advantal.shieldcrypt.utils_pkg.AppUtills.Companion.closeProgressDialog
import com.advantal.shieldcrypt.utils_pkg.MyApp.Companion.getAppInstance
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences.Companion.getSharedprefInstance
import com.advantal.shieldcrypt.xmpp.Jid
import com.google.gson.Gson
import com.hlogi.diforinterface.MyMainClass
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase.Companion.getUserDataBaseAppinstance
import net.gotev.sipservice.SipServiceCommand
import javax.inject.Inject


@AndroidEntryPoint
class AuthenticationActivity() : XmppActivity(), View.OnClickListener, OnConversationRead,
    OnConversationsListItemUpdated, OnConversationUpdate, OnRosterUpdate, OnConversationSelected {
    lateinit var binding: ActivityAuthenticationBinding
    var fragmentCommunicator: FragmentCommunicator? = null
    var fragmentCommunicatorForMsgs: FragmentCommunicatorForMsgs? = null
    val mainViewModel: MainViewModel by viewModels()
    var mySharedPreferences: SharedPrefrence? = null


    private val mAdhocConferenceCallback: UiCallback<Conversation?> =
        object : UiCallback<Conversation?> {
            override fun success(`object`: Conversation?) {
                if (fragmentCommunicator != null) {
                    fragmentCommunicator?.openChatWindow(`object`?.uuid)
                }
            }

            override fun error(errorCode: Int, `object`: Conversation?) {
            }

            override fun userInputRequired(pi: PendingIntent?, `object`: Conversation?) {
            }

        }

    companion object {
        var FLAGNOTIFY = 0
    }


    @Inject
    lateinit var networkHelper: NetworkHelper
    private var viewModel: AuthViewModel? = null

    /******This is a Exmaple of Interface Injection*****/
    @Inject
    lateinit var myMainClass: MyMainClass
    var isAutoLoginStatus = false

    constructor(parcel: Parcel) : this() {
        isAutoLoginStatus = parcel.readByte() != 0.toByte()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)

//        setTheme.ThemeHelper.find(this)

        setContentView(binding.root)
        var authViewModelFactory: AuthViewModelFactory = AuthViewModelFactory.createFactory(this)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        isAutoLoginStatus = MySharedPreferences.getSharedprefInstance().getAutoLoginStatus()
        mySharedPreferences = SharedPrefrence.getInstance(this)

        openBatteryOptimizationDialogIfNeeded()

        try {
            if (!isAutoLoginStatus) {
                stopService()
                clearLocalDetailsAfterLogout()

            }
        } catch (e: Exception) {
        }

        processViewIntent()
        myMainClass.getMyName()



        if (intent != null && intent.hasExtra("ADDNEWCHATGRP")) {
            var v = intent.getStringExtra("ADDNEWCHATGRP")
//            createNewChatGrp()
        }

        mainViewModel.responceCallBack.observeForever(object : Observer<Resource<ResponceModel>> {
            override fun onChanged(responceModelResource: Resource<ResponceModel>) {
                if (responceModelResource.status == Status.SUCCESS) {
                    closeProgressDialog()
                    if (responceModelResource.data!!.requestCode == RequestApis.ADD_USER_GROUP) {
                        val item = Gson().fromJson(
                            responceModelResource.data.data, ResponseItem::class.java
                        )
                        getUserDataBaseAppinstance(getAppInstance())!!.groupDao()
                            .addGroupMember(item)
                        val account = getSelectedAccount()
                        var groupName: String? = ""
                        var groupDescription: String? = ""
                        val jabberIds: MutableList<Jid> = ArrayList()
                        run {
//                            for (ResponseItem responseItem : createGrpList)
                            run {
                                groupName = item.groupName
                                groupDescription = item.groupDescription
                                for (usersItem in item.users!!) {
                                    jabberIds.add(
                                        Jid.of(
                                            usersItem!!.mobileNumber + "@" + getSharedprefInstance().getChatip()
                                        )
                                    )
                                }

                                if (xmppConnectionService.createAdhocConference(
                                        account,
                                        groupName,
                                        jabberIds,
                                        mAdhocConferenceCallback,
                                        item.groupJid
                                    )
                                ) {
                                }
                            }
                        }
                    }
                } else if (responceModelResource.status == Status.LOADING) {
                } else if (responceModelResource.status == Status.ERROR) {
                    closeProgressDialog()
                    getAppInstance().showToastMsg(responceModelResource.message!!)
                }
            }
        })


    }

    private fun createNewChatGrp(str: String) {
        mainViewModel.featchDataFromServerWithAuth(
            str, RequestApis.addUserInGroup, RequestApis.ADD_USER_GROUP
        )
    }


    fun clearLocalDetailsAfterLogout() {
        // remove account which is registered in the app
        try {
            SipServiceCommand.resetAccount(this)
            //SipServiceCommand.stop(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // mySharedPreferences.clearSharedPrefernces(activity)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    // Check permission for the call phone, read contact and record audio
    private fun checkPermission(): Boolean {
        val result = (ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.CALL_PHONE
        ) + ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_CONTACTS
        ) + ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) + ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
        ) + ContextCompat.checkSelfPermission(
            applicationContext, Manifest.permission.RECORD_AUDIO
        ))
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CALL_PHONE
            ), PERMISSION_REQUEST_CODE
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.search_back -> {
            }
        }
    }


    fun stopService() {
        val serviceIntent = Intent(this, BackgroundConnectionService::class.java)
        this.stopService(serviceIntent)
    }

    override fun refreshUiReal() {
        Log.e("", "")
        if (FLAGNOTIFY == 2 && fragmentCommunicatorForMsgs != null) {
            fragmentCommunicatorForMsgs?.passUpdateMsgData()

        }
    }

    override fun onBackendConnected() {
        Log.e("", "")
        if (fragmentCommunicator != null) {
            getSelectedAccount()?.let { fragmentCommunicator?.accountNotify(it) }
        }
    }


    /*update message when send/receive */
    override fun onConversationUpdate() {
        Log.e("", "")
        if (FLAGNOTIFY == 1 && fragmentCommunicator != null) {
            fragmentCommunicator?.passData()
        } else if (FLAGNOTIFY == 2 && fragmentCommunicatorForMsgs != null) {
            refreshUi()
//            fragmentCommunicatorForMsgs?.passUpdateMsgData()

        }
    }

    override fun onRosterUpdate() {
        Log.e("", "")
//        refreshUi()
    }

    override fun onDestroy() {
        Log.e("", "")
        super.onDestroy()
    }

    override fun onPause() {
        Log.e("", "")
        super.onPause()
    }

    override fun onResume() {
        Log.e("", "")

        if (MySharedPreferences.Companion.getSharedprefInstance()
                .getChatGrpData() != null && !MySharedPreferences.Companion.getSharedprefInstance()
                .getChatGrpData().equals("")
        ) {
            SoftKeyboardUtils.hideSoftKeyboard(this)

            var grpCreateData =
                MySharedPreferences.Companion.getSharedprefInstance().getChatGrpData()
            MySharedPreferences.Companion.getSharedprefInstance().setChatGrpData("")
            createNewChatGrp(grpCreateData)

        }
        super.onResume()
    }

    override fun onConversationRead(conversation: Conversation?, upToUuid: String?) {
        xmppConnectionService.sendReadMarker(conversation, upToUuid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("", "")
        if (resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("ADDNEWCHATGRP")) {
                createNewChatGrp(data.getStringExtra("requestParam").toString())
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    /*update message when Received */
    override fun onConversationsListItemUpdated() {
        Log.e("", "")
        if (fragmentCommunicator != null) {
            fragmentCommunicator?.passData()
        }
    }

    public fun updateList(){
        if (fragmentCommunicator != null) {
            fragmentCommunicator?.passData()
        }
        FLAGNOTIFY = 1
//        findNavController().navigate(R.id.action_welcomeFragment_to_tabLayoutFragment)

    }
    override fun onConversationSelected(conversation: Conversation?) {
        var conversationFragment = ConversationFragment()
        conversationFragment.reInit(conversation, null)
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_auth, conversationFragment, null)
            .setReorderingAllowed(true)

//            .addToBackStack(conversationFragment.javaClass.name)
            .commit()
    }

    override fun onBackPressed() {
        Log.e("", "")
        if (FLAGNOTIFY == 0) {
            finish()
        } else if (FLAGNOTIFY == 2) {
            if (fragmentCommunicator != null) {
                fragmentCommunicator?.passData()
            }
            FLAGNOTIFY = 1
        } else if (FLAGNOTIFY == 1) {
            FLAGNOTIFY = 0
        }
        super.onBackPressed()
    }

    //Here is new method
    fun passVal(fragmentCommunicator: FragmentCommunicator?) {
        this.fragmentCommunicator = fragmentCommunicator!!
        fragmentCommunicator.firstLoadGrpList()
    }

    //Here is new method
    fun passMsgDataInstance(fragmentCommunicatorForMsgs: FragmentCommunicatorForMsgs?) {
        this.fragmentCommunicatorForMsgs = fragmentCommunicatorForMsgs!!
    }

    fun processViewIntent(): Boolean {

        if (intent.hasExtra(StartConversationActivity.EXTRA_CONVERSATION)) {
            val uuid = intent.getStringExtra(StartConversationActivity.EXTRA_CONVERSATION)
            if (fragmentCommunicator != null) {
                if (uuid != null) {
                    fragmentCommunicator?.openChatWindow(uuid)
                }
            }
            return true
        }
        return false
    }

    fun myLogin() {
        try {
            preferences.edit().putBoolean("hide_offline", true).apply()
        } catch (e: Exception) {
            e.message
        }

    }

    fun getSelectedAccount(): Account? {
        return if (xmppConnectionService != null) {
            val jid: Jid
            jid = try {
                if (Config.DOMAIN_LOCK != null) {
                    Jid.ofEscaped(
                        MySharedPreferences.Companion.getSharedprefInstance()
                            .getLoginData().mobileNumber + "@" + MySharedPreferences.Companion.getSharedprefInstance()
                            .getChatip(), Config.DOMAIN_LOCK, null
                    )
                } else {
                    Jid.ofEscaped(
                        mySharedPreferences!!.getValue(
                            AppConstants.loggedInUserNumber
                        ) + "@" + MySharedPreferences.Companion.getSharedprefInstance().getChatip()
                    )
//                    Jid.ofEscaped(
//                        MySharedPreferences.Companion.getSharedprefInstance()
//                            .getLoginData().mobileNumber + "@" +
//                                MySharedPreferences.Companion.getSharedprefInstance().getXSip()
//                    )
                }
            } catch (e: IllegalArgumentException) {
                return null
            }
            val service = xmppConnectionService ?: return null
            service.findAccountByJid(jid)
        } else {
            null
        }
    }


    private fun openBatteryOptimizationDialogIfNeeded() {
        if (isOptimizingBattery && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && preferences.getBoolean(
                getBatteryOptimizationPreferenceKey(), true
            )
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.battery_optimizations_enabled)
            builder.setMessage(
                getString(
                    R.string.battery_optimizations_enabled_dialog, getString(R.string.name_shield)
                )
            )
            builder.setPositiveButton(R.string.next) { dialog, which ->
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                val uri = Uri.parse("package:$packageName")
                intent.data = uri
                try {
                    startActivityForResult(intent, REQUEST_BATTERY_OP)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        this, R.string.device_does_not_support_battery_op, Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.setOnDismissListener { dialog: DialogInterface? -> setNeverAskForBatteryOptimizationsAgain() }
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    private fun setNeverAskForBatteryOptimizationsAgain() {
        preferences.edit().putBoolean(getBatteryOptimizationPreferenceKey(), false).apply()
    }


    private fun getBatteryOptimizationPreferenceKey(): String? {
        @SuppressLint("HardwareIds") val device = Settings.Secure.getString(
            contentResolver, Settings.Secure.ANDROID_ID
        )
        return "show_battery_optimization" + (device ?: "")
    }


}


