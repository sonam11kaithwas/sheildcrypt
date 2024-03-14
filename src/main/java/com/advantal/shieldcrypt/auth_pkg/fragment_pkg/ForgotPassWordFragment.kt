package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentForgotPassWordBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.CallbackAlertDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPassWordFragment : Fragment() {
    lateinit var binding: FragmentForgotPassWordBinding
    val mainViewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentForgotPassWordBinding.inflate(inflater, container, false)

        binding.forgotPassBtn.setOnClickListener {
            initiateVerification()
        }

        binding.icBackarrow.setOnClickListener {
//            Navigation.findNavController(binding.forgotPassBtn)
//                .navigate(R.id.action_forgotpass_to_login)
        }


//        (activity as AuthenticationActivity).
        mainViewModel.responceCallBack.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtills.closeProgressDialog()
                    it.data?.let { users ->
//                        Navigation.findNavController(binding.forgotPassBtn)
//                            .navigate(R.id.action_forgotpass_to_login)
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


        return binding.root
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

    private fun initiateVerification() {
        if (!binding.nameEdt.text.toString().trim().isEmpty()) {
//            (activity as AuthenticationActivity?)?.
//            (activity as AuthenticationActivity).

            //Replace with your email validation condition.
            if (!AppConstants.isValidString(binding.nameEdt.text.toString().trim())) {
                showDialogs(AppConstants.PLEASE_ENTER_VALID_EMAIL_ID)
            } else {

            }
//            mainViewModel.featchDataFromServerWithoutAuth(
//                Gson().toJson(
//                    ForgotPasswrodModel(binding.nameEdt.text.toString().trim(),
//                        false)
//                ), RequestApis.forgot_password,RequestApis.FORGOT_PASSWORD_REQ
//            )
        } else {
            showDialogs(AppConstants.EMAILERRORMSG)
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


}