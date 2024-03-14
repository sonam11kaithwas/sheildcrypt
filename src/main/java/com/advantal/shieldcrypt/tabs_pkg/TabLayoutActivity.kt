package com.advantal.shieldcrypt.tabs_pkg


import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityTabLayoutBinding
import com.advantal.shieldcrypt.databinding.FragmentTabLayoutBinding
import com.advantal.shieldcrypt.meeting.activity.MeetingMainActivity
import com.advantal.shieldcrypt.service.BackgroundConnectionService
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.ViewPagerAdapter
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.WhatsAppContactsActivity
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.CallListFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.ChatListFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.SettingFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.StatusStoryActivity
import com.advantal.shieldcrypt.ui.AllContactActivity
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.google.android.material.tabs.TabLayoutMediator
import net.gotev.sipservice.*
import org.pjsip.PjCameraInfo2
import org.pjsip.pjsua2.pjsip_inv_state
import org.pjsip.pjsua2.pjsip_status_code


class TabLayoutActivity : AppCompatActivity()
    , View.OnClickListener {
    lateinit var binding: FragmentTabLayoutBinding
    private val tabsArray = arrayOf(
        "Chats", "Calls", "Settings"
    )

    var loginModel = MySharedPreferences.getSharedprefInstance().getLoginData()

    ///////////////// Sip Variables /////////////////
    private var mSipAccount: SipAccountData? = null
    private var placecall = false
    private val KEY_SIP_ACCOUNT = "sip_account"
    private var sipService: SipService? = null

    ////////////// End of Sip Variables //////////////
    ///////////// End of Bottom tab Variables ///////////////
    var mySharedPreferences: SharedPrefrence? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = FragmentTabLayoutBinding.inflate(layoutInflater)
        setupViewPager()

        binding.fab2.setOnClickListener {
            val intent = Intent(this, StatusStoryActivity::class.java)
            this?.startActivity(intent)
//            Navigation.findNavController(it)
//                .navigate(R.id.action_tablayout_to_statusfragment)
        }
        binding.meeting.setOnClickListener {
            val intent = Intent(this, MeetingMainActivity::class.java)
            this?.startActivity(intent)
        }
        val cm = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        PjCameraInfo2.SetCameraManager(cm)

        mySharedPreferences = SharedPrefrence.getInstance(this)
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

//        AppUtills.isXmppWorkScheduled(this)

        sipService = SipService()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT)
        }

        //        mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);
