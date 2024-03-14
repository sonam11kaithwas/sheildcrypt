package com.advantal.shieldcrypt.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.advantal.shieldcrypt.sip.SharedPrefrence;

import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipService;

public class PowerButtonReceiver
        extends BroadcastReceiver {
    Context context;
    SharedPrefrence share;
    SipAccount account;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        try {
            if (share == null) {
                share = SharedPrefrence.getInstance(context);
            }
            String mSIPACCOUNTID = share.getValue(SharedPrefrence.SIPACCOUNTID);

            if (account == null) {
                account = SipService.mActiveSipAccounts.get(mSIPACCOUNTID);
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
               // account.getService().stopRingtone();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}