package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentSendOtpBinding
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.network_pkg.Communicator
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.auth_pkg.model.SendOtpResponse
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.AppValidation
import com.advantal.shieldcrypt.utils_pkg.MyApp
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SendOtpFragment : Fragment(), View.OnClickListener {
    val mainViewModel: MainViewModel by viewModels()
    private lateinit var comm: Communicator
    lateinit var binding: FragmentSendOtpBinding
    lateinit var stringEdit: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSendOtpBinding.inflate(inflater, container, false)
//        comm = requireActivity() as Communicator

        binding.useUserId.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_fragment_sendotp_to_fragment_login)
        }

//        binding.icBackarrow.setOnClickListener {
//            requireActivity().onBackPressed()
//        }

        binding.icBackarrow.setOnClickListener {

            Navigation.findNavController(it)
                .navigate(R.id.action_fragment_sendotp_to_fragment_login)
        }

        binding.nextButton.setOnClickListener {
            initiateverificationOtp()
            // comment by prashant to check the flow of otp button
//            if (AppValidation.isValidPhoneNumber(
//                    binding.countryCodeLable.text.toString().trim(),
//                    binding.phoneNumberTxt.text.toString().trim())
//                //added by prashant to call api
//
//
//            )
//                Navigation.findNavController(it).navigate(R.id.action_sendOtpFragment_to_verifyNoFragment)
//            else
//                 MyApp.getAppInstance().showToastMsg(AppConstants.PHONEERRORMSG)

        }

//        clickListners()



        mainViewModel.responceCallBack.observe(
            requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.SENT_OTP_USERD_REQ -> {
//                                    MyApp.getAppInstance().showToastMsg(it.data.toString())

                                    if (users!=null && users.data!=null) {


                                       var jsonObject = JSONObject(users.data)
                                        Log.e( "otpmessag: ",jsonObject.toString() )
                                        var msg = jsonObject.get("messageotp").equals(null)
                                        Log.e( "otpmessagprahant: ",msg.toString() )


                                        if (msg==true){

                                                MyApp.getAppInstance().showToastMsgLong("Number Is Not Registered. Kindly Contact Your Administrator !")

//                                            MyApp.getAppInstance().showToastMsg(it.message.toString())
//                                            MyApp.getAppInstance().showToastMsg(it.data.toString())


                                        }
                                        else{
                                            AppConstants.MYOTOP = users.data.toString().trim()
                                            Navigation.findNavController(binding.nextButton)
                                                .navigate(R.id.action_sendOtpFragment_to_verifyNoFragment)

                                        }

                                          }
                                    else{
//                                        MyApp.getAppInstance().showToastMsg(it.data.toString())
                                    }

                                }
                            }

                        }

                    }
                    Status.LOADING -> {
                        AppUtills.setProgressDialog(requireActivity())
                    }
                    Status.ERROR -> {
//                        MyApp.getAppInstance().showToastMsg(it.message.toString())
                        AppUtills.closeProgressDialog()
//                        Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                    }
                }
            })

//        stringEdit = binding.phoneNumberTxt.text.toString()
//        AppConstants.MYPHONENO = stringEdit

            return binding.root//        return inflater.inflate(R.layout.fragment_send_otp, container, false)


    }

    private fun initiateverificationOtp() {
        stringEdit = binding.phoneNumberTxt.text.toString()
        AppConstants.MYPHONENO = stringEdit

        if (AppValidation.isValidPhoneNumber(
                binding.countryCodeLable.text.toString().trim(),
                binding.phoneNumberTxt.text.toString().trim()
            )

        ) {
            mainViewModel.getVerificationFromOtp(
                VerificationOtpModel(
//                "9893151877"
                    binding.phoneNumberTxt.text.toString()


                ), RequestApis.SENT_OTP_USERD_REQ
            )
        }
//            Navigation.findNavController(binding.nextButton).navigate(R.id.action_sendOtpFragment_to_verifyNoFragment)
        else
            MyApp.getAppInstance().showToastMsg(AppConstants.PHONEERRORMSG)



    }


//    private fun clickListners() {
//        binding.toolbarBar.icBackarrow.visibility = View.GONE
//        binding.toolbarBar.toolbar.text = getString(R.string.enter_your_phone_number)
//    }

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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                /*clean Current FRAGMENT*/
                Navigation.findNavController(requireView()).navigateUp()
            }
        }
    }
}