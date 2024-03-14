package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.advantal.shieldcrypt.Config
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.auth_pkg.AuthViewModel
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity
import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModelForDb
import com.advantal.shieldcrypt.databinding.FragmentLoginBinding
import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.service.BackgroundConnectionService
import com.advantal.shieldcrypt.service.ContactSyncingService
import com.advantal.shieldcrypt.service.SyncMyContacts2
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.utils.Resolver
import com.advantal.shieldcrypt.utils.TorServiceUtils
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.CallbackAlertDialog
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.xmpp.Jid
import com.advantal.shieldcrypt.xmpp.XmppConnection
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private var viewModel: AuthViewModel? = null
    private val mainViewModel: MainViewModel by viewModels()

    //    lateinit var syncingService: SyncMyContacts2
    private val syncingService: SyncMyContacts2 by viewModels()

    private var isVisiblePass = true
    private var mAccount: Account? = null
    private val mInitMode = false
    private val mFetchingAvatar = false

    private val mUsernameMode = Config.DOMAIN_LOCK != null
    private val mForceRegister: Boolean? = null
    private val mShowOptions = false
    private val REQUEST_ORBOT = 0xff22
    private var activity: XmppActivity? = null


    @Inject
    lateinit var networkHelper: NetworkHelper

    //ContactSyncingService mBoundService
    var mBoundService: ContactSyncingService? = null

    var mServiceBound = false

    var myServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            mServiceBound = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder: ContactSyncingService.MyBinder = service as ContactSyncingService.MyBinder
            mBoundService = myBinder.service
            mServiceBound = true


//            syncingService = SyncMyContacts2()
            GlobalScope.launch {
                syncingService.callApp()
            }

            AppConstants.runContactSyncing = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        var viewModelFactory = AuthViewModelFactory.createFactory(requireActivity())
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]



        binding.loginBtn.setOnClickListener {
            initiateVerification()
        }

        binding.icBackarrow.setOnClickListener {

            Navigation.findNavController(binding.forgotPassBtn)
                .navigate(R.id.action_loginFragment_to_welcome)
        }
        binding.forgotPassBtn.setOnClickListener {
//            Navigation.findNavController(it)
//                .navigate(R.id.action_loginFragment_to_forgot_password)
        }
        binding.usePhoneNumTxt.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_fragment_login_to_fragment_sendotp)
        }
        binding.imgPassIndicator.setOnClickListener {
            if (isVisiblePass) {
                // hide password
                binding.passEdt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.passEdt.text?.let { it1 -> binding.passEdt.setSelection(it1.length) }
                binding.imgPassIndicator.setImageResource(R.drawable.ic_password_closed_eye)
                isVisiblePass = false
            } else {
                // show password
                binding.passEdt.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.passEdt.text?.let { it1 -> binding.passEdt.setSelection(it1.length) }
                binding.imgPassIndicator.setImageResource(R.drawable.ic_password_open_eye)
                isVisiblePass = true
            }
        }

        mainViewModel.responceCallBack.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtills.closeProgressDialog()
                    it.data?.let { users ->
                        Log.e("ssssssssssssss:", "ssssssssssssss:---data")

                        when (users.requestCode) {
                            RequestApis.USER_LOGIN_REQ -> {
                                val model = Gson().fromJson(users.data, LoginResModel::class.java)
                                val modelforDb =
                                    Gson().fromJson(users.data, LoginResModelForDb::class.java)

                                model.password = binding.passEdt.text.toString()
                                modelforDb.password = binding.passEdt.text.toString()

                                MySharedPreferences.getSharedprefInstance()
                                    .setLoginData(Gson().toJson(model))

                                MySharedPreferences.getSharedprefInstance()
                                    .setChatip(model.xmpip.toString())

                                MySharedPreferences.getSharedprefInstance()
                                    .setXSip(model.sipip.toString())

                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    "loginUserFirstName", model.firstName.toString()
                                )
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    "loginUserLastName", model.lastName.toString()
                                )

                                if (model.sipip.toString() != null && !model.sipip.toString()
                                        .isEmpty()
                                ) {
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        SharedPrefrence.SIPACCOUNTID,
                                        "sip:" + model.mobileNumber.toString() + "@" + model.sipip.toString()
                                    )
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        AppConstants.sipIpDynamic, model.sipip.toString()
                                    )
                                } else {
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        SharedPrefrence.SIPACCOUNTID,
                                        "sip:" + model.mobileNumber.toString() + "@" + "shieldcrypt.co.in"
                                    )
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        AppConstants.sipIpDynamic, "shieldcrypt.co.in"
                                    )
                                }
                                if (model.sipport.toString() != null && !model.sipport.toString()
                                        .isEmpty()
                                ) {
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        AppConstants.sipPortDynamic, model.sipport.toString()
                                    )
                                } else {
                                    SharedPrefrence.getInstance(requireActivity()).setValue(
                                        AppConstants.sipPortDynamic, "6060"
                                    )
                                }
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    AppConstants.loggedInUserNumber, model.mobileNumber.toString()
                                )
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    AppConstants.userName, model.mobileNumber.toString()
                                )
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    AppConstants.userPassword, model.password.toString()
                                )

                                MySharedPreferences.getSharedprefInstance()
                                    .setLoginData(Gson().toJson(model))
