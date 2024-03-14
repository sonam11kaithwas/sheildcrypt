package com.advantal.shieldcrypt.network_pkg

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.advantal.shieldcrypt.network_pkg.RequestApis.VIEW_GRP_USER
import com.advantal.shieldcrypt.network_pkg.RequestApis.VIEW_PROFILE_BY_ID_REQ
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

/**
 * Created by Sonam on 28-07-2022 11:19.
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
//    @Inject
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val responce = MutableLiveData<Resource<ResponceModel>>()
    val responceCallBack: LiveData<Resource<ResponceModel>>
        get() = responce
    val mFinished = MutableLiveData(false)


    fun deleteDataFromserver(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject

        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "


        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.deleteDataFromserver(
                        requestUri, jsonObject, "$v2 $v"
                    )
                    emit(response)
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        } else {
                            responce.postValue(
                                Resource.error(
                                    it.body()?.get("message").toString(), null
                                )

                            )
                        }
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.body()?.get("message").toString(), null
                            )

                        )
                    }
                }
            }
        }
    }

    /***************This Function fetch data from server without TOKEN******************/
    fun featchDataFromServerWithoutAuth(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.featchDataFromServerWithoutAuth(
                        requestUri, jsonObject
                    )
                    emit(response)
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
                    }
                }
            }
        }

    }


    /***************This Function fetch data from server WITH........Authorization ******************/
    fun featchDataFromServerWithAuth(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "
        Log.e("Token", "" + v)
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.featchDataFromServerWithAuth(
                        requestUri, jsonObject, "$v2 $v"
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                            Log.e("Server Responce", it.body()?.get("data").toString())
                        }

                        if (!requestCode.equals(VIEW_PROFILE_BY_ID_REQ) || !requestCode.equals(
                                VIEW_GRP_USER
                            )
                        ) {
                            MyApp.getAppInstance().showToastMsgLong(
                                it.body()?.get("message")?.asString ?: "Login Successfully!"
                            )
                        }

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
                    }
                }
            }
        }
    }

    fun callGetApiWithToken(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        // val jsonObject = jsonParser.parse(requestStr).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance()
            .getLoginData().token//"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJyYXZpQDEyMyIsImV4cCI6MTY2Mzc1NDUzMSwiaWF0IjoxNjYzNzM2NTMxfQ.ByIvb4y8OQ-urAjA8Xc_dmZYbfIw-jj134eEUaElYttbMpQ51ZoA2ECV-8ucId7E-z9Jkf3dbFO-TudRZW-xww"
        var v2 = "Bearer "
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.getDataFromServerWithToken(
                        requestUri, "$v2 $v"
                        // jsonObject,
//                        AppUtills.getTokenForAPiCall()
//                        MySharedPreferences.getSharedprefInstance().getLoginData().token
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")
                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
//                        MyApp.getAppInstance().showToastMsg(
//                            it.body()?.get("message")?.asString ?: "Login Successfully!"
//                        )
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )
                        )
