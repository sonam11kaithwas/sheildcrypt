package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.advantal.shieldcrypt.databinding.FragmentVerifyNoBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.service.ContactSyncingService
import com.advantal.shieldcrypt.service.SyncMyContacts2
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.utils_pkg.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VerifyNoFragment : Fragment(), View.OnClickListener {
    val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentVerifyNoBinding

    private val syncingService: SyncMyContacts2 by viewModels()

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


//    private var inputText: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerifyNoBinding.inflate(inflater, container, false)

//        inputText = arguments?.getString("input_txt")
//        binding.editText1.setText(inputText)
        val phone = AppConstants.MYPHONENO
        Log.e("prashant: ", phone.toString())
        val word = AppConstants.MYOTOP
        val array = Array(word.length) { word[it].toString() }

        var finalword = word.replace("[^0-9]".toRegex(), "")
        Log.e("prashant: ", finalword.toString())

        var i = 0
        array.forEach {
            println(it)
            if (i == 15) {
                binding.editText1.setText(it)
            } else if (i == 16) {
                binding.editText2.setText(it)
            } else if (i == 17) {
                binding.editText3.setText(it)
            } else if (i == 18) {
                binding.editText4.setText(it)
            }
            i = i + 1


        }
        mainViewModel.getVerifyFromOtp(
            VerifyOtpModel(
//                "9893151877"
                phone, finalword
            ), RequestApis.VERIFY_OTP
        )


        initUI()

//        binding.nextButton.setOnClickListener {
//            Navigation.findNavController(it)
//                .navigate(R.id.action_verifyNoFragment_to_profileinfoFragment)
//        }

//        viewOtp(i)
//         comment timer for 60 second
        object : CountDownTimer(30000, 1000) {

            // Callback function, fired on regular interval
            override fun onTick(millisUntilFinished: Long) {
                binding.otpTimer.text = "" + millisUntilFinished / 1000
            }

            // Callback function, fired
            // when the time is up
            override fun onFinish() {
                binding.otpTimer.text = "Resend"
                binding.mobNumLableTxt.text =
                    "Can't send an SMS with your code because you've tried to register " + phone + " recently. Request a wait before requesting an SMS. Wrong number?"
            }
        }.start()

        binding.otpTimer.setOnClickListener {
            mainViewModel.getVerificationFromOtp(
                VerificationOtpModel(
//                "9893151877"
                    phone
                ), RequestApis.SENT_OTP_USERD_REQ
            )
        }


        mainViewModel.responceCallBack.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.SENT_OTP_USERD_REQ -> {
//
//                                    AppConstants.MYOTOP = users.data.toString().trim()
//                                    Navigation.findNavController(binding.nextButton)
//                                        .navigate(R.id.action_sendOtpFragment_to_verifyNoFragment)
//                                    MyApp.getAppInstance()
//                                        .showToastMsg(users.data.toString().trim())
                                val resendword = users.data.toString().trim()

                                val array = Array(resendword.length) { resendword[it].toString() }

                                var i = 0
                                array.forEach {
                                    println(it)
                                    if (i == 15) {
                                        binding.editText1.setText(it)
                                    } else if (i == 16) {
                                        binding.editText2.setText(it)
                                    } else if (i == 17) {
                                        binding.editText3.setText(it)
                                    } else if (i == 18) {
                                        binding.editText4.setText(it)
                                    }
                                    i = i + 1
                                }
                            }


                            RequestApis.VERIFY_OTP -> {

                                val model = Gson().fromJson(users.data, LoginResModel::class.java)
//                                    model.password=binding.passEdt.text.toString()
                                if (model.xmpip.toString() != null && !model.xmpip.toString()
                                        .equals("")
                                ) {
                                    MySharedPreferences.getSharedprefInstance()
                                        .setChatip(model.xmpip.toString())
                                }

//                                    model.xmpip="voip.vortexvt.com"
                                Gson().toJson(model)

                                MySharedPreferences.getSharedprefInstance()
                                    .setLoginData(Gson().toJson(model))

                                MySharedPreferences.getSharedprefInstance()
                                    .setXSip(model.sipip.toString())


//
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(
//                                        SharedPrefrence.SIPACCOUNTID,
//                                        "sip:" + model.mobileNumber.toString() + "@" + model.sipip.toString())
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(AppConstants.sipIpDynamic,
//                                        model.sipip.toString())
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(AppConstants.sipPortDynamic,
//                                        model.sipport.toString())
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(AppConstants.loggedInUserNumber,
//                                        model.mobileNumber.toString())
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(AppConstants.userName,
//                                        model.mobileNumber.toString())
//                                    SharedPrefrence.getInstance(requireActivity()).setValue(AppConstants.userPassword,
//                                        model.password.toString())

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
                                    AppConstants.loggedInUserNumber,
                                    model.mobileNumber.toString()
                                )
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    AppConstants.userName, model.mobileNumber.toString()
                                )
                                SharedPrefrence.getInstance(requireActivity()).setValue(
                                    AppConstants.userPassword, model.password.toString()
                                )
                                Log.e("Login responce", Gson().toJson(model))
//                                        .setLoginData(users.data)
                                MySharedPreferences.getSharedprefInstance().setAutoLoginStatus(true)

//                                    if (networkHelper.isNetworkConnected())
//                                        AppUtills.isXmppWorkScheduled(requireActivity())

                                startContactSyncing()
//                                    MyApp.getAppInstance()
//                                        .showToastMsg(users.data.toString().trim())
                                Navigation.findNavController(binding.root)
                                    .navigate(R.id.action_verifyNoFragment_to_profileinfoFragment)

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

        return binding.root
    }

//    private fun viewOtp(i: Int){
//            binding.editText1.setText()
//    }

    private fun clickListners() {
//        binding.toolbarBar.icBackarrow.visibility = View.GONE
//        binding.toolbarBar.toolbar.text = getString(R.string.enter_your_phone_number)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*clean Current FRAGMENT*/
                    requireActivity().finish()
                }
            })

    }

    fun initUI() {
        binding.editText1.addTextChangedListener(
            GenericTextWatcher(
                binding.editText1, binding.editText2, requireActivity()
            )
        )
        binding.editText2.addTextChangedListener(
            GenericTextWatcher(
                binding.editText2, binding.editText3, requireActivity()
            )
        )
        binding.editText3.addTextChangedListener(
            GenericTextWatcher(
                binding.editText3, binding.editText4, requireActivity()
            )
        )
        binding.editText4.addTextChangedListener(
            GenericTextWatcher(
                binding.editText4, null, requireActivity()
            )
        )
//        comment by PRASHANT LAL on 26 sept 2022 by assigned task on ASANA by sonam ma'am
//        binding.editText5.addTextChangedListener(
//            GenericTextWatcher(
//                binding.editText5,
//                binding.editText6,
//                requireActivity()
//            )
//        )
//        binding.editText6.addTextChangedListener(
//            GenericTextWatcher(
//                binding.editText6,
//                null,
//                requireActivity()
//            )
//        )
        ////For prev index
        binding.editText1.setOnKeyListener(VerifyNoFragment.EventListener(binding.editText1, null))
        binding.editText2.setOnKeyListener(
            VerifyNoFragment.EventListener(
                binding.editText2, binding.editText1
            )
        )
        binding.editText3.setOnKeyListener(
            VerifyNoFragment.EventListener(
                binding.editText3, binding.editText2
            )
        )
        binding.editText4.setOnKeyListener(
            VerifyNoFragment.EventListener(
                binding.editText4, binding.editText3
            )
        )
//        comment by PRASHANT LAL on 26 sept 2022 as assigned task on ASANA by sonam ma'am
//        binding.editText5.setOnKeyListener(
//            VerifyNoFragment.EventListener(
//                binding.editText5,
//                binding.editText4
//            )
//        )
//        binding.editText6.setOnKeyListener(
//            EventListener(
//                binding.editText6,
//                binding.editText5
//            )
//        )

        clckEvent()
    }

    private fun clckEvent() {
        binding.smsLayout.setOnClickListener(this)
        binding.callMeLayout.setOnClickListener(this)
    }

    class EventListener : View.OnKeyListener {
        var mCurrentView: EditText? = null
        var mPrev: EditText? = null

        constructor(mCurrentView: EditText?, mPrev: EditText?) {
            this.mCurrentView = mCurrentView
            this.mPrev = mPrev
//            }
        }

        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && mCurrentView?.id != R.id.editText1 && mCurrentView?.text.toString()
                    .isBlank()
            ) {
                //If current is empty then previous EditText's number will also be deleted
                if (mPrev?.text != null) {
                    mPrev?.requestFocus()
                    return true
                }
            }
            return false

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.sms_layout -> {
                MyApp.getAppInstance().showToastMsg("Sms Send.")
            }
            R.id.call_me_layout -> {
                MyApp.getAppInstance().showToastMsg("Not calling.")
            }
        }
    }

    /*********start contact syncing**********/
    fun startContactSyncing() {

//        var intent=Intent(activity,MainActivityFor::class.java)
//        startActivity(intent)
        try {
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

}