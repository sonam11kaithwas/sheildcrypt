package com.advantal.shieldcrypt.service

/**
 * Created by Sonam on 30-09-2022 11:08.
// */

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advantal.shieldcrypt.network_pkg.*
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel
import com.advantal.shieldcrypt.ui.model.ResponseItem
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import database.my_database_pkg.db_table.MyAppDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncMyContacts2 @Inject constructor(
    private val mainRepository: MainRepository, private val networkHelper: NetworkHelper
) : ViewModel() {
    var page: Int = 0
    var size: Int = 500
    val refreshListCallBack = MutableLiveData(false)
    val refreshListCallBackError = MutableLiveData<String>()

    fun featchDataFromServerWithAuth(requestStr: String, requestUri: String) {
        val jsonParser = JsonParser()
        val jsonObject = jsonParser.parse(requestStr).asJsonObject
        var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
        var v2 = "Bearer "

        Log.e("Token", "" + v)
        viewModelScope.launch {
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

                            if (!it.body()?.get("data").toString().isEmpty()) {
                                val listType =
                                    object : TypeToken<List<ContactDataModel?>?>() {}.type
                                val contactDataList: List<ContactDataModel> =
                                    Gson().fromJson<List<ContactDataModel>>(
                                        it.body()?.get("data").toString(), listType
                                    )

                                if (contactDataList.isNotEmpty()) {
                                    MyAppDataBase.getUserDataBaseAppinstance(
                                        MyApp.getAppInstance()
                                    )?.contactDao()?.addAllUser(contactDataList)
                                    page++
                                    size += 120
                                    callApp()
                                } else {
                                    refreshListCallBack.postValue(true)
                                }
                            }

                            Log.e("Server Responce", it.body()?.get("data").toString())
                        } else {
                            refreshListCallBackError.postValue(it.body()?.get("message").toString())
                        }

                    } else {
                    }
                }
            }
        }

    }

    fun callApp() {
        val hashMap = HashMap<String, Any>()
        hashMap["page"] = page
        hashMap["size"] = size
        hashMap["search"] = ""
        Log.e("init Block", "")
        featchDataFromServerWithAuth(
            Gson().toJson(hashMap), RequestApis.getAllContacts
        )
        sysnGrpList()
    }

    private fun sysnGrpList() {
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
            if (networkHelper.isNetworkConnected()) {
                flow {
                    val response = mainRepository.getDataFromServerWithToken2(
                        "$v2 $v", Integer.parseInt(
                            MySharedPreferences.getSharedprefInstance().getLoginData().userid
                        )
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
                            if (!it.body()?.get("data").toString().isEmpty()) {
                                val listType =
                                    object : TypeToken<List<ResponseItem?>?>() {}.type
                                val contactDataList: List<ResponseItem> =
                                    Gson().fromJson<List<ResponseItem>>(
                                        it.body()?.get("data").toString(), listType
                                    )

                                if (contactDataList.isNotEmpty()) {
                                    MyAppDataBase.getUserDataBaseAppinstance(
                                        MyApp.getAppInstance()
                                    )?.groupDao()?.addAllGroupMember(contactDataList)

                                } else {
                                    refreshListCallBack.postValue(true)
                                }
                            }

                        }

                    } else {

                    }
                }
            }
        }
    }


}


