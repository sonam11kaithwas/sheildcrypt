package com.advantal.shieldcrypt.network_pkg

import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * Created by Sonam on 27-07-2022 16:45.
 */

class MainRepository @Inject constructor(private val apiHelper: ApiService) {

    suspend fun getLoginDataFromServer(body: LoginReqModel) = apiHelper.getLoginDataFromServer(body)
    suspend fun getPhoneNoVerification(body: VerificationOtpModel) =
        apiHelper.getPhoneNoVerification(body)

    // added by prashant 10 oct 2022 purpose verify otp
    suspend fun getVerifyOtp(body: VerifyOtpModel) = apiHelper.getVerifyOtp(body)

    suspend fun featchDataFromServerWithoutAuth(uri: String, body: JsonObject) =
        apiHelper.featchDataFromServerWithoutAuth(uri, body)

    suspend fun featchDataFromServerWithAuth(
        uri: String, body: JsonObject, token: String
    ) = apiHelper.featchDataFromServerWithAuth(
        uri, body, token
    )

    suspend fun deleteDataFromserver(
        uri: String, body: JsonObject, token: String
    ) = apiHelper.deleteDataFromserver(
        uri,
        body, token
    )

    suspend fun getDataFromServerWithToken2(
        token: String, userid: Int
//       ,uri: String
    ) = apiHelper.getDataFromServerWithToken2(
        token, userid
//            ,uri
    )

    suspend fun getDataFromServerWithToken(
        uri: String, token: String
    ) = apiHelper.getDataFromServerWithToken(
        uri, token
    )

    suspend fun uploadProfilePhotoToServerWithToken(
        uri: String,
        //  uploadImage: MultipartBody.Part,
        body: RequestBody, token: String
    ) = apiHelper.uploadProfilePhotoToServerWithToken(
        uri,
        //  uploadImage,
        body, token
    )

    suspend fun getDataFromServerWithoutAuth(
        uri: String
    ) = apiHelper.getDataFromServerWithoutAuth(
        uri
    )

    suspend fun getDataFromAutoQueryServerWithoutAuth(
        query: String,
    ) = apiHelper.getDataFromAutoQueryServerWithoutAuth(
        query
    )

    suspend fun getDataFromAutoQueryServerWithAuth(
        token: String,
        query: Int,
    ) = apiHelper.getDataFromAutoQueryServerWithAuth(
        token, query
    )

    suspend fun featchDataFromServerWithAuthArray(
        uri: String, body: JsonArray, token: String
    ) = apiHelper.featchDataFromServerWithAuthArray(
        uri, body, token
    )

    suspend fun deleteDataFromServerWithToken(
        /* uri: String,*/
        body: JsonObject, token: String
    ) = apiHelper.deleteDataFromServerWithToken(
        /* uri,*/
        body, token
    )

    suspend fun deleteMeetingWithoutAuth(
         uri: String,
        body: JsonObject) =
        apiHelper.deleteMeetingWithoutAuth(
             uri,
            body)

}
