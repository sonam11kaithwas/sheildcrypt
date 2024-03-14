package com.advantal.shieldcrypt.auth_pkg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.advantal.shieldcrypt.utils_pkg.AppConstants

/**
 * Created by Sonam on 16-06-2022 13:27.
 */
class AuthViewModel : ViewModel() {
    private var loginValidationMutableList: MutableLiveData<String> = MutableLiveData()
    val readErrorMsgs : LiveData<String> get() = loginValidationMutableList

    fun verifyUsrNmPass(usrNm: String, pass: String) {
        if (usrNm == "" && pass == "") {
            loginValidationMutableList.postValue(AppConstants.NAMEPASSERRORMSG)
        } else if (usrNm == "") {
            loginValidationMutableList.postValue(AppConstants.NAMEERRORMSG)
        } else if (pass == "") {
            loginValidationMutableList.postValue(AppConstants.PASSERRORMSG)
        } else {
            loginValidationMutableList.postValue("")
        }
    }
}