//                        MyApp.getAppInstance().showToastMsg(it.errorBody().toString())
                    }
                }
            }
        }
    }


    /***************This Function fetch LOGIN data from server without TOKEN ******************/
    fun getLoginDataFromServer(loginReqModel: LoginReqModel, requestCode: Int) {
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                flow {
                    val response = mainRepository.getLoginDataFromServer(loginReqModel).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")
                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
                        MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString ?: "Login Successfully!"
                        )
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )
                        )
                        mFinished.value = false
                    }
                }
            } else {
                responce.postValue(Resource.error("No internet connection", null))
            }
        }
    }


    /***************This Function fetch PHONE NUMBER OTP from server without TOKEN******************/
    fun getVerificationFromOtp(
        verificationOtpModel: VerificationOtpModel,
        requestCode: Int,

        ) {
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {
                flow {
                    val response =
                        mainRepository.getPhoneNoVerification(verificationOtpModel).apply {
                            emit(this)
                        }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")
                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
                        /*MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString ?: "Login Successfully!"
                        )*/
                    } else {

                        var jsonObject = JSONObject(it.errorBody()?.string().toString())
                        if (jsonObject != null) {
                            MyApp.getAppInstance()
                                .showToastMsg(jsonObject.get("message").toString())
                        }

                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )
                        )
                    }
                }
            } else {
                responce.postValue(Resource.error("No internet connection", null))
            }
        }
    }

    /***************This Function fetch VERIFY OTP from server without TOKEN added by prashant 10 oct 2022 ******************/
    fun getVerifyFromOtp(
        verifyOtpModel: VerifyOtpModel,
        requestCode: Int,
    ) {
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.getVerifyOtp(verifyOtpModel).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")
                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
                        /*MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString ?: "Login Successfully!"
                        )*/
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )
                        )
                    }
                }
            } else {
                responce.postValue(Resource.error("No internet connection", null))
            }
        }
    }

    /***************This Function fetch data from server without TOKEN******************/
    fun getDataFromServerWithoutAuth(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        //val jsonObject = jsonParser.parse(requestStr).asJsonObject
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.getDataFromServerWithoutAuth(
                        requestUri
                    )
                    emit(response)
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }

                        /*MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString ?: "Login Successfully!"
                        )*/

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
//                        MyApp.getAppInstance().showToastMsg(it.errorBody().toString())
                    }
                }
            }
        }

    }

    fun getDataFromAutoQueryServerWithoutAuth(
        requestStr: String, requestUri: String, requestCode: Int
    ) {
        val jsonParser = JsonParser()
        //  val jsonObject = jsonParser.parse(requestStr).asJsonObject
        viewModelScope.launch {
            //responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.getDataFromAutoQueryServerWithoutAuth(
                        requestStr,
                    )
                    emit(response)
                }.flowOn(Dispatchers.IO).catch {
                    //  AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    // AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }

                        /*MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString ?: "Login Successfully!"
                        )*/

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
//                        MyApp.getAppInstance().showToastMsg(it.errorBody().toString())
                    }
                }
            }
        }

    }


    fun getDataFromAutoQueryServerWithAuth(requestStr: Int, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
//        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "
        Log.e("Token", "" + v)
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.getDataFromAutoQueryServerWithAuth(
//                        jsonObject,
                        "$v2 $v", requestStr
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                            Log.e("Server Responce", it.body()?.get("data").toString())
                        }

                        if (!requestCode.equals(VIEW_PROFILE_BY_ID_REQ)) {
//                            MyApp.getAppInstance().showToastMsg(
//                                it.body()?.get("message")?.asString ?: "Login Successfully!"
//                            )
                        }

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
//                        MyApp.getAppInstance().showToastMsg(it.errorBody().toString())
                    }
                }
            }
        }
    }

    fun featchDataFromServerWithAuthArray(
        requestStr: String, requestUri: String, requestCode: Int
    ) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonArray
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.featchDataFromServerWithAuthArray(
                        requestUri, jsonObject, "$v2 $v"
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                            Log.e("Server Responce", it.body()?.get("data").toString())
                        }

                        if (!requestCode.equals(VIEW_PROFILE_BY_ID_REQ)) {

                        }

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
                    }
                }
            }
        }
    }

    fun deleteFromAutoQueryServerWithAuth(
        requestStr: String, requestUri: String, requestCode: Int
    ) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.deleteDataFromServerWithToken(
                        /* requestUri,*/
                        jsonObject, "$v2 $v"
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                            Log.e("Server Responce", it.body()?.get("data").toString())
                        }

                        MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString.toString()
                        )

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
                    }
                }
            }
        }
    }


    fun callGetApiWithToken2(requestCode: Int) {
        var jsonObject = Gson().toJson(
            GroupListModel(
                Integer.parseInt(
                    MySharedPreferences.getSharedprefInstance().getLoginData().userid
                )
            )
        )
        val jsonParser = JsonParser()
        val jsonObjects = jsonParser.parse(jsonObject).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))
            if (networkHelper.isNetworkConnected()) {


                flow {
                    val response = mainRepository.getDataFromServerWithToken2(
                        "$v2 $v", Integer.parseInt(
                            MySharedPreferences.getSharedprefInstance().getLoginData().userid
                        )
//                        ,RequestApis.searchByUserId
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")
                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                        }
//                    MyApp.getAppInstance().showToastMsg(
//                        it.body()?.get("message")?.asString ?: "Login Successfully!"
//                    )
                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )
                        )
//                    MyApp.getAppInstance().showToastMsg(it.errorBody().toString())
                    }
                }
            }
        }
    }

    fun deleteMeetingWithoutAuth(requestStr: String, requestUri: String, requestCode: Int) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        /*var v = MySharedPreferences.getSharedprefInstance()
            .getLoginData().token
        var v2 = "Bearer "*/
        viewModelScope.launch {
            responce.postValue(Resource.loading(null))

            if (networkHelper.isNetworkConnected()) {

                flow {
                    val response = mainRepository.deleteMeetingWithoutAuth(
                        requestUri, jsonObject
                    ).apply {
                        emit(this)
                    }
                }.flowOn(Dispatchers.IO).catch {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "errro")
                    MyApp.getAppInstance().showToastMsg("Network Error")

                }.collect {
                    AppUtills.closeProgressDialog()
                    Log.e("Error", "responce")
                    if (it.isSuccessful) {
                        if (it.code() == 200 && it.body()?.get("status")?.asBoolean == true) {
                            responce.postValue(
                                Resource.success(
                                    ResponceModel(
                                        requestCode, it.body()?.get("data").toString()
                                    )
                                )
                            )
                            Log.e("Server Responce", it.body()?.get("data").toString())
                        }

                        MyApp.getAppInstance().showToastMsg(
                            it.body()?.get("message")?.asString.toString()
                        )

                    } else {
                        responce.postValue(
                            Resource.error(
                                it.errorBody().toString(), null
                            )

                        )
                    }
                }
            }
        }
    }

}