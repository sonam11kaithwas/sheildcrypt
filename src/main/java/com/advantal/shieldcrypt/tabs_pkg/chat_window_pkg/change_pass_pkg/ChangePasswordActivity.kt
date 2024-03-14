package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.change_pass_pkg

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.Config
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityChangePasswordBinding
import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.change_pass_pkg.change_pass_model.ChangePassModel
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.change_pass_pkg.change_pass_model.PasswordPolicyResModel
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.xmpp.Jid
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase
import net.gotev.sipservice.SipServiceCommand

@AndroidEntryPoint
class ChangePasswordActivity : XmppActivity() {
    val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: ActivityChangePasswordBinding
    var loginModel = MySharedPreferences.getSharedprefInstance().getLoginData()
    private var policyItemList = ArrayList<PasswordPolicyResModel>()
    private var isVisibleOldPass = true
    private var isVisibleNewPass = true
    private var isVisibleConfirmPass = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        handledOnClik()

        binding.icBackarrow.setOnClickListener {
            //MyApp.getAppInstance().showToastMsg("test")
            onBackPressed()
//            Navigation.findNavController(it)
//                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
        }

        getPasswordPolicy()

        mainViewModel.responceCallBack.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.CHANGE_PASSWORD_REQ -> {
                                //MyApp.getAppInstance().showToastMsg("Change Password")
                                binding.edtOldPassword.setText("")
                                binding.edtNewPassword.setText("")
                                binding.edtConfirmPass.setText("")

                                try {
                                    if (xmppConnectionService != null) {
                                        xmppConnectionService?.logoutAndSave(true)
                                        xmppConnectionService?.deletedatabaseBackend()
                                        var accoutn = getSelectedAccount()
                                        if (accoutn != null)
                                            xmppConnectionService?.deleteAccount(accoutn)
                                    }
                                } catch (e: Exception) {
                                }

                                MySharedPreferences.getSharedprefInstance()
                                    .setAutoLoginStatus(false)
                                clearLocalDetailsAfterLogout()
                                MySharedPreferences.getSharedprefInstance().setLoginData("")
                                try {
                                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                                        ?.contactDao()?.delete()
                                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                                        ?.groupDao()?.delete()


                                } catch (e: Exception) {
                                }


                            }
                            RequestApis.PASSWORD_POLICY_REQ -> {
                                // MyApp.getAppInstance().showToastMsg("Password Policy")
                                val gson = Gson()
                                val itemType =
                                    object : TypeToken<ArrayList<PasswordPolicyResModel>>() {}.type
                                policyItemList = gson.fromJson(users.data, itemType)
                                setPrivacyPolicy()
                            }
                            else -> {}
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
        })
    }

    private fun setPrivacyPolicy() {
        if (policyItemList != null && policyItemList.size > 0) {
            for (policyRow in policyItemList) {
                if (policyRow.number == 1) {
                    if (policyRow.minPasswordLength > 0) {
                        binding.txtMinNumChar.visibility
                        binding.txtMinNumChar.text =
                            "" + getString(R.string.minimum_number_of_characters) + " " + policyRow.minPasswordLength
                    }

                    if (policyRow.lowerCaseletters > 0) {
                        binding.txtMinNumLowerChar.visibility
                        binding.txtMinNumLowerChar.text =
                            "" + getString(R.string.minimum_number_of_lowercase_characters) + " " + policyRow.lowerCaseletters
                    }

                    if (policyRow.number > 0) {
                        binding.txtMinNumNumericChar.visibility
                        binding.txtMinNumNumericChar.text =
                            "" + getString(R.string.minimum_number_of_numeric_characters) + " " + policyRow.number
                    }

                    if (policyRow.specialCharacters > 0) {
                        binding.txtMinNumSpecialChar.visibility
                        binding.txtMinNumSpecialChar.text =
                            "" + getString(R.string.minimum_number_of_special_characters) + " " + policyRow.specialCharacters
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.toolbar.text = resources.getString(R.string.change_password)
    }

    fun getPasswordPolicy() {
        // mainViewModel.callGetApiWithToken(Gson().toJson(""), RequestApis.passwordPolicy,RequestApis.PASSWORD_POLICY_REQ)
        mainViewModel.getDataFromServerWithoutAuth(
            "",
            RequestApis.passwordPolicy,
            RequestApis.PASSWORD_POLICY_REQ
        )
    }

    private fun doChangePassword() {
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                ChangePassModel(
                    loginModel.mobileNumber,
                    binding.edtNewPassword.text.toString().trim()/*,
                    binding.edtOldPassword.text.toString().trim()*/
                )
            ), RequestApis.changePassword, RequestApis.CHANGE_PASSWORD_REQ
        )
    }

    private fun handledOnClik() {
        binding.btnResetPassword.setOnClickListener {
            /*if (binding.edtOldPassword.text.toString().equals("")){
                MyApp.getAppInstance().showToastMsg(getString(R.string.please_enter_your_old_password))
            } else*/ if (binding.edtNewPassword.text.toString().equals("")) {
            MyApp.getAppInstance().showToastMsg(getString(R.string.please_enter_your_new_password))
        } else if (binding.edtConfirmPass.text.toString().equals("")) {
            MyApp.getAppInstance()
                .showToastMsg(getString(R.string.please_enter_your_confirm_password))
        } else if (loginModel.mobileNumber.equals("")) {
            MyApp.getAppInstance()
                .showToastMsg(getString(R.string.please_enter_your_confirm_password))
        } else if (!binding.edtNewPassword.text.toString()
                .equals(binding.edtConfirmPass.text.toString())
        ) {
            MyApp.getAppInstance()
                .showToastMsg(getString(R.string.new_password_and_confirm_password_should_be_same))
        } else {
            // MyApp.getAppInstance().showToastMsg("Success")
            doChangePassword()
        }
        }

        binding.imgOldPassIndicator.setOnClickListener {
            if (isVisibleOldPass) {
                // hide password
                binding.edtOldPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.edtOldPassword.text?.let { it1 -> binding.edtOldPassword.setSelection(it1.length) }
                binding.imgOldPassIndicator.setImageResource(R.drawable.ic_password_closed_eye)
                isVisibleOldPass = false
            } else {
                // show password
                binding.edtOldPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.edtOldPassword.text?.let { it1 -> binding.edtOldPassword.setSelection(it1.length) }
                binding.imgOldPassIndicator.setImageResource(R.drawable.ic_password_open_eye)
                isVisibleOldPass = true
            }
        }

        binding.imgNewPassIndicator.setOnClickListener {
            if (isVisibleNewPass) {
                // hide password
                binding.edtNewPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.edtNewPassword.text?.let { it1 -> binding.edtNewPassword.setSelection(it1.length) }
                binding.imgNewPassIndicator.setImageResource(R.drawable.ic_password_closed_eye)
                isVisibleNewPass = false
            } else {
                // show password
                binding.edtNewPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.edtNewPassword.text?.let { it1 -> binding.edtNewPassword.setSelection(it1.length) }
                binding.imgNewPassIndicator.setImageResource(R.drawable.ic_password_open_eye)
                isVisibleNewPass = true
            }
        }

        binding.imgConfirmPassIndicator.setOnClickListener {
            if (isVisibleConfirmPass) {
                // hide password
                binding.edtConfirmPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edtConfirmPass.text?.let { it1 -> binding.edtConfirmPass.setSelection(it1.length) }
                binding.imgConfirmPassIndicator.setImageResource(R.drawable.ic_password_closed_eye)
                isVisibleConfirmPass = false
            } else {
                // show password
                binding.edtConfirmPass.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edtConfirmPass.text?.let { it1 -> binding.edtConfirmPass.setSelection(it1.length) }
                binding.imgConfirmPassIndicator.setImageResource(R.drawable.ic_password_open_eye)
                isVisibleConfirmPass = true
            }
        }
    }


    fun clearLocalDetailsAfterLogout() {
        // remove account which is registered in the app
        try {
            SipServiceCommand.resetAccount(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // mySharedPreferences.clearSharedPrefernces(activity)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    override fun refreshUiReal() {

    }

    override fun onBackendConnected() {

    }

    fun getSelectedAccount(): Account? {
        return if (xmppConnectionService != null) {
            val jid: Jid
            jid = try {
                if (Config.DOMAIN_LOCK != null) {
                    Jid.ofEscaped(
                        MySharedPreferences.Companion.getSharedprefInstance()
                            .getLoginData().mobileNumber + "@"
                                + MySharedPreferences.Companion.getSharedprefInstance().getChatip(),
                        Config.DOMAIN_LOCK,
                        null
                    )
                } else {
                    Jid.ofEscaped(
                        MySharedPreferences.Companion.getSharedprefInstance()
                            .getLoginData().mobileNumber + "@" +
                                MySharedPreferences.Companion.getSharedprefInstance().getChatip()
                    )
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

}