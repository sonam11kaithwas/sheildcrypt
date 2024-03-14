package com.advantal.shieldcrypt.network_pkg

import com.advantal.shieldcrypt.auth_pkg.model.LoginReqModel
import com.advantal.shieldcrypt.auth_pkg.model.VerificationOtpModel
import com.advantal.shieldcrypt.auth_pkg.model.VerifyOtpModel
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Header
import javax.inject.Inject

/**
 * Created by Sonam on 27-07-2022 16:44.
 */

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getLoginDataFromServer(body: LoginReqModel): Response<JsonObject> =
        apiService.getLoginDataFromServer(jsonObject = body)

    override suspend fun getPhoneNoVerification(body: VerificationOtpModel): Response<JsonObject> =
        apiService.getPhoneNoVerification(jsonObject = body)

    override suspend fun getVerifyOtp(body: VerifyOtpModel): Response<JsonObject> =
        apiService.getVerifyOtp(jsonObject = body)

    override suspend fun deleteDataFromserver(
        uri: String, body: JsonObject, token: Header
    ) = apiService.deleteDataFromserver(
        url = uri, jsonObject = body, Token = String()
    )


    override suspend fun featchDataFromServerWithoutAuth(
        uri: String, body: JsonObject
    ): Response<JsonObject> = apiService.featchDataFromServerWithoutAuth(
        url = uri, jsonObject = body
    )

    override suspend fun featchDataFromServerWithAuth(
        uri: String, body: JsonObject, Token: Header
    ): Response<JsonObject> = apiService.featchDataFromServerWithAuth(
        url = uri, jsonObject = body, Token = String()
    )

    override suspend fun getDataFromServerWithToken(uri: String): Response<JsonObject> =
        apiService.getDataFromServerWithToken(
            url = uri, Token = String()
        )

    override suspend fun getDataFromServerWithoutToken(uri: String): Response<JsonObject> =
        apiService.getDataFromServerWithoutAuth(
            url = uri
        )

    override suspend fun getDataFromServerWithToken2(
        token: Header, body: Int
//        , uri: String
    ): Response<JsonObject> {
        return apiService.getDataFromServerWithToken2(
            Token = String(), userid = body
        )
    }

}