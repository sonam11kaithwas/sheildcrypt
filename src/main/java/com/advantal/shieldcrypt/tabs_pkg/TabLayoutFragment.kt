package com.advantal.shieldcrypt.tabs_pkg

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import androidx.viewpager2.widget.ViewPager2
import com.advantal.shieldcrypt.Config
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity.Companion.FLAGNOTIFY
import com.advantal.shieldcrypt.databinding.FragmentTabLayoutBinding
import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.entities.Conversation
import com.advantal.shieldcrypt.meeting.activity.MeetingMainActivity
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.service.BackgroundConnectionService
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.ViewPagerAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.privacy_help_pkg.WebViewActivity
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.StatusStoryActivity
import com.advantal.shieldcrypt.ui.AllContactActivity
import com.advantal.shieldcrypt.ui.ConversationFragment
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.ui.XmppFragment
import com.advantal.shieldcrypt.ui.grp_create_contact.ContactListActivity2
import com.advantal.shieldcrypt.ui.interfaces.OnConversationSelected
import com.advantal.shieldcrypt.utils.Resolver
import com.advantal.shieldcrypt.utils.TorServiceUtils
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.MyApp.Companion.getAppInstance
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences.Companion.getSharedprefInstance
import com.advantal.shieldcrypt.xmpp.Jid
import com.advantal.shieldcrypt.xmpp.XmppConnection
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import database.my_database_pkg.db_table.MyAppDataBase.Companion.getUserDataBaseAppinstance
import net.gotev.sipservice.*
import org.pjsip.PjCameraInfo2
import org.pjsip.pjsua2.pjsip_inv_state
import org.pjsip.pjsua2.pjsip_status_code


class TabLayoutFragment : XmppFragment()
//    Fragment()
    , View.OnClickListener, OnConversationSelected {
    lateinit var binding: FragmentTabLayoutBinding
    private val tabsArray = arrayOf(
        "Chats", "Calls", "Settings"
    )
    private var conversationFragment: ConversationFragment? = null


    private var isVisiblePass = true
    private var mAccount: Account? = null
    private val mInitMode = false
    private val mFetchingAvatar = false

    private val mUsernameMode = Config.DOMAIN_LOCK != null
    private val mForceRegister: Boolean? = null
    private val mShowOptions = false
    private val REQUEST_ORBOT = 0xff22
    private var activity: XmppActivity? = null

    ///////////////// Sip Variables /////////////////
    private var mSipAccount: SipAccountData? = null
    private var placecall = false
    private val KEY_SIP_ACCOUNT = "sip_account"
    private var sipService: SipService? = null

    ////////////// End of Sip Variables //////////////
    ///////////// End of Bottom tab Variables ///////////////
    var mySharedPreferences: SharedPrefrence? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*clean Current FRAGMENT*/
                    binding.searchLayout.searchEdt.setText("")
                    if (binding.viewPager.currentItem == 0) {
                        if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(
                            5
                        )
                        else requireActivity().finish()

                    } else {
                        if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(
                            5
                        )
                        else
                        // Otherwise, select the previous step.
                            binding.viewPager.currentItem = 0
                    }
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.e("soooooooooooo", "")
        // Inflate the layout for this fragment
        binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        setupViewPager()


        binding.dialCallBtn.setOnClickListener {
            doCall()
        }
        binding.doMeetingBtn.setOnClickListener {
            doMeeting()
        }

        binding.fab2.setOnClickListener {
            val intent = Intent(activity, StatusStoryActivity::class.java)
            activity?.startActivity(intent)
//            Navigation.findNavController(it)
//                .navigate(R.id.action_tablayout_to_statusfragment)
        }
        binding.meeting.setOnClickListener {
            val intent = Intent(activity, MeetingMainActivity::class.java)
            activity?.startActivity(intent)
        }
        val cm = requireActivity().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        PjCameraInfo2.SetCameraManager(cm)

        mySharedPreferences = SharedPrefrence.getInstance(requireActivity())
        mySharedPreferences?.setValue(AppConstants.isAppKilled, AppConstants.falseValue)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        hideShowTitleAndSearchBar(1)

                        binding.fab.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources, R.drawable.ic_sms_fab_btn, null
                            )
                        )
                        binding.fab.show()
                        binding.fab1.hide()



                        intializeTabFirst()
                    }
                    1 -> {
                        hideShowTitleAndSearchBar(2)

                        binding.fab.hide()
                        binding.fab1.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources, R.drawable.ic_call_fab_btn, null
                            )
                        )
