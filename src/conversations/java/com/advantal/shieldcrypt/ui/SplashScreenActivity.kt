package com.advantal.shieldcrypt.ui


import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.advantal.shieldcrypt.R
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.*


class SplashScreenActivity : AppCompatActivity() {
//public
    var isAutoLoginStatus = false

    // Check permission for the call phone, read contact and record audio
    private fun checkPermission(): Boolean {
        val result = (ContextCompat.checkSelfPermission(applicationContext, permission.CALL_PHONE)
                + ContextCompat.checkSelfPermission(applicationContext, permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(applicationContext, permission.RECORD_AUDIO))
        return result == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
//        isAutoLoginStatus = MySharedPreferences.getSharedprefInstance().getAutoLoginStatus();


Log.e("","")


    }


    override fun onResume() {
//        startActivity(Intent(this, ConversationsActivity::class.java))
//        finish()
        Handler(Looper.getMainLooper()).postDelayed({

//            if (isAutoLoginStatus){
//                val intent = Intent(this, AuthenticationActivity::class.java)
//                startActivity(intent)
//                this.finish()
//            } else{
//                val intent = Intent(this, AuthenticationActivity::class.java)
//                startActivity(intent)
//                this.finish()
//            }
           /* val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            this.finish()*/
        }, 2000)
        super.onResume()
    }
}
