package com.advantal.shieldcrypt.sip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.network_pkg.ApiService;
import com.advantal.shieldcrypt.sip.dialog.MessageListDialogFragment;
import com.advantal.shieldcrypt.sip.fragment.ConfCallFragment;
import com.advantal.shieldcrypt.sip.fragment.DtmfFragment;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;
import com.advantal.shieldcrypt.sip.model.SendMessageResponseBean;
import com.advantal.shieldcrypt.sip.rest.ApiClient;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.splash_scr_pkg.SplashScreenActivity;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.CallerInfo;
import net.gotev.sipservice.Logger;
import net.gotev.sipservice.SharedPreferencesHelper;
import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipCall;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;
import net.gotev.sipservice.SipServiceConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import database.my_database_pkg.db_table.MyAppDataBase;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CallActivity extends AppCompatActivity implements View.OnClickListener,
        MessageListDialogFragment.CallBack, SipServiceConstants, ConfCallFragment.CallBack {

    private final String KEY_SIP_ACCOUNT = "sip_account";
    private SipAccountData mSipAccount;
    public SharedPrefrence share;
    private BroadcastReceiver networkReceiver;
    private BroadcastReceiver callstateupdateReceiver;
    Chronometer elapsedTime;
    AudioManager mAudioManager;

    private View parentLayout;

    TextView tv_name, tv_number, tv_city, tv_call_status;
    RelativeLayout rl_calling, rl_incoming;
    ImageView iv_keypad, iv_hold, iv_add_call, iv_speaker, iv_decline, iv_mute, iv_conf_call_info, iv_call_park;
    ImageView iv_block_user, iv_message, iv_incoming_decline, iv_incoming_accept;
    LinearLayout ll_message;

    MyAppDataBase appDataBase;

    String mAccountID, mCallState;
    int mCallID = -1;
    String mNumber, mCallType = "";
    int mCallStateCode;
    String nametodisplay;
    boolean isMute, isHold;
    boolean isresume = false;
    public static boolean confirm = false;
    private PowerManager mPowerManager;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;

    public String callLogSipUserName = "";
    public String callLogFromNumber = "";
    public String callLogToNumber = "";
    public String callLogTimeStamp = "";
    public String callLogDuration = "";

    SharedPrefrence sharedPrefrence;

    MediaPlayer mp;

    private ProgressDialog progress;
    ApiService apiservice;
    MaterialToolbar topAppBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);

        sharedPrefrence = SharedPrefrence.getInstance(this);
     //   appDataBase = AppDataBase.getAppDatabase(CallActivity.this);
        appDataBase = MyAppDataBase.Companion.getUserDataBaseAppinstance(this);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setTurnScreenOn(true);
            setShowWhenLocked(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        View v = getLayoutInflater().inflate(R.layout.call_activity, null);// or any View (incase generated programmatically )
        v.setKeepScreenOn(true);
        setContentView(v);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        topAppBar = (MaterialToolbar)findViewById(R.id.topAppBar);
        callLogTimeStamp = System.currentTimeMillis() + "";
        if (sharedPrefrence != null) {
            callLogFromNumber = sharedPrefrence.getValue(AppConstants.primaryCountryCode) + sharedPrefrence.getValue(AppConstants.primaryMobile);
            callLogSipUserName = sharedPrefrence.getValue(AppConstants.primaryCountryCode) + sharedPrefrence.getValue(AppConstants.primaryMobile);
        }

        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
        } catch (Throwable ignored) {
        }

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, getLocalClassName());

        share = SharedPrefrence.getInstance(CallActivity.this);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT);
        }