//        startContactSyncing();
     //   if (Utils.checkInternetConn(this)) onRegister()

      //  startService()

    }
    override fun onBackPressed() {
        binding.searchLayout.searchEdt.setText("")
        if (binding.viewPager.currentItem == 0) {
            if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(
                5
            )
            else this.finish()

        } else {
            if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(
                5
            )
            else
            // Otherwise, select the previous step.
                binding.viewPager.currentItem = 0
        }
        super.onBackPressed()
    }

   /* override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.e("soooooooooooo", "")
        // Inflate the layout for this fragment
        binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        setupViewPager()

        binding.fab2.setOnClickListener {
            val intent = Intent(this, StatusStoryActivity::class.java)
            this?.startActivity(intent)
//            Navigation.findNavController(it)
//                .navigate(R.id.action_tablayout_to_statusfragment)
        }
        binding.meeting.setOnClickListener {
            val intent = Intent(this, MeetingMainActivity::class.java)
            this?.startActivity(intent)
        }
        val cm = this.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        PjCameraInfo2.SetCameraManager(cm)

        mySharedPreferences = SharedPrefrence.getInstance(this)
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

        AppUtills.isXmppWorkScheduled(requireActivity())

        sipService = SipService()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT)
        }

        //        mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);
//        startContactSyncing();
        if (Utils.checkInternetConn(requireActivity())) onRegister()

        startService()

        return binding.root
    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mSipAccount != null) {
            outState.putParcelable(KEY_SIP_ACCOUNT, mSipAccount)
        }
    }

    private fun setupViewPager() {

//        super.getActivity().getSupportFragmentManager()
        val adapter = ViewPagerAdapter(
            this?.supportFragmentManager, this?.lifecycle!!
        )
        binding.viewPager.adapter = adapter


        binding.fab1.hide()


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabsArray[position]
        }.attach()

        binding.tabLayout.getTabAt(0)?.setText("Chats")?.setIcon(R.drawable.caht_ic)
        binding.tabLayout.getTabAt(1)?.setText("Calls")?.setIcon(R.drawable.ic_tab_call)
        binding.tabLayout.getTabAt(2)?.setText("Settings")?.setIcon(R.drawable.setting_ic)



        closeKeyboard(this, binding.searchLayout.searchEdt)

        intializeTabFirst()

        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.viewPager.currentItem == 0) {
//                    adapter.chatListFragment?.filterChatsList(s.toString())
                } else if (binding.viewPager.currentItem == 1) {
                    ///adapter.callListFragment?.filtercallsList(s.toString())
                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    private fun intializeTabFirst() {

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )


        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.tab_unselected
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
                this, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun intializeTabSecond() {
        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )

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
//        var intent = Intent(this, WhatsAppContactsActivity::class.java)
        var intent = Intent(this, AllContactActivity::class.java)
        startActivity(intent)
//        this.finish()
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
            this?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


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
                    this, "Clear Call Log", Toast.LENGTH_SHORT
                ).show()
            else Toast.makeText(
                this, "New Group", Toast.LENGTH_SHORT
            ).show()
            mypopupWindow.dismiss()

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
/*
    private val sipEvents: BroadcastEventReceiver = object : BroadcastEventReceiver() {
        override fun onRegistration(accountID: String, registrationStateCode: Int) {
            println("...........$accountID")
            Log.e("onRegistration1", "onRegistration1$this")
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
*/

    override fun onResume() {
        super.onResume()
        /*sipEvents.register(this)
        if (mSipAccount != null) {
            SipServiceCommand.getRegistrationStatus(this, mSipAccount!!.idUri)
        }
        val id: String = mySharedPreferences!!.getValue(SharedPrefrence.SIPACCOUNTID)
        loadConfiguredAccounts()

        SipServiceCommand.getRegistrationStatus(this, id)*/
    }

    override fun onDestroy() {
        super.onDestroy()
        /*startService()
        sipEvents.unregister(this)
        mySharedPreferences?.setValue(AppConstants.isAppKilled, AppConstants.trueValue)*/
    }

    fun Registersip(sipUsername: String?, sipPassword: String?) {
        val mSipAccount = SipAccountData()
        mSipAccount.setHost(mySharedPreferences!!.getValue(AppConstants.sipIpDynamic))
            .setPort(mySharedPreferences!!.getValue(AppConstants.sipPortDynamic)!!.toInt().toLong())
            .setTcpTransport(false) //                .setUsername(mySharedPreferences.getValue(AppConstants.userName))
            //                .setPassword(mySharedPreferences.getValue(AppConstants.userPassword))
            .setUsername(sipUsername).setPassword(sipPassword).realm =
            mySharedPreferences!!.getValue(AppConstants.sipIpDynamic)
        SipServiceCommand.setAccount(this, mSipAccount)
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
        //        SipServiceCommand.setAccount(this, mSipAccount);
        SipServiceCommand.setReRegisterAccount(this, mSipAccount)
        SipServiceCommand.getCodecPriorities(this)
    }

    private fun loadConfiguredAccounts() {
        val TAG = SipService::class.java.simpleName
        val PREFS_NAME = TAG + "prefs"

//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

//        String accounts = prefs.getString("accounts", "");
        val sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this)
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
        val serviceIntent = Intent(this, BackgroundConnectionService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    fun stopService() {
        val serviceIntent = Intent(this, BackgroundConnectionService::class.java)
        this.stopService(serviceIntent)
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

    companion object
}


/*, TabLayout.OnTabSelectedListener,
    View.OnClickListener {

    private lateinit var binding: ActivityTabLayoutBinding
    var sectionsPagerAdapter: SectionsPagerAdapter? = null
    private val onPageChangeListener: OnPageChangeListener? = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int, positionOffset: Float, positionOffsetPixels: Int
        ) {
            Log.d(
                "TAG",
                "onPageScrolled: Position: $position. Position Offset: $positionOffset. Position Offset Pixels: $positionOffsetPixels"
            )
        }

        override fun onPageSelected(position: Int) {
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
                    binding.fab1.show()



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

        override fun onPageScrollStateChanged(state: Int) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTabLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



        initalizeViews()
    }

    fun initalizeViews() {

        sectionsPagerAdapter = SectionsPagerAdapter(
            supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.viewPager.offscreenPageLimit = 3
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.currentItem = 0


        binding.fab1.hide()
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.getTabAt(0)?.setText("Chats")?.setIcon(R.drawable.caht_ic)
        binding.tabLayout.getTabAt(1)?.setText("Calls")?.setIcon(R.drawable.ic_tab_call)
        binding.tabLayout.getTabAt(2)?.setText("Settings")?.setIcon(R.drawable.setting_ic)

        intializeTabFirst()


        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)


    }

    private fun intializeTabThird() {

        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )



        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )
    }

    private fun intializeTabSecond() {
        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )

    }

    private fun intializeTabFirst() {

        binding.tabLayout.getTabAt(0)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.white
            ), PorterDuff.Mode.SRC_IN
        )


        binding.tabLayout.getTabAt(1)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )
        binding.tabLayout.getTabAt(2)!!.icon!!.setColorFilter(
            ContextCompat.getColor(
                this@TabLayoutActivity, com.advantal.shieldcrypt.R.color.tab_unselected
            ), PorterDuff.Mode.SRC_IN
        )


        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if (s.toString().isNotEmpty()) {
                    if (binding.viewPager.currentItem == 0) {
                        sectionsPagerAdapter?.firstFragment?.filterChatsList(s.toString())
                    } else if (binding.viewPager.currentItem == 1) {
                        sectionsPagerAdapter?.secondFragment?.filtercallsList(s.toString())
                    }
//                }

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


    }

    override fun onBackPressed() {
        binding.searchLayout.searchEdt.setText("")
        if (binding.viewPager.currentItem == 0) {
            if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(5)
            else this.finish()
        } else {
            if (binding.searchParentLayout.visibility == View.VISIBLE) hideShowTitleAndSearchBar(5)
            else
            // Otherwise, select the previous step.
                binding.viewPager.currentItem = 0
        }
    }

    override fun onStart() {
        super.onStart()
        onPageChangeListener?.let { binding.viewPager.addOnPageChangeListener(it) }
    }

    override fun onStop() {
        super.onStop()
        onPageChangeListener?.let { binding.viewPager.removeOnPageChangeListener(it) }
    }


    class SectionsPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        var mFragmentList = ArrayList<Fragment>()
        var firstFragment: ChatListFragment? = null
        var secondFragment: CallListFragment? = null


        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return NUMBER_OF_FRAGMENTS
        }

        companion object {
            const val NUMBER_OF_FRAGMENTS = 3
        }

        init {
            firstFragment = ChatListFragment()
            secondFragment = CallListFragment()
            mFragmentList.add(firstFragment!!)
            mFragmentList.add(secondFragment!!)
            mFragmentList.add(SettingFragment())
        }
    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        binding.viewPager.currentItem = tab!!.position

//        super.onTabSelected(tab)
        val tabIconColor = ContextCompat.getColor(this, com.advantal.shieldcrypt.R.color.white)
        tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
//        super.onTabUnselected(tab)
        val tabIconColor = ContextCompat.getColor(this, com.advantal.shieldcrypt.R.color.colorPrimary)
        tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }


    private fun showCustomPopUp() {
        var mypopupWindow: PopupWindow
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: ViewGroup =
            inflater.inflate(com.advantal.shieldcrypt.R.layout.popup_layout, null, false) as ViewGroup


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
                    this@TabLayoutActivity, "Clear Call Log", Toast.LENGTH_SHORT
                ).show()
            else Toast.makeText(
                this@TabLayoutActivity, "New Group", Toast.LENGTH_SHORT
            ).show()
            mypopupWindow.dismiss()

        }
        mypopupWindow.showAtLocation(
            binding.searchView, Gravity.TOP, 300, 200
        )
//        mypopupWindow.contentView.id
        mypopupWindow.contentView.setOnClickListener {


            mypopupWindow.dismiss()

        }


    }


    override fun onClick(view: View?) {
        when (view?.id) {
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
        var intent = Intent(this, WhatsAppContactsActivity::class.java)
        startActivity(intent)
//        this.finish()
    }

    private fun hideShowTitleAndSearchBar(type: Int) {
        when (type) {
            1, 2, 5 -> {
                binding.menuLayout.visibility = View.VISIBLE
                binding.searchParentLayout.visibility = View.GONE
                binding.searchView
            }


            3 -> {
                binding.menuLayout.visibility = View.GONE
                binding.searchParentLayout.visibility = View.GONE
            }
            4 -> {
                binding.searchParentLayout.visibility = View.VISIBLE
                binding.menuLayout.visibility = View.GONE

            }
        }

    }


}
*/