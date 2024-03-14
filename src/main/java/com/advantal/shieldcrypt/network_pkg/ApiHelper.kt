package com.advantal.shieldcrypt.network_pkg

import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Header

/**
 * Created by Sonam on 27-07-2022 16:42.
 */
interface ApiHelper {

    suspend fun getLoginDataFromServer(body: LoginReqModel): Response<JsonObject>
    suspend fun getPhoneNoVerification(body: VerificationOtpModel): Response<JsonObject>
    suspend fun getVerifyOtp(body: VerifyOtpModel): Response<JsonObject>
    suspend fun deleteDataFromserver(
        uri: String,
        body: JsonObject,
        token: Header
    ): Response<JsonObject>

    suspend fun featchDataFromServerWithoutAuth(uri: String, body: JsonObject): Response<JsonObject>

    suspend fun featchDataFromServerWithAuth(
        uri: String,
        body: JsonObject,
        token: Header
    ): Response<JsonObject>

    suspend fun getDataFromServerWithToken(uri: String): Response<JsonObject>
    suspend fun getDataFromServerWithoutToken(uri: String): Response<JsonObject>
    suspend fun getDataFromServerWithToken2(token: Header, userid: Int): Response<JsonObject>
}