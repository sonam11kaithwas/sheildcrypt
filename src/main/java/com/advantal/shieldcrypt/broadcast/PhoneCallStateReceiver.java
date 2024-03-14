package com.advantal.shieldcrypt.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

//import com.ithub.utility.MySharedPreferences;

import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;

import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipServiceConstants;

public class PhoneCallStateReceiver extends BroadcastReceiver implements SipServiceConstants {

    String mSIPACCOUNTID;
    MySharedPreferences mySharedPreferences;
    Context context;
    private SipAccountData mSipAccount;

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneCallStateListner customPhoneListener = new PhoneCallStateListner(context);
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
//        holdCallWhileGsmCall();
    }
}