//                                        .setLoginData(users.data)
                                Log.e("Login responce", Gson().toJson(model))
                                startContactSyncing()
                                MySharedPreferences.getSharedprefInstance().setAutoLoginStatus(true)


                                try {
                                    ((requireActivity() as AuthenticationActivity)).myLogin()
                                } catch (e: Exception) {
                                }


                                createXamppConnection()
                                Navigation.findNavController(binding.loginBtn)
                                    .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
                            }
                        }
                    }
                }

                Status.LOADING -> {
                    AppUtills.setProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    AppUtills.closeProgressDialog()
                    Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                }
            }
        })



        if (XMPPConnectionListener.mConnection != null) {
            if (XMPPConnectionListener.mConnection!!.isConnected) {
                XMPPConnectionListener.mConnection!!.disconnect()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*clean Current FRAGMENT*/
                    requireActivity().finish()
                    Navigation.findNavController(binding.forgotPassBtn)
                        .navigate(R.id.action_loginFragment_to_welcome)
                }
            })
    }

    fun startService() {
        val serviceIntent = Intent(activity, BackgroundConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            activity?.startForegroundService(serviceIntent)
        } else {
            activity?.startService(serviceIntent)
        }
    }

    private fun verifyUsrNmPass(usrNm: String, pass: String): Boolean {
        var flag = false
        if (usrNm == "" && pass == "") {
            flag = true
            showDialogs(AppConstants.NAMEPASSERRORMSG)
        } else if (usrNm == "") {
            flag = true
            showDialogs(AppConstants.NAMEERRORMSG)
        } else if (pass == "") {
            flag = true
            showDialogs(AppConstants.PASSERRORMSG)
        }
        return flag
    }

    private fun initiateVerification() {
        if (!verifyUsrNmPass(
                binding.nameEdt.text.toString().trim(), binding.passEdt.text.toString().trim()
            )
        ) {
            mainViewModel.getLoginDataFromServer(
                (LoginReqModel(
                    binding.nameEdt.text.toString().trim(), binding.passEdt.text.toString().trim()
                )), RequestApis.USER_LOGIN_REQ
            )
        }

        mainViewModel.mFinished.observe(
            requireActivity()
        ) {
            if (it) Navigation.findNavController(binding.root)
                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
        }

        mainViewModel.mFinished.observe(
            requireActivity()
        ) {
            if (it) Navigation.findNavController(binding.root)
                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
        }
    }

    private fun showDialogs(msg: String) {
        AppUtills.setDialog(requireActivity(),
            msg,
            requireActivity().resources.getString(R.string.ok),
            object : CallbackAlertDialog {
                override fun onPossitiveCall() {


                }

                override fun onNegativeCall() {

                }
            })
    }


    /*********start contact syncing**********/
    fun startContactSyncing() {
        try {
//            Navigation.findNavController(binding.loginBtn)
//                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
            if (!mServiceBound) {
                AppConstants.runContactSyncing = true
                val intent = Intent(requireActivity(), ContactSyncingService::class.java)
                activity?.startService(intent)
                activity?.bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE)
                mServiceBound = true

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    private fun createXamppConnection() {

        val password = binding.passEdt.text.toString()
        val wasDisabled = mAccount != null && mAccount!!.status == Account.State.DISABLED
        val accountInfoEdited: Boolean = accountInfoEdited()
        if (mInitMode && mAccount != null) {
            mAccount!!.setOption(Account.OPTION_DISABLED, false)
        }

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
//                                jid = Jid.ofEscaped("9589967838" + "@" +
                //                                MySharedPreferences.getSharedprefInstance().getLoginData().xmpip, getUserModeDomain(), null);
                jid = Jid.ofEscaped(
                    MySharedPreferences.getSharedprefInstance()
                        .getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance()
                        .getLoginData().xmpip, /*getUserModeDomain()*/"", null
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
            binding.nameEdt.requestFocus()
            return
        } catch (e: IllegalArgumentException) {
            binding.nameEdt.requestFocus()
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
                    Account.OPTION_MAGIC_CREATE, mAccount!!.password.contains(password)
                )
            }
            mAccount!!.jid = jid
            mAccount!!.port = numericPort
            mAccount!!.hostname = hostname
            //                binding.accountJidLayout.setError(null);
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
            updateSaveButton()
//            updateAccountInformation(true)
        }
    }

    protected fun accountInfoEdited(): Boolean {
        return if (mAccount == null) {
            false
        } else jidEdited() || mAccount!!.password != binding.passEdt.text.toString()
        //                ("admin");
    }


    fun jidEdited(): Boolean {
        val unmodified: String
        unmodified = if (mUsernameMode) {
            mAccount!!.jid.escapedLocal
        } else {
            mAccount!!.jid.asBareJid().toEscapedString()
        }
        return unmodified != binding.nameEdt.text.toString() + "@" + MySharedPreferences.getSharedprefInstance()
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

    protected fun updateSaveButton() {
        val accountInfoEdited = accountInfoEdited()
        if (accountInfoEdited && !mInitMode) {
//            this.binding.loginBtn.setText(R.string.account_status_connecting);
            binding.loginBtn.isEnabled = true
        } else if (mAccount != null && (mAccount!!.status == Account.State.CONNECTING || mAccount!!.status == Account.State.REGISTRATION_SUCCESSFUL || mFetchingAvatar)) {
            binding.loginBtn.isEnabled = false
            binding.loginBtn.setText(R.string.account_status_connecting)
        } else if (mAccount != null && mAccount!!.status == Account.State.DISABLED && !mInitMode) {
            binding.loginBtn.isEnabled = false
            binding.loginBtn.setText(R.string.account_status_connecting)
        } else if (torNeedsInstall(mAccount)) {
            binding.loginBtn.isEnabled = false
            binding.loginBtn.setText(R.string.account_status_connecting)
        } else if (torNeedsStart(mAccount)) {
            binding.loginBtn.isEnabled = false
            binding.loginBtn.setText(R.string.account_status_connecting)
        } else {
            binding.loginBtn.isEnabled = true
            binding.loginBtn.setText(R.string.account_status_connecting)
            if (!mInitMode) {
                if (mAccount != null && mAccount!!.isOnlineAndConnected) {
                    binding.loginBtn.setText(R.string.save)
                    if (!accountInfoEdited) {
                        binding.loginBtn.isEnabled = true
                    }
                } else {
                    val connection = if (mAccount == null) null else mAccount!!.xmppConnection
                    val url =
                        if (connection != null && mAccount!!.status == Account.State.PAYMENT_REQUIRED) connection.redirectionUrl else null
                    if (url != null) {
//                        this.binding.saveButton.setText(R.string.open_website);
                    } else if (inNeedOfSaslAccept()) {
//                        this.binding.saveButton.setText(R.string.accept);
                    } else {
//                        this.binding.saveButton.setText(R.string.connect);
                    }
                }
            } else {
                val connection = if (mAccount == null) null else mAccount!!.xmppConnection
                val url =
                    if (connection != null && mAccount!!.status == Account.State.REGISTRATION_WEB) connection.redirectionUrl else null
                //                if (url != null && this.binding.accountRegisterNew.isChecked() && !accountInfoEdited) {
//                    this.binding.saveButton.setText(R.string.open_website);
//                } else {
//                    this.binding.saveButton.setText(R.string.next);
//                }
            }
        }
    }

    private fun torNeedsInstall(account: Account?): Boolean {
        return account != null && account.status == Account.State.TOR_NOT_AVAILABLE && !TorServiceUtils.isOrbotInstalled(
            requireActivity()
        )
    }

    private fun torNeedsStart(account: Account?): Boolean {
        return account != null && account.status == Account.State.TOR_NOT_AVAILABLE
    }


}