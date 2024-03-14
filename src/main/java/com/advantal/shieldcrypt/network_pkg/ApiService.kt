package com.advantal.shieldcrypt.network_pkg

import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.advantal.shieldcrypt.sip.model.SendMessageResponseBean
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Sonam on 27-07-2022 16:25.
 */

interface ApiService {


    @POST()
    @Headers("Content-type:application/json")
    suspend fun featchDataFromServerWithoutAuth(//featchDataFromServerWithoutAuth
        @Url url: String, @Body jsonObject: JsonObject
    ): Response<JsonObject>

    @POST()
    @Headers("Content-type:application/json")
    suspend fun featchDataFromServerWithAuth(
        @Url url: String, @Body jsonObject: JsonObject, @Header("Authorization") Token: String
    ): Response<JsonObject>

    @POST("shieldcryptUserManagementMobile/user/loginUser")
    @Headers("Content-type:application/json")
    suspend fun getLoginDataFromServer(
        @Body jsonObject: LoginReqModel
    ): Response<JsonObject>

    @GET()
    @Headers("Content-type:application/json")
    suspend fun getDataFromServerWithToken(
        @Url url: String, @Header("Authorization") Token: String
    ): Response<JsonObject>


    @GET("shieldcryptUserManagementMobile/user/searchByUserId")//shieldcryptUserManagementMobile/user/searchByUserId?userid=2

    @Headers("Content-type:application/json")
    suspend fun getDataFromServerWithToken2(
        @Header("Authorization") Token: String, @Query("userid") userid: Int
    ): Response<JsonObject>


    @GET()
    @Headers("Content-type:application/json")
    suspend fun getDataFromServerWithoutAuth(
        @Url url: String
    ): Response<JsonObject>

    @POST()
    @Headers("Content-type:application/json")
    suspend fun uploadProfilePhotoToServerWithToken(
        @Url url: String,
        //  @Part uploadImage: MultipartBody.Part,
        @Body body: RequestBody, @Header("Authorization") Token: String
    ): Response<JsonObject>

    // added by prashant 30 sep 2022
    @POST("shieldcryptUserManagementMobile/user/sendOtpUser")
    @Headers("Content-type:application/json")
    suspend fun getPhoneNoVerification(
        @Body jsonObject: VerificationOtpModel
    ): Response<JsonObject>

    // added by prashant 10 oct 2022
    @POST("shieldcryptUserManagementMobile/user/verifyOtp")
    @Headers("Content-type:application/json")
    suspend fun getVerifyOtp(
        @Body jsonObject: VerifyOtpModel
    ): Response<JsonObject>


    // Api to send message to gsm number
    @POST("sms/send")
    fun getSendMessageToGsmNumberApi(
        @Header("Authorization") authHeader: String?, @Body body: JsonObject?
    ): Call<SendMessageResponseBean?>?

    @POST("shieldcryptGroupManagement/groupmanagement/searchByUserName")
    @Headers("Content-type:application/json")
    suspend fun getDataFromAutoQueryServerWithoutAuth(
        @Query("username") name: String
    ): Response<JsonObject>


    @GET("shieldcryptUserManagementMobile/user/profilestatus/getAllIndividualStatus")
    @Headers("Content-type:application/json")
    suspend fun getDataFromAutoQueryServerWithAuth(
        @Header("Authorization") authHeader: String?, @Query("userid") name: Int
    ): Response<JsonObject>

    @POST()
    @Headers("Content-type:application/json")
    suspend fun featchDataFromServerWithAuthArray(
        @Url url: String, @Body jsonObject: JsonArray, @Header("Authorization") Token: String
    ): Response<JsonObject>


    @HTTP(
        method = "DELETE",
        path = "shieldcryptUserManagementMobile/user/profilestatus/deleteProfilePhoto",
        hasBody = true
    )
    @Headers("Content-type:application/json")
    suspend fun deleteDataFromServerWithToken(
        @Body jsonObject: JsonObject, @Header("Authorization") Token: String
    ): Response<JsonObject>


//    @DELETE
    @HTTP(method = "DELETE", path = "", hasBody = true)
    @Headers("Content-type:application/json")
    suspend fun deleteDataFromserver(//@Path("login") String postfix,
    @Url url: String ,
    @Body jsonObject: JsonObject, @Header("Authorization") Token: String
    ): Response<JsonObject>
//    ): Response<ResponseBody>

    @HTTP(method = "DELETE", hasBody = true)
    @Headers("Content-type:application/json")
    suspend fun deleteMeetingWithoutAuth(
         @Url url: String,
        @Body jsonObject: JsonObject
    ): Response<JsonObject>
}