//        contact_name_display_name = (TextView) findViewById(R.id.contact_name_display_name);
//        txt_call_status = (TextView) findViewById(R.id.txt_call_status);
        elapsedTime = (Chronometer) findViewById(R.id.elapsedTime);

        tv_name = findViewById(R.id.tv_name);
        tv_number = findViewById(R.id.tv_number);
        tv_city = findViewById(R.id.tv_city);
        tv_call_status = findViewById(R.id.tv_call_status);

        rl_calling = findViewById(R.id.rl_calling);
        rl_incoming = findViewById(R.id.rl_incoming);
        parentLayout = findViewById(android.R.id.content);

        iv_keypad = findViewById(R.id.iv_keypad);
        iv_hold = findViewById(R.id.iv_hold);
        iv_add_call = findViewById(R.id.iv_add_call);
        iv_speaker = findViewById(R.id.iv_speaker);
        iv_decline = findViewById(R.id.iv_decline);
        iv_mute = findViewById(R.id.iv_mute);
        iv_conf_call_info = findViewById(R.id.iv_conf_call_info);
        iv_call_park = findViewById(R.id.iv_call_park);
        iv_block_user = findViewById(R.id.iv_block_user);
        iv_message = findViewById(R.id.iv_message);
        iv_incoming_decline = findViewById(R.id.iv_incoming_decline);
        iv_incoming_accept = findViewById(R.id.iv_incoming_accept);
        ll_message = findViewById(R.id.ll_message);

        iv_keypad.setOnClickListener(this);
        iv_hold.setOnClickListener(this);
        iv_add_call.setOnClickListener(this);
        iv_speaker.setOnClickListener(this);
        iv_decline.setOnClickListener(this);
        iv_mute.setOnClickListener(this);
        iv_conf_call_info.setOnClickListener(this);
        iv_call_park.setOnClickListener(this);
        iv_block_user.setOnClickListener(this);
        iv_message.setOnClickListener(this);
        iv_incoming_decline.setOnClickListener(this);
        iv_incoming_accept.setOnClickListener(this);
        ll_message.setOnClickListener(this);

        mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);

        mp = MediaPlayer.create(CallActivity.this, R.raw.beep);

        try {
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GetdatafromActivity();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private BroadcastEventReceiver sipEvents = new BroadcastEventReceiver()
    {

        @Override
        public void onRegistration(String accountID, int registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
                //Toast.makeText(CallActivity.this, "Registered", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(CallActivity.this, "Unregistered", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCallState(String accountID, int callID, int callStateCode, int callStatusCode,
                                long connectTimestamp, boolean isLocalHold, boolean isLocalMute, boolean isVideo) {

            Logger.debug("<<--Call", "callID: " + callID +
                    ", callStat: " + callStateCode +
                    ", Timestamp: " + connectTimestamp +
                    ", isLocalHold: " + isLocalHold +
                    ", isLocalMute: " + isLocalMute +
                    ", callStatusCode: " + callStatusCode);
            Log.e("sv","advcalstate"+callStateCode);

            if (callStatusCode==pjsip_status_code.PJSIP_SC_RINGING){
                tv_call_status.setText("Ringing...");
            } else {
                tv_call_status.setText("Calling...");
            }

            mCallStateCode = callStateCode;
            isMute = isLocalMute;
            isHold = isLocalHold;
            //mCallID = callID;

            if (isLocalHold) {
                iv_hold.setBackgroundResource(R.drawable.hold_selected);
            } else {
                iv_hold.setBackgroundResource(R.drawable.hold_disable);
            }

            if (isLocalMute) {
                iv_mute.setBackgroundResource(R.drawable.mute_selected);
            } else {
                iv_mute.setBackgroundResource(R.drawable.mute);

            }

            if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
//                if (SipAccount.activeCalls.size() == 0) {
                Log.e("callcutcsacac","123cc");

                iv_conf_call_info.setVisibility(View.GONE);
                loadName();
                callLogDuration = elapsedTime.getText() + "";
                mAudioManager.setSpeakerphoneOn(false);
                SplashScreenActivity.Companion.setOncall(false);
                iv_speaker.setBackgroundResource(R.drawable.speaker);
                if (mCallID == callID) {
                    stopTimer();
                    share.setValue(SharedPrefrence.UPDATE_CALL, "1");
                    if (mCallType.equals("OUT") && connectTimestamp == 0) {
                        delayindisconnect();
                    } else {
//                            cancelNotification(getNotificationIdFromSP());
                        cancelNotification(mCallID);
                        killApplication();
                        finish();
                    }
                }

                Intent in = new Intent("conf_call");
                sendBroadcast(in);
                /*} else {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                    Intent in = new Intent("conf_call");
                    sendBroadcast(in);
                }*/
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                if (SipAccount.activeCalls.size() > 1) {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                } else {
                    iv_conf_call_info.setVisibility(View.GONE);
                    showCalligComponant();
                    confirm = true;
//                accept_call_bar.setVisibility(View.GONE);
                    elapsedTime.setVisibility(View.GONE);
                   // tv_call_status.setText("Calling...");
                    disableHold();
                    disabelMute();
                    disabelAddCall();
                    disabelCallPark();
                }
               // Log.e("mAudioManager", "  checkSpeakerEnable-> " + mAudioManager.isSpeakerphoneOn());
                if (mAudioManager.isSpeakerphoneOn()){
                    if (mSipAccount!=null && mSipAccount.getIdUri()!=null && !mSipAccount.getIdUri().isEmpty()){
                        SipServiceCommand.getSpeaker(CallActivity.this, mSipAccount.getIdUri());
                        /*mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                        mAudioManager.setSpeakerphoneOn(false);*/
                        //Log.e("mAudioManager", "  checkSpeakerEnable-> 1111 " + mAudioManager.isSpeakerphoneOn());
                    }
                }
//                ll_hold.setVisibility(View.GONE);
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                if (!tv_call_status.getText().toString().equalsIgnoreCase("Connected")) {
                    confirm = true;
                    showIncomingComponant();
                }
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {

                if (SipAccount.activeCalls.size() > 1) {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                } else {
                    confirm = true;
                    if (!mCallType.equals("OUT")) {
                        if (!tv_call_status.getText().toString().equalsIgnoreCase("Connected")) {
                            elapsedTime.setVisibility(View.GONE);
                            tv_call_status.setText("Incoming");
                            showIncomingComponant();
                        }
                    } else {
                        elapsedTime.setVisibility(View.GONE);
                        showCalligComponant();
                       // tv_call_status.setText("Calling...");
                        disableHold();
                        disabelAddCall();
                        disabelMute();
                        disabelCallPark();
                    }
                }
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {

            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                SplashScreenActivity.Companion.setOncall(true);
                if (SipAccount.activeCalls.size() > 1) {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                    tv_name.setText("Conference");
                    tv_number.setVisibility(View.GONE);
                } else {
                    iv_conf_call_info.setVisibility(View.GONE);

                    confirm = true;
                    /*if (PhoneCallStateListener.statecheck == 2) {
                        onholdAct();
                    }*/

                    if (!isLocalHold) {
                        enableHold();
                    }
                    if (!isLocalMute) {
                        enabelMute();
                    }
                    enabelCallPark();
                    enabelAddCall();
                    elapsedTime.setVisibility(View.VISIBLE);
                    tv_call_status.setText("Connected");
                    sharedPrefrence.setValue(AppConstants.callStartTimeStamp, System.currentTimeMillis() + "");
                    showCalligComponant();
                    if (!isresume) {
                        // txt_call_status.setVisibility(View.INVISIBLE);

                        if (elapsedTime.getText().equals("00:00")) {
                            resetTimer();
                        }

                    } else {
                        if (elapsedTime.getText().equals("00:00")) {
                            resetTimer(connectTimestamp);
                        }
                    }
                    isresume = false;
                    loadName();
                }

            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_NULL) {
                killApplication();
                finish();
            }
            // pjsua 2 kashish
            /*else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_BUSYHERE) {
                Toast.makeText(CallActivity.this, "User is Busy on another call.", Toast.LENGTH_LONG).show();

            }*/
            else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {

                //callheaderRv.setVisibility(View.VISIBLE);
//                Toast.makeText(CallActivity.this, "User is Busy on another call.", Toast.LENGTH_LONG).show();

            }
            checkSpeaker();

        }


    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSipAccount != null) {
            outState.putParcelable(KEY_SIP_ACCOUNT, mSipAccount);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("pause called");
        try {
            if (wakeLock.isHeld()) {
                wakeLock.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // mSensorManager.unregisterListener(this);
        getCallState();
//        sipEvents.unregister(this);
//        unregisterReceiver(networkReceiver);
//        unregisterReceiver(callstateupdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        sipEvents.unregister(this);
        unregisterReceiver(networkReceiver);
        unregisterReceiver(callstateupdateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  System.out.println("onResume 11 -> " +mSipAccount);
        if (mSipAccount==null){
            loadConfiguredAccounts();
        }

        try {
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        GetdatafromActivity();

        sipEvents.register(this);
        if (mSipAccount != null) {
            SipServiceCommand.getRegistrationStatus(this, mSipAccount.getIdUri());
        }
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
        loadConfiguredAccounts();

        SipServiceCommand.getRegistrationStatus(this, id);
        //System.out.println("onResume 33 -> " +id + "  -> " + mSipAccount.getIdUri());
        SipServiceCommand.getCallStatus(this, mSipAccount.getIdUri(), mCallID);
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                killApplication();
              //  finish();

            }
        };

        IntentFilter intentnetwork = new IntentFilter("com.disconnectcall");
        registerReceiver(networkReceiver, intentnetwork);

        callstateupdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                System.out.println("dsfjsklfsd");
                String mcallstate = intent.getStringExtra("STATE");
                if (intent.hasExtra("phoneCallListner")) {
                    holdCallWhileGsmCall();
                } else {
                    if (mcallstate.equals("HOLD")) {
                        System.out.println("dsfjsklfsdhhhhhhh");
                        isHold = true;
                        iv_hold.setBackgroundResource(R.drawable.hold_selected);
                    } else {
                        if (mCallType.equals("OUT")) {
                            Toast.makeText(getApplicationContext(), "User is busy", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };

        IntentFilter intentcall = new IntentFilter("com.call.stateupdate");
        registerReceiver(callstateupdateReceiver, intentcall);

        checkSpeaker();
        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        if (account == null)
            return;

        Set<Integer> activeCallIDs = account.getCallIDs();
//        addNotificationIdAndCallIdInSharedPreference(mAccountID, mCallID, mNumber);
//        cancelNotification(getNotificationIdFromSP());
        cancelNotification(mCallID);
        setNotificationIdInSP();
        showNotificationForCall(mAccountID, mCallID, mNumber);
//        showNotificationForCall(mAccountID, getNotificationIdFromSP(), mNumber);
    }

    public static void test() {

    }

    private void holdCallWhileGsmCall() {
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
                        SipServiceCommand.setCallHold(CallActivity.this, mSIPACCOUNTID, sipCall.getId(), true);
                        isHold = true;
                        iv_hold.setBackgroundResource(R.drawable.hold_selected);
                        return;
                    } else if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                        SipServiceCommand.hangUpCall(CallActivity.this, mSIPACCOUNTID, sipCall.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void loadConfiguredAccounts() {

        String TAG = SipService.class.getSimpleName();
        String PREFS_NAME = TAG + "prefs";

//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

//        String accounts = prefs.getString("accounts", "");
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(CallActivity.this);


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


    public void onTerminate() {
        Log.e("callcut","123-> "+ mCallID);
        CallActivity.confirm = false;
        SplashScreenActivity.Companion.setOncall(false);

        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = account.getActiveCalls();
       // SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
        Log.e("djdjd","which id here "+mAccountID + " activeCalls-> " +activeCalls.size());
        SipServiceCommand.hangUpActiveCalls(this, mAccountID);
        mCallStateCode = pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED;

        stopring();
        share.setValue(SharedPrefrence.UPDATE_CALL, "1");
//        speakerTurnOff();
        try {
            for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
//                cancelNotification(entry.getKey());
//                cancelNotification(getNotificationIdFromSP());
                cancelNotification(mCallID);
//                cancelNotificationThroughSharedPreference(entry.getKey());
            }
        } catch (Exception e) {
            cancelNotification(mCallID);
//            cancelNotification(getNotificationIdFromSP());
//            cancelNotificationThroughSharedPreference(mCallID);
            e.printStackTrace();
        }
        mAudioManager.setSpeakerphoneOn(false);
        iv_speaker.setBackgroundResource(R.drawable.speaker);
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        killApplication();
        finish();
    }

    public void onTerminateSingleCall(int callId) {
        CallActivity.confirm = false;

        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
//        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
//        activeCalls = account.getActiveCalls();
        //SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
        SipServiceCommand.hangUpCall(this, mAccountID, callId);
//        SipServiceCommand.hangUpActiveCalls(this, mAccountID);
        mCallStateCode = pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED;

        stopring();
        share.setValue(SharedPrefrence.UPDATE_CALL, "1");
        /*try {
            for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
//                cancelNotification(entry.getKey());
                cancelNotification(getNotificationIdFromSP());
//                cancelNotificationThroughSharedPreference(entry.getKey());
            }
        } catch (Exception e) {
//            cancelNotification(mCallID);
            cancelNotification(getNotificationIdFromSP());
//            cancelNotificationThroughSharedPreference(mCallID);
            e.printStackTrace();
        }
        Intent i = new Intent();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();*/
        Intent in = new Intent("conf_user_update");
        sendBroadcast(in);

    }

    public void onholde() {
        if (isHold) {
            isHold = false;
        } else {
            isHold = true;
        }
        SipServiceCommand.setCallHold(this, mSipAccount.getIdUri(), mCallID, isHold);
        SipServiceCommand.getCallStatus(this, mSipAccount.getIdUri(), mCallID);
    }

    public void onmute() {
        if (isMute) {
            isMute = false;
        } else {
            isMute = true;
        }
        Log.e("mutecall","send"+mSipAccount.getIdUri());
        Log.e("semdjeu","lll"+mCallID);
        SipServiceCommand.setCallMute(this, mSipAccount.getIdUri(), mCallID, isMute);
        //  SipServiceCommand.toggleCallMute(this, mSipAccount.getIdUri(), mCallID);

        SipServiceCommand.getCallStatus(this, mSipAccount.getIdUri(), mCallID);
    }

    public void onSpeaker() {

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager.isSpeakerphoneOn()) {

            iv_speaker.setBackgroundResource(R.drawable.speaker);

        } else {
            iv_speaker.setBackgroundResource(R.drawable.speaker_selected);

        }
        SipServiceCommand.getSpeaker(this, mSipAccount.getIdUri());
    }

    public void acceptIncomingCall() {

        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        if (account == null)
            return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = getCall(accountID, callID);

                if (sipCall != null) {
                    SipServiceCommand.acceptIncomingCall(this, mSipAccount.getIdUri(), sipCall.getId());
                    return;
                }

            } catch (Exception exc) {
            }
        }
        SipServiceCommand.acceptIncomingCall(this, mSipAccount.getIdUri(), mCallID);
    }

    private SipCall getCall(String accountID, int callID) {
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);

        if (account == null)
            return null;
        return account.getCall(callID);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationForCall(String acId, int callid, String mNumber) {
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "My Channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setSound(null, null);
        }

        /*Notification.Builder mBuilder = new Notification.Builder(CallActivity.this);

        Intent resultIntent = null;

        resultIntent = new Intent(CallActivity.this, CallActivity.class);

        resultIntent.putExtra(PARAM_ACCOUNT_ID, acId);
        resultIntent.putExtra(PARAM_CALL_ID, callid);
        resultIntent.putExtra(PARAM_NUMBER, mNumber);
        resultIntent.putExtra(PARAM_CALL_STATUS, "activie");
        resultIntent.putExtra(PARAM_CALL_TYPE, mCallType);
        resultIntent.putExtra(PARAM_CONNECT_TIMESTAMP, elapsedTime.getText());


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(CallActivity.this);
        stackBuilder.addParentStack(CallActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = mBuilder
                    .setAutoCancel(true)
                    .setContentTitle("VorTex")
                    .setContentIntent(resultPendingIntent)
                    .setStyle(new Notification.BigTextStyle().bigText("IT HUB Call in Progress"))
                    .setStyle(new Notification.BigTextStyle().setBigContentTitle(tv_call_status.getText().toString()))
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_color_icon))
//                    .setContentText("Call")
                    .setChannelId(CHANNEL_ID)
                    .setShowWhen(true)
                    .build();
        } else {
            notification = mBuilder
                    .setAutoCancel(true)
                    .setContentTitle("VorTex")
                    .setContentIntent(resultPendingIntent)
                    .setSound(null)
                    .setStyle(new Notification.BigTextStyle().bigText("IT HUB Call in Progress"))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setStyle(new Notification.BigTextStyle().setBigContentTitle(tv_call_status.getText().toString()))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification_color_icon))
//                    .setContentText("Call")
                    .setShowWhen(true)
                    .build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(callid, notification);*/


        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                CallActivity.this)
                .setContentTitle("Shield Crypt Call in Progress")
                .setSmallIcon(R.drawable.ic_baseline_call_24)
                .setContentText("Call");
        mBuilder.setColor(this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setOngoing(true);
        mBuilder.setVibrate(null);
        mBuilder.setChannelId(CHANNEL_ID);
        mBuilder.setAutoCancel(false);
        mBuilder.setSound(null);
        mBuilder.setNotificationSilent();

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Shield Crypt Call in Progress");
        bigText.setBigContentTitle(tv_call_status.getText().toString());
        bigText.setSummaryText("Call");
        mBuilder.setStyle(bigText);
        Intent resultIntent = null;

        resultIntent = new Intent(CallActivity.this, CallActivity.class);

        resultIntent.putExtra(PARAM_ACCOUNT_ID, acId);
        resultIntent.putExtra(PARAM_CALL_ID, callid);
        resultIntent.putExtra(PARAM_NUMBER, mNumber);
        resultIntent.putExtra(PARAM_CALL_STATUS, "activie");
        resultIntent.putExtra(PARAM_CALL_TYPE, mCallType);
        resultIntent.putExtra(PARAM_CONNECT_TIMESTAMP, elapsedTime.getText());


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(CallActivity.this);
        stackBuilder.addParentStack(CallActivity.class);
        stackBuilder.addNextIntent(resultIntent);
       // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mNotificationManager.notify(callid, mBuilder.build());
    }

    public void cancelNotification(int Callid) {

        try {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
            nMgr.cancel(Callid);
        } catch (Exception e) {
        }
    }

    public void getCallState() {
        if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_EARLY
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                stopTimer();
//            addNotificationIdAndCallIdInSharedPreference(mAccountID, mCallID, mNumber);
//            cancelNotification(getNotificationIdFromSP());
                cancelNotification(mCallID);
                setNotificationIdInSP();
//            showNotificationForCall(mAccountID, getNotificationIdFromSP(), mNumber);
                showNotificationForCall(mAccountID, mCallID, mNumber);
            }
        }
    }

    private void resumeTimer() {
        long systemClockTime = SystemClock.elapsedRealtime();
        long sharedTimer = share.getlongValue(SharedPrefrence.TIMEWHENSTOP);
        System.out.println("TImerrrr = systemclock " + systemClockTime + " Shared Timer = " + sharedTimer);
        long time = share.getlongValue(SharedPrefrence.TIMEWHENSTOP) - SystemClock.elapsedRealtime();
//        long time = SystemClock.elapsedRealtime() - share.getlongValue(SharedPrefrence.TIMEWHENSTOP);
        elapsedTime.setBase(SystemClock.elapsedRealtime() + time);
        elapsedTime.start();
    }

    private void stopTimer() {
        share.setlongValue(SharedPrefrence.TIMEWHENSTOP, elapsedTime.getBase());
        elapsedTime.stop();
    }

    private void resetTimer() {

        elapsedTime.setBase(SystemClock.elapsedRealtime());
        elapsedTime.start();
    }

    private void resetTimer(long timewhenstart) {
        elapsedTime.setBase(timewhenstart);
        elapsedTime.start();
    }

    public void stopring() {
        try {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
            toneGenerator.stopTone();
            toneGenerator.release();
            toneGenerator = null;
        } catch (Exception e) {

        }
    }

    public void checkSpeaker() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager.isSpeakerphoneOn()) {
            iv_speaker.setBackgroundResource(R.drawable.speaker_selected);
        } else {
            iv_speaker.setBackgroundResource(R.drawable.speaker);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void delayindisconnect() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                cancelNotification(mCallID);
//                cancelNotification(getNotificationIdFromSP());
//                cancelNotificationThroughSharedPreference(mCallID);
                killApplication();
                finish();
            }
        }, 200);
    }

    public void GetdatafromActivity() {
        /*HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = SipAccount.activeCalls;
        if(getIsConfCall()){

        }else{
            boolean isCallWaiting = false;
            for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
                if (entry.getValue().getCurrentState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                    isCallWaiting = true;
                }
            }
        }*/
        mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);
        mCallID = getIntent().getExtras().getInt(PARAM_CALL_ID);
        mNumber = getIntent().getExtras().getString(PARAM_NUMBER);
        mCallType = getIntent().getExtras().getString(PARAM_CALL_TYPE);
        String oldTimeStamp = sharedPrefrence.getValue(AppConstants.callStartTimeStamp);
        if (mCallType.equals("OUT")) {
            showCalligComponant();
//            accept_call_bar.setVisibility(View.GONE);
        }
        mCallState = "";
        // get from way for android 12
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S)!=null && !getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S).isEmpty()
                    && getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S).equals("yes")){
                Log.e("checkFromWay", "  -> "+ getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S));
                try {
                    String ns = Context.NOTIFICATION_SERVICE;
                    NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
                    nMgr.cancel(share.getIntValue(AppConstants.headsUpNotificationId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                loadConfiguredAccounts();
                SipServiceCommand.acceptIncomingCall(this, mSipAccount.getIdUri(), mCallID);
            } else {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
                nMgr.cancel(share.getIntValue(AppConstants.headsUpNotificationId));
            }
        }

        if (getIntent().hasExtra(PARAM_CALL_STATUS)) {
            mCallState = getIntent().getExtras().getString(PARAM_CALL_STATUS);
            isresume = true;
            String elapsedTimee = elapsedTime.getText().toString();
            if (!elapsedTime.getText().equals("00:00")) {
                resumeTimer();
            }
        }
        if (!mCallType.equals("OUT")) {
            if (!tv_call_status.getText().toString().equalsIgnoreCase("Connected")) {
                showIncomingComponant();
            }
//            componentView.setVisibility(View.INVISIBLE);


        } else if (mCallType.equals("OUT") && isresume) {
            iv_hold.setBackgroundResource(R.drawable.hold_disable);
//            ll_hold.setVisibility(View.GONE);
        }

        loadName();

        iv_mute.setBackgroundResource(R.drawable.mute);
        iv_hold.setBackgroundResource(R.drawable.hold_disable);
        iv_call_park.setBackgroundResource(R.drawable.ic_call_park_disable);
        iv_speaker.setBackgroundResource(R.drawable.speaker);
    }

    public void loadName(){
        String numberWithoutIp = Utils.getidwithoutip(mNumber);
        try {
            // IthubContactsModel ithubContactsObjectBean = appDataBase.ithubContactDao().getIthubContactsObjectByDisplayname(numberWithoutIp);
            ContactDataModel ithubContactsObjectBean = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance())
                    .contactDao().getContactNameById(numberWithoutIp);
            // number is the ithub contact
            if (ithubContactsObjectBean != null) {
                //   nametodisplay = ithubContactsObjectBean.getFirstName() + " " + ithubContactsObjectBean.getLastName();
                nametodisplay = ithubContactsObjectBean.getContactName();// + " " + ithubContactsObjectBean.getLastName();
//                Toast.makeText(CallActivity.this, "Name is = " + nametodisplay, Toast.LENGTH_LONG).show();
                tv_name.setText(nametodisplay);
                tv_number.setVisibility(View.VISIBLE);
                tv_number.setText(numberWithoutIp);
            }
            // number is not the ithub user now check in the phone book
            else {
                nametodisplay = Utils.getContactName(numberWithoutIp, CallActivity.this);
//                Toast.makeText(CallActivity.this, "Name is = " + nametodisplay, Toast.LENGTH_LONG).show();
                // number not found in the phone book
                if (numberWithoutIp.equalsIgnoreCase(nametodisplay)) {
                    tv_name.setText(numberWithoutIp);
                    tv_number.setVisibility(View.GONE);
                } else {
                    tv_name.setText(nametodisplay);
                    tv_number.setVisibility(View.VISIBLE);
                    tv_number.setText(numberWithoutIp);
                }
            }

//            Toast.makeText(CallActivity.this, "Number is = " + numberWithoutIp + " " + mNumber + " "+nametodisplay, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
//            Toast.makeText(CallActivity.this, "exc Number is = " + numberWithoutIp + " " + mNumber + " "+nametodisplay, Toast.LENGTH_LONG).show();
            tv_name.setText(numberWithoutIp);
            tv_number.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    ////// call api to send recent call ///////
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_keypad:
                openDtmf();
                break;
            case R.id.iv_hold:
                onholde();
//                callTransferForCallParking();
//                onCall();
                break;
            case R.id.iv_add_call:
                Set<Integer> activeCallIDs = SipService.mActiveSipAccounts.get(mSipAccount.getIdUri()).getCallIDs();

                Log.e("active callsss", "call" + activeCallIDs.size());
                if (activeCallIDs.size() <= 6) {
                    openITHUBContactList();
                } else {
                    Toast.makeText(CallActivity.this, "can't add more than 6 call's", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_speaker:
                onSpeaker();
                break;
            case R.id.iv_decline:
                onTerminate();
                break;
            case R.id.iv_mute:
                onmute();
                break;
            case R.id.iv_block_user:
                break;
            case R.id.iv_message:
                callMessageListDialog();
                break;
            case R.id.iv_incoming_decline:
                onTerminate();
                break;
            case R.id.iv_incoming_accept:
                acceptIncomingCall();
                break;
            case R.id.ll_message:
                break;
            case R.id.iv_call_park:
                callTransferForCallParking();
                break;
            case R.id.iv_conf_call_info:
                HashMap<Integer, SipCall> activeCalls = new HashMap<>();
                activeCalls = SipAccount.activeCalls;
                ArrayList<ConfCallBean> al_call_id = new ArrayList<>();
                for (int callid : activeCalls.keySet()) {
                    String contactName = getContactName(activeCalls, callid);
                    Log.e("nameWithUri", " contactName-> " + contactName);
                    ConfCallBean confCallBean = new ConfCallBean();
                    confCallBean.setCallId(callid);
                    confCallBean.setName(contactName);
                    //confCallBean.setName("Contact " + callid);
                    al_call_id.add(confCallBean);
                }
                ConfCallFragment fragment = new ConfCallFragment();

                Bundle bundle = new Bundle();
                bundle.putString("tag", "hiii");
                bundle.putSerializable("arraylist", al_call_id);
                fragment.setArguments(bundle);
                fragment.setCallBack(CallActivity.this);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                fragment.show(ft, "dialog");
                break;
        }
    }

    public String getContactName(HashMap<Integer, SipCall> activeCalls, int callid) {
        String contactName = "";
        SipCall call = activeCalls.get(callid);
        try {
            CallInfo callInfo = call.getInfo();
            CallerInfo callerInfo = new CallerInfo(call.getInfo());
//            CallerInfo contactInfo = new CallerInfo(call.getInfo());
            if (callInfo != null) {
                String nameWithUri = callInfo.getRemoteUri();
                String numberWithoutIp = Utils.getidwithoutip(nameWithUri);
                try {
                    //IthubContactsModel ithubContactsObjectBean = appDataBase.ithubContactDao().getIthubContactsObjectByDisplayname(numberWithoutIp);
                    if (numberWithoutIp!=null && !numberWithoutIp.isEmpty()){
                        ContactDataModel ithubContactsObjectBean =MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance())
                                .contactDao().getContactByThreadId(Utils.getNumberFromSipPath(numberWithoutIp));
                        // number is the ithub contact
                        if (ithubContactsObjectBean != null && ithubContactsObjectBean.getContactName()!=null &&
                                !ithubContactsObjectBean.getContactName().isEmpty()) {
                            contactName = ithubContactsObjectBean.getContactName();
                        } else {
                            contactName = Utils.getContactName(numberWithoutIp, CallActivity.this);
                            // number not found in the phone book
                            if (numberWithoutIp.equalsIgnoreCase(nametodisplay)) {
                                contactName = numberWithoutIp;
                            }
                        }
                    } else {
                        contactName = numberWithoutIp;
                    }
                } catch (Exception e) {
                    contactName = numberWithoutIp;
                    e.printStackTrace();
                }
            } else {
                contactName = callid + "";
            }
        } catch (Exception e) {
            contactName = callid + "";
            e.printStackTrace();
        }
        return contactName;
    }

    private void openITHUBContactList() {
        Intent in = new Intent(CallActivity.this, IthubContactForChatActivity.class);
        in.putExtra(AppConstants.ithubContactIntentTag, "call");
        in.putExtra("VideoAudioWayScreen", "audio");
        startActivity(in);
        //pickerOption();
    }

    public void initiateCall(String number) {
        Utils.hideKeyBoard(this);
        if (!Utils.checkInternetConn(this)) {
            Toast.makeText(this, getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
        } else {
            String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
            if (number.isEmpty()) {
                number = "*9000";
            }
            Log.d("sachin", "num " + number);
            SipServiceCommand.makeCall(this, id, number);
        }
    }

    private void openDtmf() {
        DtmfFragment dtmfFragment = new DtmfFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        dtmfFragment.show(ft, "dialog");
    }

    public void showIncomingComponant() {
        rl_incoming.setVisibility(View.VISIBLE);
        rl_calling.setVisibility(View.GONE);
    }

    public void showCalligComponant() {
        rl_incoming.setVisibility(View.GONE);
        rl_calling.setVisibility(View.VISIBLE);
    }

    public void disabelMute() {
        iv_mute.setBackgroundResource(R.drawable.mute);
        iv_mute.setOnClickListener(null);
    }

    public void enabelMute() {
        iv_mute.setBackgroundResource(R.drawable.mute);
        iv_mute.setOnClickListener(this);
    }

    public void disabelCallPark() {
        iv_call_park.setBackgroundResource(R.drawable.ic_call_park_disable);
        iv_call_park.setOnClickListener(null);
    }

    public void enabelCallPark() {
        iv_call_park.setBackgroundResource(R.drawable.ic_call_park_enable);
        iv_call_park.setOnClickListener(this);
    }

    public void disabelAddCall() {
        iv_add_call.setBackgroundResource(R.drawable.add_call_disable);
        iv_add_call.setOnClickListener(null);
    }

    public void enabelAddCall() {
        iv_add_call.setBackgroundResource(R.drawable.add_call_disable);
        iv_add_call.setOnClickListener(this);
    }

    public void disableHold() {
        iv_hold.setBackgroundResource(R.drawable.hold_disable);
        iv_hold.setOnClickListener(null);
    }

    public void enableHold() {
        iv_hold.setBackgroundResource(R.drawable.hold_disable);
        iv_hold.setOnClickListener(this);
    }


    /////////////////////// Call Screening Code ///////////////////////
    // Open country dialog when user click on the country code field
    public void callMessageListDialog() {
        MessageListDialogFragment messageListDialogFragment = new MessageListDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        messageListDialogFragment.setCallBack(CallActivity.this);
        messageListDialogFragment.show(ft, "dialog");
    }

    @Override
    public void doClickMessage(String message) {
        checkValidationToSendMessage(message);
    }

    public void checkValidationToSendMessage(String message) {
        if (message.equalsIgnoreCase("Customise...")) {
            setMessageTextAlertDialog(CallActivity.this);
        } else if (message.equalsIgnoreCase("")) {
            Utils.displaySnackbar(CallActivity.this, parentLayout, getString(R.string.please_write_message), R.color.blue);
        } else {
            if (Utils.checkInternetConn(CallActivity.this)) {
                createJsonToSendMessage(message);
            } else {
                Utils.displaySnackbar(CallActivity.this, parentLayout, getString(R.string.internet_check), R.color.blue);
            }
        }
    }

    // Show alert dialog to set the greeting text
    public void setMessageTextAlertDialog(final Activity activity) {
        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Dialog);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_set_call_messge_text, null);
            dialogBuilder.setView(dialogView);

            final EditText et_message_text = (EditText) dialogView.findViewById(R.id.et_message_text);
            TextView tv_done = (TextView) dialogView.findViewById(R.id.tv_done);
            TextView tv_cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
            LinearLayout ll_sms_greeting = (LinearLayout) dialogView.findViewById(R.id.ll_sms_greeting);
            final AlertDialog show = dialogBuilder.show();
            final Window window = show.getWindow();

