package com.advantal.shieldcrypt.splash_scr_pkg


import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*


class SplashScreenActivity : XmppActivity() {
    private val PERMISSION_REQUEST_CODE = 1000
    var isAutoLoginStatus = false
    var observer: Observer<Objects> = object : Observer<Objects> {


        override fun onError(e: Throwable) {
            println("Error received: " + e.message)
        }

        override fun onNext(integer: Objects) {
            Toast.makeText(this@SplashScreenActivity, "Its a toast!", Toast.LENGTH_SHORT).show()
        }


        override fun onComplete() {
            println("Error received: " + "e.message")
        }

        override fun onSubscribe(d: Disposable) {
        }
    }

    // Check permission for the call phone, read contact and record audio
    /*  private fun checkPermission(): Boolean {
          val result = (ContextCompat.checkSelfPermission(applicationContext, permission.CALL_PHONE)
                  + ContextCompat.checkSelfPermission(applicationContext, permission.READ_CONTACTS)
                  + ContextCompat.checkSelfPermission(applicationContext, permission.RECORD_AUDIO))
          return result == PackageManager.PERMISSION_GRANTED
      }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        isAutoLoginStatus = MySharedPreferences.getSharedprefInstance().getAutoLoginStatus()

        if (!isAutoLoginStatus && xmppConnectionService != null) {
            preferences.edit().putBoolean("hide_offline", false).apply()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            checkAndroidVersion()
        }, 1000)
    }

    // Check android version for the permission
    private fun checkAndroidVersion() {
        Utils.hideKeyBoard(this@SplashScreenActivity)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) switchActivity() else requestPermission()
        } else {
            switchActivity()
        }
    }

    // Check permission for the call phone, read contact and record audio
    private fun checkPermission(): Boolean {
        val result = (ContextCompat.checkSelfPermission(
            applicationContext, permission.CALL_PHONE
        ) + ContextCompat.checkSelfPermission(
            applicationContext, permission.READ_CONTACTS
        ) + ContextCompat.checkSelfPermission(
            applicationContext, permission.RECORD_AUDIO
        ) + ContextCompat.checkSelfPermission(
            applicationContext, permission.WRITE_EXTERNAL_STORAGE
        ) + ContextCompat.checkSelfPermission(applicationContext, permission.CAMERA))
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                permission.READ_PHONE_STATE,
                permission.READ_CONTACTS,
                permission.RECORD_AUDIO,
                permission.CALL_PHONE,
                permission.CAMERA,
                permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_REQUEST_CODE
        )
    }

    // Permission result callback
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val readCallState = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readContactState = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val recordAudioState = grantResults[2] == PackageManager.PERMISSION_GRANTED
                val cameraState = grantResults[4] == PackageManager.PERMISSION_GRANTED
                if (readCallState && readContactState && recordAudioState && cameraState) switchActivity() else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permission.CALL_PHONE) || shouldShowRequestPermissionRationale(
                                permission.READ_PHONE_STATE
                            ) || shouldShowRequestPermissionRationale(permission.READ_CONTACTS) || shouldShowRequestPermissionRationale(
                                permission.RECORD_AUDIO
                            ) || shouldShowRequestPermissionRationale(
                                permission.CAMERA
                            ) || shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                    arrayOf(
                                        permission.CALL_PHONE,
                                        permission.READ_CONTACTS,
                                        permission.RECORD_AUDIO,
                                        permission.READ_PHONE_STATE,
                                        permission.CAMERA,
                                        permission.WRITE_EXTERNAL_STORAGE
                                    ), PERMISSION_REQUEST_CODE
                                )
                            }
                            return
                        } else {
                            if (checkPermission()){
                                switchActivity()
                            } else {
                                Utils.permissionAlertDialog(
                                    this@SplashScreenActivity,
                                    "Permission Alert",
                                    "For continue you need to enable permissions from the Settings-> Permissions-> Enable phone permission."
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppConstants.RUNTIME_PERMISSION_REQUEST_CODE) {
            checkAndroidVersion()
        }
    }

    private fun switchActivity() {
        if (isAutoLoginStatus) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            this.finish()
        } else {
            val intent = Intent(this, AuthenticationActivity::class.java)
//            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    companion object {
        var oncall: Boolean = false
    }

    override fun refreshUiReal() {

    }

    override fun onBackendConnected() {

//        if (!isAutoLoginStatus) {
//            if (xmppConnectionService!=null) {
//                xmppConnectionService.deletedatabaseBackend()
//                preferences.edit().putBoolean("hide_offline", false).apply()
//            }
//
//        }
    }
}