//                        binding.fab1.show()
                        binding.fab1.hide()


                        intializeTabSecond()
                    }
                    2 -> {
                        hideShowTitleAndSearchBar(3)
                        binding.fab.hide()
                        binding.fab1.hide()

                        intializeTabThird()

                    }
                }
            }

        })


        if (XMPPConnectionListener.mConnection != null) {
            if (XMPPConnectionListener.mConnection!!.isConnected) {
                XMPPConnectionListener.mConnection!!.disconnect()
            }
        }

//        AppUtills.isXmppWorkScheduled(requireActivity())

        sipService = SipService()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT)
        }

        //        mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);
//        startContactSyncing();
        if (Utils.checkInternetConn(requireActivity())) onRegister()

        startService()

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mSipAccount != null) {
            outState.putParcelable(KEY_SIP_ACCOUNT, mSipAccount)
        }
    }

    private fun setupViewPager() {

//        super.getActivity().getSupportFragmentManager()
        val adapter = ViewPagerAdapter(
            activity?.supportFragmentManager, activity?.lifecycle!!
        )
        binding.viewPager.adapter = adapter


        binding.fab1.hide()


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        binding.tabLayout.getTabAt(0)?.setText("Chats")?.setIcon(R.drawable.caht_ic)
        binding.tabLayout.getTabAt(1)?.setText("Calls")?.setIcon(R.drawable.ic_tab_call)
        binding.tabLayout.getTabAt(2)?.setText("Settings")?.setIcon(R.drawable.setting_ic)




        closeKeyboard(requireActivity(), binding.searchLayout.searchEdt)

        intializeTabFirst()

        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.viewPager.currentItem == 0) {
//                    adapter.chatListFragment?.fi(s.toString())
                } else if (binding.viewPager.currentItem == 1) {
                   // adapter.callListFragment?.filtercallsList(s.toString())
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }


    private fun intializeTabFirst() {

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )


        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )



        clickListners()
    }

    private fun clickListners() {
        binding.fab.setOnClickListener(this)
        binding.fab1.setOnClickListener(this)
        binding.threeDotsImg.setOnClickListener(this)
        binding.searchView.setOnClickListener(this)
        binding.searchLayout.icBackarrow.setOnClickListener(this)
//        binding.
    }


    private fun intializeTabThird() {

        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun intializeTabSecond() {
        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                requireActivity(), com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("", "")
        if (context is XmppActivity) {
            Log.e("", "")
            this.activity = context
        } else {
            throw IllegalStateException("Trying to attach fragment to activity that is not an XmppActivity")
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab1 -> {

            }
            R.id.fab -> {
                selectedContactOnWhatsApp()
            }
            R.id.three_dots_img -> {
                showCustomPopUp()
            }
            R.id.search_view -> {
                hideShowTitleAndSearchBar(4)
            }
            R.id.ic_backarrow -> {
                hideShowTitleAndSearchBar(5)
            }
        }
    }


    private fun selectedContactOnWhatsApp() {
        var intent = Intent(requireActivity(), AllContactActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
//        activity?.finish()
    }

    private fun hideShowTitleAndSearchBar(type: Int) {
        when (type) {
            1, 2, 5 -> {
                binding.menuLayout.visibility = View.VISIBLE
                binding.searchParentLayout.visibility = View.GONE
                binding.threeDotsImg.visibility = View.VISIBLE
                binding.searchView.visibility = View.VISIBLE
                binding.fab2.visibility = View.VISIBLE
                binding.meeting.visibility = View.VISIBLE
            }


            3 -> {
                binding.menuLayout.visibility = View.VISIBLE
                binding.searchParentLayout.visibility = View.GONE

                binding.threeDotsImg.visibility = View.INVISIBLE
                binding.searchView.visibility = View.INVISIBLE
                binding.fab2.visibility = View.INVISIBLE
                binding.meeting.visibility = View.INVISIBLE
            }
            4 -> {
                binding.searchParentLayout.visibility = View.VISIBLE
                binding.menuLayout.visibility = View.GONE

            }
        }

    }

    private fun showCustomPopUp() {
        var mypopupWindow: PopupWindow
        val inflater: LayoutInflater =
            activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: ViewGroup = inflater.inflate(
            com.advantal.shieldcrypt.R.layout.popup_layout, null, false
        ) as ViewGroup


        mypopupWindow = PopupWindow(
            view, 380,
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT, true
        )
        var new_group = view.findViewById(com.advantal.shieldcrypt.R.id.new_group) as TextView
        var actionForPopUp = false
        if (binding.viewPager.currentItem == 0) {
            new_group.text = getString(R.string.popup_menu_new_grp)
            actionForPopUp = false
        } else if (binding.viewPager.currentItem == 1) {
            new_group.text = getString(R.string.popup_menu_clear_log)
            actionForPopUp = true
        }
        new_group.setOnClickListener {
            if (actionForPopUp)

                Toast.makeText(
                    requireActivity(), "Clear Call Log", Toast.LENGTH_SHORT
                ).show()
            else {
                getSelectedContactFromList()
                mypopupWindow.dismiss()
            }


        }
        mypopupWindow.showAtLocation(
            binding.searchView, Gravity.TOP, 300, 200
        )
//        mypopupWindow.contentView.id
        mypopupWindow.contentView.setOnClickListener {


            mypopupWindow.dismiss()

        }


    }

    // broadcast receive when get sip registration response
    private val sipEvents: BroadcastEventReceiver = object : BroadcastEventReceiver() {
        override fun onRegistration(accountID: String, registrationStateCode: Int) {
            println("...........$accountID")
            Log.e("onRegistration1", "onRegistration1$this" + " --> " + registrationStateCode)
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
//                            Toast.makeText(DrawerActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                if (placecall) {
                    placecall = false
                }
            } else {
//                Toast.makeText(DrawerActivity.this, "Unregistered", Toast.LENGTH_SHORT).show();
                if (placecall) {
                    placecall = false
                }
                Registersip(
                    mySharedPreferences!!.getValue(AppConstants.userName),
                    mySharedPreferences!!.getValue(AppConstants.userPassword)
                )
            }
        }

        override fun onCallState(
            accountID: String,
            callID: Int,
            callStateCode: Int,
            callStatusCode: Int,
            connectTimestamp: Long
        ) {
            super.onCallState(accountID, callID, callStateCode, callStatusCode, connectTimestamp)
            //            Log.e("calstate1","onRegistration121"+sipEvents1);
            if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING || callStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING || callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING || callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            }
            if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                Log.e("callwhenclick", "mutecall")
            } else {
                Log.e("callwhenclick", "mutecall")
            }
            System.out.println("))))))) sipip " + mySharedPreferences!!.getValue(AppConstants.sipIpDynamic))
        }
    }

    override fun onResume() {
        super.onResume()
        sipEvents.register(requireActivity())
        FLAGNOTIFY = 1
        loadConfiguredAccounts()
        if (mSipAccount != null) {
            SipServiceCommand.getRegistrationStatus(requireActivity(), mSipAccount!!.idUri)
        }
        val id: String = mySharedPreferences!!.getValue(SharedPrefrence.SIPACCOUNTID)

        SipServiceCommand.getRegistrationStatus(requireActivity(), id)
    }

    override fun onDestroy() {
        super.onDestroy()
        startService()
        sipEvents.unregister(requireActivity())
        mySharedPreferences?.setValue(AppConstants.isAppKilled, AppConstants.trueValue)
    }

    override fun onBackendConnected() {
        refresh()
//        setupViewPager()
//        createXamppConnection()
    }

    override fun refresh() {
        Log.e("", "")
    }

