package com.advantal.shieldcrypt.utils_pkg

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.advantal.shieldcrypt.broadcast.PowerButtonReceiver
import com.advantal.shieldcrypt.globalexceptionhandler.LoggingExceptionHandler
import com.advantal.shieldcrypt.network.NetworkSchedulerService
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.service.BackgroundConnectionService
import com.advantal.shieldcrypt.xmpp_pkg.RoosterConnectionService
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


/**
 * Created by Sonam on 12-07-2022 12:56.
 */
@HiltAndroidApp
class MyApp : Application(), LifecycleObserver, ActivityLifecycleCallbacks {

    @Inject
    lateinit var networkHelper: NetworkHelper
    var powerButtonReceiver: PowerButtonReceiver? = null
    override fun onCreate() {
        instance = this
        LoggingExceptionHandler(this);
        EmojiManager.install(GoogleEmojiProvider())


        super.onCreate()

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_USER_PRESENT)
        powerButtonReceiver = PowerButtonReceiver()
        instance.getApplicationContext()
            .registerReceiver(powerButtonReceiver, filter)
    }

    fun showToastMsg(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showToastMsgLong(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    companion object {
        private lateinit var instance: MyApp

        fun getAppInstance(): MyApp {
            return instance
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun appInResumeState() {
//        mySharedPreferences.setValue(AppConstants.isAppForeground, AppConstants.trueValue)


        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
            NetworkSchedulerService::class.java
        )
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(oneTimeWorkRequest)
        if (MyApp.Companion.getAppInstance().getNetWokAvailabe()) {
//            if (Utils.checkInternetConn(com.ithub.utility.MyApplication.getAppContext())) {
//            AppUtills.isXmppWorkScheduled(applicationContext)
            println("NetworkSchedulerService connected check inside myapp 149")
//            }
            if (XMPPConnectionListener.mConnection != null && RoosterConnectionService.getState()!!
                    .equals(XMPPConnectionListener.ConnectionState.CONNECTED)
            ) {
                XMPPConnectionListener.sendPresenceOnline()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun appInPauseState() {
        /*mySharedPreferences.setValue(AppConstants.isAppForeground, AppConstants.falseValue);
//        Toast.makeText(this, "In Background", Toast.LENGTH_LONG).show();
        if (XmppConnect.mConnection != null && RoosterConnectionService.getState().equals(XmppConnect.ConnectionState.CONNECTED)) {
            XmppConnect.sendPresenceOffline();
        }*/

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun appInDestroyState() {
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(
            NetworkSchedulerService::class.java
        )
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(oneTimeWorkRequest)
        if (MyApp.getAppInstance().getNetWokAvailabe()) {
//            if (Utils.checkInternetConn(com.ithub.utility.MyApplication.getAppContext())) {
//            AppUtills.isXmppWorkScheduled(applicationContext)
            println("NetworkSchedulerService connected check inside myapp 149")
//            }
            if (XMPPConnectionListener.mConnection != null && RoosterConnectionService.getState()!!
                    .equals(XMPPConnectionListener.ConnectionState.CONNECTED)
            ) {
                XMPPConnectionListener.sendPresenceOnline()
            }
        }

        val serviceIntent = Intent(this, BackgroundConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(serviceIntent)
        }else{
            this.startService(serviceIntent)
        }
//        startService(Intent(this,))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun appInStopState() {
//        My.setValue(AppConstants.isAppForeground, AppConstants.falseValue)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
//        if (XMPPConnectionListener.mConnection != null && RoosterConnectionService.getState()
//                ?.equals(XMPPConnectionListener.ConnectionState.CONNECTED)!!
//        )
        XMPPConnectionListener.sendPresenceOffline()
    }

    fun getNetWokAvailabe(): Boolean {
        return (networkHelper.isNetworkConnected())
    }
}