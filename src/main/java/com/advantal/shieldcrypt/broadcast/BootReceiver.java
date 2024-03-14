package com.advantal.shieldcrypt.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.advantal.shieldcrypt.service.BackgroundConnectionService;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;

//Created by SV 16JUN22
public class BootReceiver
        extends BroadcastReceiver {
    SharedPrefrence mySharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootReceiver", "BootReceiver received!");
        mySharedPreferences = SharedPrefrence.getInstance(context);
        //if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        // Do my stuff

        startService(context);
        //  startServiceToConnectXmpp(context);
        appInDestroyState(context);

        // }
    }

    public void startServiceToConnectXmpp(Context context) {
        //Start the service
//        Intent i1 = new Intent(this, RoosterConnectionService.class);
//        startService(i1);

//        Utils.scheduleJob(this);
      /*  if (Utils.checkInternetConn(context))
            AppUtills.isXmppWorkScheduled(context);*/

    }


    ///Add by SV
    public void startService(Context context) {
        /// String input = editTextInput.getText().toString();
        Intent serviceIntent = new Intent(context, BackgroundConnectionService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

    public void stopService(Context context) {
        Intent serviceIntent = new Intent(context, BackgroundConnectionService.class);
        (context).stopService(serviceIntent);
    }

    public void appInDestroyState(Context context) {
        mySharedPreferences.setValue(AppConstants.isAppForeground, AppConstants.falseValue);
       /* boolean isAutologin = mySharedPreferences.getBooleanValue(AppConstants.autoLogin);
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NetworkSchedulerService.class)
                .build();
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest);
        if (isAutologin) {
            if (Utils.checkInternetConn(context)) {
                Utils.isXmppWorkScheduled(context);
                System.out.println("NetworkSchedulerService connected check inside boot brodcast");
            }
            if (XmppConnect.mConnection != null && RoosterConnectionService.getState().equals(XmppConnect.ConnectionState.CONNECTED)) {
                XmppConnect.sendPresenceOnline();
            }
        }*/
    }

}