//    override fun refresh() {
//
//    }

    fun Registersip(sipUsername: String?, sipPassword: String?) {
        val mSipAccount = SipAccountData()
        mSipAccount.setHost(mySharedPreferences!!.getValue(AppConstants.sipIpDynamic))
            .setPort(mySharedPreferences!!.getValue(AppConstants.sipPortDynamic)!!.toInt().toLong())
            .setTcpTransport(false) //                .setUsername(mySharedPreferences.getValue(AppConstants.userName))
            //                .setPassword(mySharedPreferences.getValue(AppConstants.userPassword))
            .setUsername(sipUsername).setPassword(sipPassword).realm =
            mySharedPreferences!!.getValue(AppConstants.sipIpDynamic)
        SipServiceCommand.setAccount(requireActivity(), mSipAccount)
    }

    // Request for the SIP registration
    fun onRegister() {
        mSipAccount = SipAccountData()
        System.out.println("))))))) sipip " + mySharedPreferences!!.getValue(AppConstants.sipIpDynamic))
        System.out.println("))))))) sipport " + mySharedPreferences!!.getValue(AppConstants.sipPortDynamic))
        mSipAccount!!.setHost(mySharedPreferences!!.getValue(AppConstants.sipIpDynamic))
            .setPort(mySharedPreferences!!.getValue(AppConstants.sipPortDynamic)!!.toInt().toLong())
            .setTcpTransport(false)
            .setUsername(mySharedPreferences!!.getValue(AppConstants.userName))
            .setPassword(mySharedPreferences!!.getValue(AppConstants.userPassword)).realm =
            mySharedPreferences!!.getValue(AppConstants.sipIpDynamic)
        mySharedPreferences!!.setValue(
            SharedPrefrence.SIPSERVER, mySharedPreferences!!.getValue(AppConstants.sipIpDynamic)
        )
        mySharedPreferences!!.setintValue(
            SharedPrefrence.SIPPORT,
            mySharedPreferences!!.getValue(AppConstants.sipPortDynamic)!!.toInt()
        )
        mySharedPreferences!!.setValue(
            SharedPrefrence.SIPUSERNAME, mySharedPreferences!!.getValue(AppConstants.userName)
        )
        mySharedPreferences!!.setValue(
            SharedPrefrence.SIPPASS, mySharedPreferences!!.getValue(AppConstants.userPassword)
        )
        //        share.setValue(SharedPrefrence.SIPUSERNAME, "1000");
//        share.setValue(SharedPrefrence.SIPPASS, "1000");
        mySharedPreferences!!.setValue(
            SharedPrefrence.SIPREALM, mySharedPreferences!!.getValue(AppConstants.sipIpDynamic)
        )
        SipServiceCommand.setAccount(requireActivity(), mSipAccount)
        SipServiceCommand.setReRegisterAccount(requireActivity(), mSipAccount)
        SipServiceCommand.getCodecPriorities(requireActivity())
    }

    private fun loadConfiguredAccounts() {
        val TAG = SipService::class.java.simpleName
        val PREFS_NAME = TAG + "prefs"

//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

//        String accounts = prefs.getString("accounts", "");
        val sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireActivity())
        var mConfiguredAccounts: List<SipAccountData> = ArrayList()
        mConfiguredAccounts = sharedPreferencesHelper.configuredAccounts

        /*if (accounts.isEmpty()) {
            mConfiguredAccounts = new ArrayList<>();
        } else {
            Type listType = new TypeToken<ArrayList<SipAccountData>>() {
            }.getType();
            mConfiguredAccounts = new Gson().fromJson(accounts, listType);
        }*/
        val iterator = mConfiguredAccounts.iterator()
        while (iterator.hasNext()) {
            mSipAccount = iterator.next()
        }
    }

    ///Add by SV
    fun startService() {
        val serviceIntent = Intent(requireActivity(), BackgroundConnectionService::class.java)
        ContextCompat.startForegroundService(requireActivity(), serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(requireActivity(), BackgroundConnectionService::class.java)
        requireActivity().stopService(serviceIntent)
    }

    private fun closeKeyboard(context: Context, view: View?) {
        //val view = this.currentFocus
        if (view != null) {
            Handler().postDelayed({
                val imm =
                    context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }, 200)
        }
    }

    override fun onConversationSelected(conversation: Conversation?) {
        if (conversationFragment != null) conversationFragment = null
        openFragment(conversation!!) //, null);
    }

    private fun openFragment(conversation: Conversation) {

//        binding.lay.setVisibility(View.GONE)

//        activity.xmppConnectionService.getNotificationService().setOpenConversation(conversation)
        conversationFragment = ConversationFragment()
        conversationFragment!!.reInit(conversation, null)
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_tablayout_to_conversationfragment)
//        val fragmentManager: FragmentManager = getSupportFragmentManager()
//        fragmentManager.beginTransaction().replace(R.id.main_fragment, conversationFragment!!, null)
//            .setReorderingAllowed(false).addToBackStack(
//                conversationFragment!!.javaClass.name
//            ).commit()
    }


    private fun createXamppConnection() {
        val password = MySharedPreferences.getSharedprefInstance().getLoginData().password

        val wasDisabled = mAccount != null && mAccount!!.status == Account.State.DISABLED
        val accountInfoEdited: Boolean = accountInfoEdited()
        if (mInitMode && mAccount != null) {
            mAccount!!.setOption(Account.OPTION_DISABLED, false)
        }
//        requireActivity().xmppConnectionService
        if (mAccount != null && mAccount!!.status == Account.State.DISABLED && !accountInfoEdited) {
            mAccount!!.setOption(Account.OPTION_DISABLED, false)
            if (!activity?.xmppConnectionService?.updateAccount(mAccount)!!) {
                Toast.makeText(
                    requireActivity(), R.string.unable_to_update_account, Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        val registerNewAccount: Boolean
        if (mForceRegister != null) {
            registerNewAccount = mForceRegister
        } else {
            registerNewAccount =
                false //binding.accountRegisterNew.isChecked() && !Config.DISALLOW_REGISTRATION_IN_UI;
        }
        val connection: XmppConnection? = if (mAccount == null) null else mAccount!!.xmppConnection
        val startOrbot = mAccount != null && mAccount!!.status == Account.State.TOR_NOT_AVAILABLE
        if (startOrbot) {
            if (TorServiceUtils.isOrbotInstalled(requireActivity())) {
                TorServiceUtils.startOrbot(
                    requireActivity(), REQUEST_ORBOT
                )
            } else {
                TorServiceUtils.downloadOrbot(
                    requireActivity(), REQUEST_ORBOT
                )
            }
            return
        }
        if (inNeedOfSaslAccept()) {
            mAccount!!.resetPinnedMechanism()
            if (!activity?.xmppConnectionService?.updateAccount(mAccount)!!) {
                Toast.makeText(
                    requireActivity(), R.string.unable_to_update_account, Toast.LENGTH_SHORT
                ).show()
            }
            return
        }
        val openRegistrationUrl =
            registerNewAccount && !accountInfoEdited && mAccount != null && mAccount!!.status == Account.State.REGISTRATION_WEB
        val openPaymentUrl = mAccount != null && mAccount!!.status == Account.State.PAYMENT_REQUIRED
        val redirectionWorthyStatus = openPaymentUrl || openRegistrationUrl
        val url =
            if (connection != null && redirectionWorthyStatus) connection.redirectionUrl else null
        if (url != null && !wasDisabled) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))
                return
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    requireActivity(),
                    R.string.application_found_to_open_website,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }
        val jid: Jid
        try {
            if (mUsernameMode) {
//                                jid = Jid.ofEscaped("9589967838" + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().xmpip, getUserModeDomain(), null);
                jid = Jid.ofEscaped(
                    MySharedPreferences.getSharedprefInstance()
                        .getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance()
                        .getLoginData().xmpip, getUserModeDomain(), null
                )
            } else {
                jid = Jid.ofEscaped(
                    MySharedPreferences.getSharedprefInstance()
                        .getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance()
                        .getLoginData().xmpip
                )
                //                jid = Jid.ofEscaped("9589967838@"+MySharedPreferences.Companion.getSharedprefInstance().getXSip();
                Resolver.checkDomain(jid)
            }
        } catch (e: NullPointerException) {
            return
        } catch (e: IllegalArgumentException) {
            return
        }
        val hostname: String?
        var numericPort = 5222
        if (mShowOptions) {
            hostname = null
            val port: String? = null
            if (Resolver.invalidHostname(hostname)) {
                return
            }
            if (hostname != null) {
                try {
                    numericPort = port!!.toInt()
                    if (numericPort < 0 || numericPort > 65535) {
                        return
                    }
                } catch (e: NumberFormatException) {
                    return
                }
            }
        } else {
            hostname = null
        }
        if (mAccount != null) {
            if (mAccount!!.isOptionSet(Account.OPTION_MAGIC_CREATE)) {
                mAccount!!.setOption(
                    Account.OPTION_MAGIC_CREATE, mAccount!!.password.contains(password.toString())
                )
            }
            mAccount!!.jid = jid
            mAccount!!.port = numericPort
            mAccount!!.hostname = hostname
            mAccount!!.password = password
            mAccount!!.setOption(Account.OPTION_REGISTER, registerNewAccount)
            if (!activity?.xmppConnectionService?.updateAccount(mAccount)!!) {
                Toast.makeText(
                    requireActivity(), R.string.unable_to_update_account, Toast.LENGTH_SHORT
                ).show()
                return
            }
        } else {
            if (activity?.xmppConnectionService?.findAccountByJid(jid) != null) {
                return
            }
            mAccount = Account(jid.asBareJid(), password)
            mAccount!!.port = numericPort
            mAccount!!.hostname = hostname
            mAccount!!.setOption(Account.OPTION_REGISTER, registerNewAccount)
            activity?.xmppConnectionService?.createAccount(mAccount)
        }
        if (mAccount!!.isOnion) {
            Toast.makeText(
                requireActivity(), R.string.audio_video_disabled_tor, Toast.LENGTH_LONG
            ).show()
        }
        if (mAccount!!.isEnabled && !registerNewAccount && !mInitMode) {
//            activity?.finish()
        } else {

        }
    }

    protected fun accountInfoEdited(): Boolean {
        return if (mAccount == null) {
            false
        } else jidEdited() || mAccount!!.password != MySharedPreferences.getSharedprefInstance()
            .getLoginData().password
    }


    fun jidEdited(): Boolean {
        val unmodified: String
        unmodified = if (mUsernameMode) {
            mAccount!!.jid.escapedLocal
        } else {
            mAccount!!.jid.asBareJid().toEscapedString()
        }
        return unmodified != MySharedPreferences.getSharedprefInstance()
            .getLoginData().username + "@" + MySharedPreferences.getSharedprefInstance()
            .getLoginData().xmpip
    }

    private fun inNeedOfSaslAccept(): Boolean {
        return mAccount != null && mAccount!!.lastErrorStatus == Account.State.DOWNGRADE_ATTACK && mAccount!!.pinnedMechanismPriority >= 0 && !accountInfoEdited()
    }


    private fun getUserModeDomain(): String? {
        return if (mAccount != null && mAccount!!.jid.domain != null) {
            mAccount!!.server
        } else {
            Config.DOMAIN_LOCK
        }
    }

    // added by arvind for call from dail pad
    fun doCall() {
        val alertDialog = AlertDialog.Builder(requireActivity())
        //alertDialog.setTitle("Call")
        // alertDialog.setMessage("Enter number...")

        val input = EditText(requireActivity())
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.maxEms = 10
        input.height = 40
        input.hint = "Enter number..."
        input.inputType = InputType.TYPE_CLASS_NUMBER

        input.setPadding(20, 20, 20, 20)

        input.layoutParams = lp
        alertDialog.setView(input)
        // alertDialog.setIcon(R.drawable.key)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Call", null)
        alertDialog.setNegativeButton("No", null)

        val mAlertDialog = alertDialog.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            if (input.text.toString().trim().isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter your number", Toast.LENGTH_SHORT)
                    .show()
            } else if (input.text.toString().trim().length <= 9) {
                Toast.makeText(requireActivity(), "Please enter valid number", Toast.LENGTH_SHORT)
                    .show()
            } else if (input.text.toString().trim().length > 10) {
                Toast.makeText(requireActivity(), "Please enter valid number", Toast.LENGTH_SHORT)
                    .show()
            } else {
                mAlertDialog.cancel()
                //Toast.makeText(requireActivity(), "initiateCall...", Toast.LENGTH_SHORT).show()
                initiateCall(input.text.toString().trim())
            }
        }

        /*alertDialog.setNegativeButton(
            "NO"
        ) {
                dialog, which -> dialog.cancel()
        }*/
        // alertDialog.show()
    }

    fun doMeeting() {
        val alertDialog = AlertDialog.Builder(requireActivity())
        //alertDialog.setTitle("Call")
        // alertDialog.setMessage("Enter number...")

        val input = EditText(requireActivity())
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.maxEms = 10
        input.height = 40
        input.hint = "Enter room name..."
        input.inputType = InputType.TYPE_CLASS_NUMBER

        input.setPadding(20, 20, 20, 20)

        input.layoutParams = lp
        alertDialog.setView(input)
        // alertDialog.setIcon(R.drawable.key)
        alertDialog.setCancelable(false)
        alertDialog.setPositiveButton("Join", null)
        alertDialog.setNegativeButton("No", null)

        val mAlertDialog = alertDialog.create()
        mAlertDialog.show()
        val mPositiveButton = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        mPositiveButton.setOnClickListener {
            if (input.text.toString().trim().isEmpty()) {
                Toast.makeText(requireActivity(), "Please enter your room name", Toast.LENGTH_SHORT)
                    .show()
            }/* else if (input.text.toString().trim().length <= 9) {
                Toast.makeText(requireActivity(), "Please enter valid number", Toast.LENGTH_SHORT)
                    .show()
            } else if (input.text.toString().trim().length > 10) {
                Toast.makeText(requireActivity(), "Please enter valid number", Toast.LENGTH_SHORT)
                    .show()
            }*/ else {
                mAlertDialog.cancel()
                val intent = Intent (requireActivity(), WebViewActivity::class.java)
                intent.putExtra("selectedRoomName", ""+input.text.toString().trim())
                intent.putExtra("meetingBaseUrl", "https://192.168.8.109:7443/ofmeet/")
                startActivity(intent)
            }
        }

        /*alertDialog.setNegativeButton(
            "NO"
        ) {
                dialog, which -> dialog.cancel()
        }*/
        // alertDialog.show()
    }

    fun initiateCall(number: String) {
        Utils.hideKeyBoard(requireActivity())
        if (!Utils.checkInternetConn(requireActivity())) {
            Toast.makeText(
                requireActivity(), getString(R.string.internet_check), Toast.LENGTH_SHORT
            ).show()
        } else {
            if (checkValidation(number)) {
                if (SipAccount.activeCalls.size > 0) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.you_have_already_on_call),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onCall(number)
                }
            }
        }
    }

    fun checkValidation(number: String): Boolean {
        if (number.equals("", ignoreCase = true)) {
            Toast.makeText(
                requireActivity(), getString(R.string.please_enter_mobile), Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            return true
        }
    }

    fun onCall(number: String) {
        if (mySharedPreferences != null) {
            val id: String? = mySharedPreferences?.getValue(SharedPrefrence.SIPACCOUNTID)
            if (id != null && !id.isEmpty()) {
                AppConstants.TYPE = "CALL"
                /*if (number == null && number!!.isEmpty()) {
                    number = "*9000"
                }*/
                Log.e("go id", "dsda $id")
                Log.e("c", "no $number")
                SipServiceCommand.makeCall(requireActivity(), id, number)
            }
        }
    }

    private fun getSelectedContactFromList() {
        val intent = Intent(activity, ContactListActivity2::class.java)
        intent.putExtra(
            "contacts", Gson().toJson(
                getUserDataBaseAppinstance(getAppInstance())!!.contactDao().getAllNotes(
                    getSharedprefInstance().getLoginData().mobileNumber
                )
            )
        )
        startActivity(intent)
    }



}