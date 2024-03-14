package com.advantal.shieldcrypt.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.google.gson.Gson;
import com.advantal.shieldcrypt.sip.CallActivity;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.VideoActivity;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;

import net.gotev.sipservice.Logger;
import net.gotev.sipservice.SharedPreferencesHelper;
import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;
import net.gotev.sipservice.SipServiceConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HeadsUpNotificationActionReceiver extends BroadcastReceiver implements SipServiceConstants {

    String mSIPACCOUNTID;
    SharedPrefrence mySharedPreferences;
    Context context;
    private SipAccountData mSipAccount;
    private static final String LOG_TAG = HeadsUpNotificationActionReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        mySharedPreferences = SharedPrefrence.getInstance(context);
        mSIPACCOUNTID = mySharedPreferences.getValue(SharedPrefrence.SIPACCOUNTID);
        try {
            Logger.debug(LOG_TAG, " gettingIntentAction: " + intent.getAction());
        JSONObject jsonObject = new Gson().fromJson(intent.getAction(), JSONObject.class);
        String action = jsonObject.getString("action");
        if (action.equals("hangup")) {
            int callId = jsonObject.getInt("callid");

            try {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
                nMgr.cancel(mySharedPreferences.getIntValue(AppConstants.headsUpNotificationId));
            } catch (Exception e) {
            }

            SipServiceCommand.hangUpCall(context, mSIPACCOUNTID, callId);
           // SipServiceCommand.declineIncomingCall(context, mSIPACCOUNTID, callId);
           // SipServiceCommand.hangUpActiveCalls(context, mSIPACCOUNTID);
        } else if (action.equals("Accept")) {
            final int newCallId = jsonObject.getInt("callid");
            final String number = jsonObject.getString("number");
            boolean isVideo = jsonObject.getBoolean("isVideo");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    loadConfiguredAccounts();
                    try {
                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
                        nMgr.cancel(mySharedPreferences.getIntValue(AppConstants.headsUpNotificationId));
                    } catch (Exception e) {
                    }
                    SipServiceCommand.acceptIncomingCall(context, mSipAccount.getIdUri(), newCallId);

                    Intent resultIntent;
                    if (isVideo){
                        resultIntent = new Intent(context, VideoActivity.class);
                    } else {
                        resultIntent = new Intent(context, CallActivity.class);
                    }
                    resultIntent.putExtra(PARAM_ACCOUNT_ID, mSIPACCOUNTID);
                    resultIntent.putExtra(PARAM_CALL_ID, newCallId);
                    resultIntent.putExtra(PARAM_NUMBER, number);
                    resultIntent.putExtra(PARAM_CALL_STATUS, "activie");
                    resultIntent.putExtra(PARAM_CALL_TYPE, "IN");
//                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
//            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(resultIntent);
                }
            }, 2000);
        }
        } catch (JSONException e){
            e.printStackTrace();
        }
        //This is used to close the notification tray
       /* Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);*/

        //by arvind
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }
    }

    private void loadConfiguredAccounts() {

        String TAG = SipService.class.getSimpleName();
        String PREFS_NAME = TAG + "prefs";

//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

//        String accounts = prefs.getString("accounts", "");
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);


        List<SipAccountData> mConfiguredAccounts = new ArrayList<>();
        mConfiguredAccounts = sharedPreferencesHelper.getConfiguredAccounts();

        /*if (accounts.isEmpty()) {
            mConfiguredAccounts = new ArrayList<>();
        } else {
            Type listType = new TypeToken<ArrayList<SipAccountData>>() {
            }.getType();
            mConfiguredAccounts = new Gson().fromJson(accounts, listType);
        }*/

        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();

        while (iterator.hasNext()) {
            mSipAccount = iterator.next();
        }
    }
}