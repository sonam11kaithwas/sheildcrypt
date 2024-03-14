package com.advantal.shieldcrypt.broadcast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.advantal.shieldcrypt.sip.SharedPrefrence;

import net.gotev.sipservice.SharedPreferencesHelper;
import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipCall;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.pjsip_inv_state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PhoneCallStateListner
        extends PhoneStateListener {
    private Context context;
    public static int stateCheck;
    public SharedPrefrence share;
    private int prev_state;
    public static boolean valueGsm = false;

    public PhoneCallStateListner(Context context) {
        this.context = context;
        share = SharedPrefrence.getInstance(context);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        stateCheck = state;
        Log.e("PhoneCallStateListnerPhoneNumber>>>>>>>>>>>>>>", phoneNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                System.out.println("call telephone ringing");
                Log.d("oncallreceiver", "PhoneCallStateListener ringing");
                Log.e("CALL_STATE_RINGING", "CALL_STATE_RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("call telephone offhook");
                Log.d("oncallreceiver", "PhoneCallStateListener offhoook");
                Log.e("CALL_STATE_OFFHOOK", "CALL_STATE_OFFHOOK");
                holdCallWhileGsmCall();
                break;
            case PhoneStateListener.LISTEN_CALL_STATE:
                Log.d("oncallreceiver", "PhoneCallStateListener listen");
                System.out.println("call telephone listne");
                Log.e("LISTEN_CALL_STATE", "LISTEN_CALL_STATE");
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d("oncallreceiver", "PhoneCallStateListener idle");
                Log.e("CALL_STATE_IDLE============", "CALL_STATE_IDLE===================");
                if (share.getValue("Gsm Call").equals("true")) {
                    System.out.println("yes hardik call hold print====");
                    unHoldCallWhileGsmCall();
                }

//                if((stateCheck == TelephonyManager.CALL_STATE_OFFHOOK)){
//                    Log.e("Answered Call which is ended============","Answered Call which is ended===================");
//                    //Answered Call which is ended
//                }
//                if((stateCheck == TelephonyManager.CALL_STATE_RINGING)){
//                    Log.e("Rejected or Missed call============","Rejected or Missed call===================");
//                    //Rejected or Missed call
//                }
                break;

            default:
                break;
        }
        super.onCallStateChanged(state, phoneNumber);
    }

    private SipAccountData mSipAccount;

    private void loadConfiguredAccounts() {

        String TAG = SipService.class.getSimpleName();
        String PREFS_NAME = TAG + "prefs";
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        List<SipAccountData> mConfiguredAccounts = new ArrayList<>();
        mConfiguredAccounts = sharedPreferencesHelper.getConfiguredAccounts();
        Iterator<SipAccountData> iterator = mConfiguredAccounts.iterator();
        while (iterator.hasNext()) {
            mSipAccount = iterator.next();
        }
        // Log.e("LOAD_CONFIGURE_ACCOUNT_________________AccountID", mSipAccount.getIdUri());
    }

    private void holdCallWhileGsmCall() {
        share.setValue("Gsm Call", "true");


        String mSIPACCOUNTID = share.getValue(SharedPrefrence.SIPACCOUNTID);
        Log.e("holdCalylWhileGsmCall======mSIPACCOUNTID", mSIPACCOUNTID);
        SipAccount account = SipService.mActiveSipAccounts.get(mSIPACCOUNTID);
        Log.e("holdCallWhileGsmCall======account", String.valueOf(account));
        loadConfiguredAccounts();
        if (account == null)
            return;
        Set<Integer> activeCallIDs = account.getCallIDs();
        Log.e("holdCallWhileGsmCall======activeCallIDs", String.valueOf(activeCallIDs));
        if (activeCallIDs == null || activeCallIDs.isEmpty())
            return;
        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = account.getCall(callID);
                Log.e("holdCallWhileGsmCall======sipCall", String.valueOf(sipCall));

                if (sipCall == null) {
                    return;
                } else
                    {

                    // SipServiceCommand.setCallHold(context, mSIPACCOUNTID, sipCall.getId(), true);
                    // SipServiceCommand.toggleCallHold(context, mSIPACCOUNTID, sipCall.getId());
                    SipServiceCommand.setCallMute(context, mSIPACCOUNTID, sipCall.getId(), true);

//
//
//                    int callState = sipCall.getCurrentState();
//                    int mCallStateCode = callState;
//
//                    if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
//                        Log.e("holdCallWhileGsmCall======PJSIP_INV_STATE_CONFIRMED", "PJSIP_INV_STATE_CONFIRMED");
//                        SipServiceCommand.setCallHold(context, mSIPACCOUNTID, sipCall.getId(), true);
//                       // isHold = true;
//                        //  iv_hold.setBackgroundResource(R.drawable.hold_selected);
//                        return;
//                    }
                }
            } catch (Exception e) {
                //
            }
        }
    }

    private void unHoldCallWhileGsmCall() {
        share.setValue("Gsm Call", "false");
        String mSIPACCOUNTID = share.getValue(SharedPrefrence.SIPACCOUNTID);
        Log.e("holdCallWhileGsmCall======mSIPACCOUNTID", mSIPACCOUNTID);
        SipAccount account = SipService.mActiveSipAccounts.get(mSIPACCOUNTID);
        Log.e("holdCallWhileGsmCall======account", String.valueOf(account));
        loadConfiguredAccounts();
        if (account == null)
            return;
        Set<Integer> activeCallIDs = account.getCallIDs();
        Log.e("holdCallWhileGsmCall======activeCallIDs", String.valueOf(activeCallIDs));
        if (activeCallIDs == null || activeCallIDs.isEmpty())
            return;
        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = account.getCall(callID);
                Log.e("holdCallWhileGsmCall======sipCall", String.valueOf(sipCall));

                if (sipCall == null) {
                    return;
                } else {

                    // SipServiceCommand.setCallHold(context, mSIPACCOUNTID, sipCall.getId(), false);

                    //SipServiceCommand.toggleCallHold(context, mSIPACCOUNTID, sipCall.getId());
                    SipServiceCommand.setCallMute(context, mSIPACCOUNTID, sipCall.getId(), false);

//                    int callState = sipCall.getCurrentState();
//                    int mCallStateCode = callState;
//
//                    if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
//                        Log.e("holdCallWhileGsmCall======PJSIP_INV_STATE_CONFIRMED", "PJSIP_INV_STATE_CONFIRMED");
//                        SipServiceCommand.toggleCallHold(context, mSIPACCOUNTID, sipCall.getId());
//                        // isHold = true;
//                        //  iv_hold.setBackgroundResource(R.drawable.hold_selected);
//                        return;
//                    }
                }
            } catch (Exception e) {

            }
        }
    }

    private void onholdAct() {
        String mSIPACCOUNTID = share.getValue(SharedPrefrence.SIPACCOUNTID);
        SipAccount account = SipService.mActiveSipAccounts.get(mSIPACCOUNTID);
        if (account == null)
            return;
        Set<Integer> activeCallIDs = account.getCallIDs();
        if (activeCallIDs == null || activeCallIDs.isEmpty())
            return;
        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = account.getCall(callID);

                if (sipCall == null) {
                    return;
                } else {
                    int callState = sipCall.getCurrentState();
                    int mCallStateCode = callState;
                    if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                        SipServiceCommand.setCallHold(context, mSIPACCOUNTID, sipCall.getId(), true);
                        System.out.println("dsfjsklfsdyyyyyy");
                        Intent intent = new Intent("com.call.stateupdate");
                        intent.putExtra("STATE", "HOLD");
                        context.sendBroadcast(intent);
                        return;
                    } else if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                        Log.d("hangup", "SipServiceCommand.hangUpCall called at broadcast - 215");

                        SipServiceCommand.hangUpCall(context, mSIPACCOUNTID, sipCall.getId());
                    }
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}
