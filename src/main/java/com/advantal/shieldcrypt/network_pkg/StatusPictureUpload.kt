package com.advantal.sheildcrypt.network_pkg

import android.app.Activity
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.TextStatusFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.UploadCallBack

import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * Created by Prashant Lal on 01-11-2022 16:30.
 */
class StatusPictureUpload {
    val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
    val client = OkHttpClient()
    var v = MySharedPreferences.getSharedprefInstance()
        .getLoginData().token
    var v2 = "Bearer "
    var activity: Activity? = null
    var context: TextStatusFragment? = null

    fun ProfilePhotoUpload(
        activity: Activity?,
        username: String?,
        mediatype: Boolean,
        colorcode: Int,
        fontcode: Int,
        penciltext: String?,
        file: File?
    ) {
        this.activity = activity;
        run(username!!, mediatype!!, colorcode!!, fontcode!!, penciltext!!, file!!)
    }

    fun ProfilePhotoUpload(
        context: TextStatusFragment?,
        username: String?,
        mediatype: Boolean,
        colorcode: Int,
        fontcode: Int,
        penciltext: String?,
        file: File?,
        activity: Activity?
    ) {
        this.context = context;
        this.activity = activity;
        run(username!!, mediatype!!, colorcode!!, fontcode!!, penciltext!!, file!!)
    }

    //mediadetails
    fun run(
        username: String,
        mediatype: Boolean,
        colorcode: Int,
        fontcode: Int,
        penciltext: String,
        file: File
    ) {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("mediatype", "false")
            .addFormDataPart("colorcode", "0")
            .addFormDataPart("fontcode", "0")
            .addFormDataPart("penciltext", "hi")
            .addFormDataPart(
                "mediadetails", file.name,
                RequestBody.create(MEDIA_TYPE_PNG, file)
            )
            .build()
        val request = Request.Builder()
            .header(
                "Authorization",
                "$v2 $v"
            )
            .url(BuildConfig.BASE_URL + "shieldcryptUserManagementMobile/user/mediastatus/create")
//        https://shieldcrypt.co.in:8011/shieldcryptUserManagementMobile/user/mediastatus/create?username=sonamkaithwas
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //MyApp.getAppInstance().showToastMsg(e.message.toString())
                AppUtills.closeProgressDialog()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    AppUtills.closeProgressDialog()
                    if (response != null && response.body != null) {
                        activity?.runOnUiThread(Runnable {
                            if (context != null) {
                                val callBack = context as UploadCallBack
                                callBack.imageUploadCallBack("")
                            } else {
                                val callBack = activity as UploadCallBack
                                callBack.imageUploadCallBack("")
                            }
                            MyApp.getAppInstance().showToastMsg("Status Upload Successfully")
                        })
                        /*val body = response.body()
                        Log.e("response", "  response-> " + response.body()?.string())
                        val jsonData = body.toString()
                        val json = JSONObject(jsonData)
                        if (json!=null && json.getString("message")!=null && !json.getString("message").isEmpty()){
                            MyApp.getAppInstance().showToastMsg(json.getString("message"))
                        }*/
                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }
}