// when user click on the greeting edit field it will open the edittext
            ll_sms_greeting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        View view = show.getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            // When user click on the set greeting button it will set to the server
            tv_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String messageText = et_message_text.getText().toString().trim();
                    if (Utils.checkInternetConn(CallActivity.this)) {
                        try {
                            View dialogView = show.getCurrentFocus();
                            if (dialogView != null) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (messageText.equalsIgnoreCase("")) {
                            Toast.makeText(CallActivity.this, getString(R.string.please_write_message), Toast.LENGTH_SHORT).show();
                        } else {
                            dialogBuilder.setCancelable(true);
                            show.dismiss();
                            createJsonToSendMessage(messageText);
                        }
                    } else {
                        Toast.makeText(CallActivity.this, getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        View dialogView = show.getCurrentFocus();
                        if (dialogView != null) {
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialogBuilder.setCancelable(true);
                    show.dismiss();

                }
            });

        } catch (Exception e) {

        }
    }

    private void createJsonToSendMessage(String message) {
       // String userId = mySharedPreferences.getValue(AppConstants.userId);
        String userId = MySharedPreferences.getSharedprefInstance().getLoginData().userid;
        JSONObject jsonObject = new JSONObject();
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("destination", Utils.getidwithoutip(mNumber));
            jsonObject.put("message", message);
            gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());
            callSendMessageApi(gsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void callSendMessageApi(JsonObject body) {
        apiservice = ApiClient.getClient().create(ApiService.class);
        Call<SendMessageResponseBean> call = apiservice.getSendMessageToGsmNumberApi(
                Utils.getBasicAuthenticationString(), body);
        progress = new ProgressDialog(CallActivity.this);
        progress.setMessage("Loading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.show();
        call.enqueue(new Callback<SendMessageResponseBean>() {
            @Override
            public void onResponse(Call<SendMessageResponseBean> call, Response<SendMessageResponseBean> response) {

                try {
                    String responseMessage = response.body().getMessage();
                    if (response.body().getStatus().equalsIgnoreCase(AppConstants.successResponse)) {
                        progress.dismiss();
                        onTerminate();
                    } else if (response.body().getStatus().equalsIgnoreCase(AppConstants.failResponse)) {
                        progress.dismiss();
//                        Utils.displaySnackbar(SendMessageActivity.this, parentLayout, responseMessage, R.color.red);
                    }
                } catch (Exception e) {
                    progress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SendMessageResponseBean> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    private static int getRandomCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900);
    }

    public int getNotificationIdFromSP() {
        int notificationId = sharedPrefrence.getIntValue(AppConstants.notificationIdForCall);
        return notificationId;
    }

    public void setNotificationIdInSP() {
        int code = getRandomCode();
        sharedPrefrence.setIntValue(AppConstants.notificationIdForCall, code);
    }

    public boolean getIsConfCall() {
        boolean isConfCall = false;
        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = SipAccount.activeCalls;
        if (activeCalls.size() > 1) {
            if (activeCalls.size() == 2) {
                boolean isCallWaiting = false;

                for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
                    if (entry.getValue().getCurrentState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                        isCallWaiting = true;
                    }
                }

                if (isCallWaiting) {
                    isConfCall = false;
                } else {
                    isConfCall = true;
                }

            } else {
                isConfCall = false;
            }
        } else {
            isConfCall = false;
        }
        return isConfCall;
    }

    @Override
    public void doClickHangUp(int callId) {
        onTerminateSingleCall(callId);
    }

    private void killApplication() {
        if (sharedPrefrence.getValue(AppConstants.isAppKilled).equalsIgnoreCase(AppConstants.trueValue)) {
            Log.e("onResume", " killApplication-> " + sharedPrefrence.getValue(AppConstants.isAppKilled));
            ExitActivity.exitApplication(CallActivity.this);
        }
    }

    // Methods for call parking feature
    public void onCall() {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
        String number = "70";
        if (number.isEmpty()) {
            number = "*9000";
        }
        SipServiceCommand.makeCall(CallActivity.this, id, number);
    }

    public void callTransferForCallParking() {
//        SipServiceCommand.call
        CallOpParam callOpParam = new CallOpParam();
        //   SipServiceCommand.setXferCall(CallActivity.this, mSipAccount.getIdUri(), mCallID, "70");
        SipServiceCommand.transferCall(CallActivity.this, mSipAccount.getIdUri(), mCallID, "70");
    }

    public void speakerTurnOff() {
        try {
            AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (mAudioManager.isSpeakerphoneOn()) {

                iv_speaker.setBackgroundResource(R.drawable.speaker);

            }
            SipServiceCommand.getSpeaker(this, mSipAccount.getIdUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
