package com.advantal.shieldcrypt.network_pkg

import android.app.Activity
import com.advantal.shieldcrypt.BuildConfig
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.SettingFragment
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import java.io.File
import java.io.IOException

/* "shieldcryptUserManagementMobile/user/updateUserProfilePhoto"*/
class ProfilePictureUpload {
    val MEDIA_TYPE_PNG = "image/png".toMediaTypeOrNull()
    val client = OkHttpClient()
    var v = MySharedPreferences.getSharedprefInstance().getLoginData().token
    lateinit var imageUploadSuccesFully: ImageUploadSuccesFully
    var v2 = "Bearer "
    var activity: Activity? = null
    fun ProfilePhotoUpload(
        activity: Activity?,
        file: File?,
        username: String?,
        strurl: String,
        id: String?,
        imageUploadSuccesFully: ImageUploadSuccesFully
    ) {
        this.activity = activity
        this.imageUploadSuccesFully = imageUploadSuccesFully
        run(file!!, username!!, strurl, id!!)
    }fun ProfilePhotoUpload(
        activity: Activity?,
        file: File?,
        username: String?,
        strurl: String,
        id: String?
    ) {
        this.activity = activity
        run(file!!, username!!, strurl, id!!)
    }

    fun run(file: File, username: String, strurl: String, id: String) {
        var key = "uploadImage"
        if (id != null && !id.isEmpty()) {
            key = "file"
        }
        val requestBody: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("username", username)
            .addFormDataPart("id", id.toString())//.toRequestBody(okhttp3.MultipartBody.FORM)
            .addFormDataPart(
                key, file.name, file.asRequestBody(MEDIA_TYPE_PNG)
            ).build()
        val request = Request.Builder().header(
                "Authorization", "$v2 $v"
            ).url(BuildConfig.BASE_URL + strurl).post(requestBody).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                AppUtills.closeProgressDialog()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    AppUtills.closeProgressDialog()
                    if (response != null && response.body != null) {
                        activity?.runOnUiThread(Runnable {
                            SettingFragment.MyObject.profileChangedStatus = true
                            MyApp.getAppInstance()
                                .showToastMsg("Profile Photo Uploaded Successfully!")
                            if (imageUploadSuccesFully != null) {
                                imageUploadSuccesFully.uploadProfileSyccesFully()
                            }
                        })

                    }
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

    interface ImageUploadSuccesFully {
        fun uploadProfileSyccesFully()
    }
}