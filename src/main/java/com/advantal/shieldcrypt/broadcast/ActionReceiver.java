package com.advantal.shieldcrypt.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;


import com.advantal.shieldcrypt.sip.CallActivity;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;

import net.gotev.sipservice.Logger;
import net.gotev.sipservice.SharedPreferencesHelper;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;
import net.gotev.sipservice.SipServiceConstants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionReceiver
        extends BroadcastReceiver implements SipServiceConstants {

    String mSIPACCOUNTID;
    SharedPrefrence mySharedPreferences;
    Context context;
    private SipAccountData mSipAccount;
    private static final String LOG_TAG = ActionReceiver.class.getSimpleName();
    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        mySharedPreferences = SharedPrefrence.getInstance(context);
        mSIPACCOUNTID = mySharedPreferences.getValue(SharedPrefrence.SIPACCOUNTID);
        Logger.debug(LOG_TAG, " gettingIntentAction: " + intent);
        String action = intent.getStringExtra("action");
        if (action.equals("hangup")) {
            int callId = intent.getIntExtra("callid", 0);
            SipServiceCommand.hangUpCall(context, mSIPACCOUNTID, callId);
            try {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
                nMgr.cancel(callId);
            } catch (Exception e) {
            }
        } else if (action.equals("Accept")) {
            final int newCallId = intent.getIntExtra("callid", 0);
            int oldCallId = intent.getIntExtra("oldcallid", 0);
            final String number = intent.getStringExtra("number");
            SipServiceCommand.hangUpCall(context, mSIPACCOUNTID, oldCallId);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadConfiguredAccounts();
                    SipServiceCommand.acceptIncomingCall(context, mSipAccount.getIdUri(), newCallId);
                    try {
                        String ns = Context.NOTIFICATION_SERVICE;
                        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
                        nMgr.cancel(newCallId);
                    } catch (Exception e) {
                    }
                    Intent resultIntent = null;
                    resultIntent = new Intent(context, CallActivity.class);

                    resultIntent.putExtra(PARAM_ACCOUNT_ID, mSIPACCOUNTID);
                    resultIntent.putExtra(PARAM_CALL_ID, newCallId);
                    resultIntent.putExtra(PARAM_NUMBER, number);
                    resultIntent.putExtra(PARAM_CALL_STATUS, "activie");
                    resultIntent.putExtra(PARAM_CALL_TYPE, "IN");
                    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(resultIntent);
                }
            }, 2000);
        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
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