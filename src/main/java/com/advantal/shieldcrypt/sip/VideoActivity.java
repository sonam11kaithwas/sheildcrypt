package com.advantal.shieldcrypt.sip;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PictureInPictureParams;
import android.app.ProgressDialog;
import android.app.RemoteAction;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.conference.VideoAddedConUserAdapter;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.sip.videoconf.AddedUserVideoConf;
import com.advantal.shieldcrypt.sip.videoconf.ContactForVideoConfDialogFragment;
import com.advantal.shieldcrypt.splash_scr_pkg.SplashScreenActivity;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.CallReconnectionState;
import net.gotev.sipservice.Logger;
import net.gotev.sipservice.MediaState;
import net.gotev.sipservice.RtpStreamStats;
import net.gotev.sipservice.SharedPreferencesHelper;
import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipCall;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;
import net.gotev.sipservice.SipServiceConstants;

import org.pjsip.pjsua2.CallInfo;
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


public class VideoActivity extends AppCompatActivity implements SipServiceConstants {

    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder = null;
    private LinearLayout mBottomSheetLayout;
    private BottomSheetBehavior sheetBehavior;
    private ImageView header_Arrow_Image;

    private final String KEY_SIP_ACCOUNT = "sip_account";
    private SipAccountData mSipAccount;
    SipAccount mSipA;
    static SipCall mSipCall;
    public SharedPrefrence share;
    int mCallStateCode;
    SharedPrefrence mySharedPreferences;
    MyAppDataBase appDataBase;
    private BroadcastReceiver networkReceiver;
    private BroadcastReceiver callstateupdateReceiver;
    Chronometer elapsedTime;
    AudioManager mAudioManager;

    String mAccountID, mCallState, activeCallClickedWay = "";
    int mCallID = -1;
    String mNumber, mCallType = "";
    SurfaceView mVideoPreview, mSurfaceView, surfaceByDefaultCamera;

    ImageView mButtonAccept, mButtonDecline, mMuteImgBtn, mStopPreview, mSwitchCam, declineImgBtn;
    LinearLayout mOutgoingLL, mUserDetailLL;
    RelativeLayout mIncomingLL;

    private static CallInfo lastCallInfo;
    private RecyclerView rvVideoConAddedContact;
    private VideoAddedConUserAdapter videoAddedConUserAdapter;
    private List<ConfCallBean> confCallBeans = new ArrayList<>();
    private ProgressDialog progress;

    boolean isMute, isSwitch = true, isStopPreview;
    boolean isresume = false;
    TextView tv_call_status, tv_name;
    MaterialToolbar topAppBar;
    private boolean addParticipantsEnableStatus = false;
    private RelativeLayout rlAddParticipants;
    private RecyclerView idCourseRV;
    private int screenHeight = 800;
    private SharedPrefrence sharedPrefrence;
    private BroadcastReceiver mReceiver;
    private static final String ACTION_MEDIA_CONTROL = "media_control";
    private static final String EXTRA_CONTROL_TYPE = "control_type";

    private static final int REQUEST_PLAY = 1;
    private static final int REQUEST_PAUSE = 2;
    private static final int REQUEST_INFO = 3;
    private static final int CONTROL_TYPE_PLAY = 1;
    private static final int CONTROL_TYPE_PAUSE = 2;

    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int field = 0x00000020;
    private ProgressBar progressBarView;
    Camera camera;

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("pause called");
        try {
          /*  if (wakeLock.isHeld()) {
                wakeLock.release();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        getCallState();
    }

    /**
     * Enters Picture-in-Picture mode.
     */

    void minimize() {
        /*if (mVideoPreview == null) {
            return;
        }
        Rational aspectRatio = new Rational(mVideoPreview.getWidth(), mVideoPreview.getHeight());*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            Log.e("onPictureInPictureModeChanged", " 1111--> ");
            if (mPictureInPictureParamsBuilder==null){
                mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mPictureInPictureParamsBuilder.setAutoEnterEnabled(true);
                mPictureInPictureParamsBuilder.setSeamlessResizeEnabled(true);
            }
//            Rational aspectRatio = new Rational(mVideoPreview.getWidth(), mVideoPreview.getHeight());
//            mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
        } else{
           // Toast.makeText(this, "Your device doesn't supports PIP", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        onUserLeaveHint();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mPictureInPictureParamsBuilder.setAutoEnterEnabled(true);
                mPictureInPictureParamsBuilder.setSeamlessResizeEnabled(true);
            }
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
        }*/
        //when user presses home button, if not in PIP mode, enter in PIP, requires Android N and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minimize();
        }
    }

    public void pipEnable(){
        topAppBar.setVisibility(View.GONE);
        mOutgoingLL.setVisibility(View.GONE);
        int margin = getResources().getDimensionPixelSize(R.dimen.pip_inner_view_enable);
        mSurfaceView.getLayoutParams().height = margin;
        mSurfaceView.getLayoutParams().width = margin;
        mSurfaceView.requestLayout();
    }

    public void pipDisable(){
        topAppBar.setVisibility(View.VISIBLE);
        mOutgoingLL.setVisibility(View.VISIBLE);
        mSurfaceView.setVisibility(View.VISIBLE);
        int margin = getResources().getDimensionPixelSize(R.dimen.pip_inner_view);
        mSurfaceView.getLayoutParams().height = margin;
        mSurfaceView.getLayoutParams().width = margin;
        mSurfaceView.requestLayout();
    }

    @Override
    public void onPictureInPictureModeChanged(
            boolean isInPictureInPictureMode, Configuration configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, configuration);
        }
        Log.e("onPictureInPictureModeChanged", " Lifecycle--> " + getLifecycle().getCurrentState());

        /*if (mPictureInPictureParamsBuilder==null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            }
        }*/
        Log.e("onPictureInPictureModeChanged", " --> " +isInPictureInPictureMode + " callTyp--> " + mCallType);
        if (isInPictureInPictureMode) {
            pipEnable();
            // Starts receiving events from action items in PiP mode.
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            //unregisterReceiver(mReceiver);
           // mReceiver = null;
            // Show the video controls if the video is not playing
                        if (mSipAccount!=null && mSipAccount.getIdUri()!=null){
                String accountID = mSipAccount.getIdUri();
                SipAccount account = SipService.mActiveSipAccounts.get(accountID);
                if (account == null)
                    return;
                Set<Integer> activeCallIDs = account.getCallIDs();
                if (activeCallIDs.size()>0) {
                    SipCall sipCall = account.getCall(mCallID);
                    if (sipCall == null) {
                        return;
                    } else {
                        mCallStateCode = sipCall.getCurrentState();
                    }
                    if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                        try {
                            pipDisable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mCallType.equals("OUT")) {
                            pipDisable();
                        }
                    }
                }
            }
        }

        if (getLifecycle().getCurrentState() == Lifecycle.State.CREATED){
            onTerminate();
        }
    }

    public void createRemoteAction(){
        final ArrayList<RemoteAction> actions = new ArrayList<>();
        final PendingIntent intent =
                PendingIntent.getBroadcast(
                        VideoActivity.this, CONTROL_TYPE_PAUSE,
                        new Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, REQUEST_PAUSE),
                        0);
        final Icon icon = Icon.createWithResource(VideoActivity.this, R.drawable.cross_black_icon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            actions.add(new RemoteAction(icon, "Remove", "Remove", intent));
            mPictureInPictureParamsBuilder.setActions(actions);
            // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
            // Note this call can happen even when the app is not in PiP mode. In that case, the
            // arguments will be used for at the next call of #enterPictureInPictureMode.
            setPictureInPictureParams(mPictureInPictureParamsBuilder.build());
        }
    }

    void initUI() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        mVideoPreview = (SurfaceView) findViewById(R.id.surfaceIncomingVideo);
        //mVideoPreview.getHolder().setFixedSize(width, height);

        surfaceByDefaultCamera = (SurfaceView)findViewById(R.id.surfaceByDefaultCamera);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfacePreviewCapture);
        mIncomingLL = findViewById(R.id.ll_incoming_componant);
        mOutgoingLL = findViewById(R.id.ll_outgoing_componant);
        mUserDetailLL = findViewById(R.id.userDetailLL);
        mButtonAccept = findViewById(R.id.iv_incoming_accept);
        mButtonDecline = findViewById(R.id.iv_incoming_decline);
        declineImgBtn = findViewById(R.id.declineImgBtn);
        mSwitchCam = findViewById(R.id.switchCamImg);
        mStopPreview = findViewById(R.id.stopPreviewImg);
        mMuteImgBtn = findViewById(R.id.muteImg);
        tv_call_status = findViewById(R.id.tv_call_status);
        tv_name = findViewById(R.id.tv_name);
        topAppBar = (MaterialToolbar)findViewById(R.id.topAppBar);
        rlAddParticipants = findViewById(R.id.rlAddParticipants);

        mBottomSheetLayout = findViewById(R.id.mBottomSheetLayout);
        sheetBehavior = BottomSheetBehavior.from(mBottomSheetLayout);
        header_Arrow_Image = findViewById(R.id.bottom_sheet_arrow);

        rvVideoConAddedContact = findViewById(R.id.rvVideoConAddedContact);
        idCourseRV = findViewById(R.id.idCourseRV);
        progressBarView = findViewById(R.id.progressBarView);

        header_Arrow_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                header_Arrow_Image.setRotation(slideOffset * 180);
            }
        });

        mSwitchCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera(isSwitch);
            }
        });
        mStopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopVideoPreview(isStopPreview);

            }
        });

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onBackPressed();
                minimize();
            }
        });

        mButtonDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTerminate();
            }
        });

        declineImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTerminate();
            }
        });

        rlAddParticipants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addParticipantsEnableStatus){
                    gettingActiveCalls();
                }
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        mMuteImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                muteVideoCall();
            }
        });
    }

    public void conVideoAddedUser(ArrayList<ConfCallBean> al_call_id) {
        videoAddedConUserAdapter = new VideoAddedConUserAdapter(VideoActivity.this, al_call_id,
                new VideoAddedConUserAdapter.CallBack() {
                    @Override
                    public void doClickItem(int position) {

                    }
                });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VideoActivity.this);
        rvVideoConAddedContact.setLayoutManager(layoutManager);
        rvVideoConAddedContact.setItemAnimator(new DefaultItemAnimator());
        rvVideoConAddedContact.setAdapter(videoAddedConUserAdapter);
    }

    public void gettingActiveCalls(){
        Set<Integer> activeCallIDs = SipService.mActiveSipAccounts.get(mSipAccount.getIdUri()).getCallIDs();
        Log.e("active callsss", "call " + activeCallIDs.size());
        if (activeCallIDs.size() <= 5) {
            openContactList();
            //SipServiceCommand.makeCall(this, "sip:7509384950@shieldcrypt.co.in", "8109383638", true, false);
        } else {
            Toast.makeText(VideoActivity.this, "can't add more than 5 call's", Toast.LENGTH_SHORT).show();
        }
    }

    private void openContactList() {
        /*Intent in = new Intent(VideoActivity.this, IthubContactForChatActivity.class);
        in.putExtra(AppConstants.ithubContactIntentTag, "call");
        in.putExtra("VideoAudioWayScreen", "video");
        startActivity(in);*/

        ContactForVideoConfDialogFragment fragment = new ContactForVideoConfDialogFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        fragment.setCallBack(new ContactForVideoConfDialogFragment.CallBack() {
            @Override
            public void doClickMakeCall(String number) {
                if (mAccountID!=null && !mAccountID.isEmpty()){
                    Log.e("clickOnAdded", "  doClickMakeCall-> " + number + " mAccountID-> " + mAccountID);
                    SipServiceCommand.makeCall(VideoActivity.this, mAccountID,
                            number, true, true);
                }
            }
        });
        fragment.show(ft, "dialog");
        //pickerOption();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mySharedPreferences = SharedPrefrence.getInstance(VideoActivity.this);
        //appDataBase = AppDataBase.getAppDatabase(VideoActivity.this);
        appDataBase = MyAppDataBase.Companion.getUserDataBaseAppinstance(this);

        share = SharedPrefrence.getInstance(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        sharedPrefrence = SharedPrefrence.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
        }
        //createRemoteAction();
        initUI();

        try {
            // Yeah, this is hidden field.
            field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);

            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(field, getLocalClassName());
        } catch (Throwable ignored) {
        }

        clearAlNotification();
        GetdatafromActivity();



        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT);
            String accountID = mSipAccount.getIdUri();
            mSipA = SipService.mActiveSipAccounts.get(accountID);
            mSipCall = getCall(accountID, mCallID);
        }

        if (!activeCallClickedWay.equals("activated")){
            loadData();
        } else {
           /* sipEvents.register(this);
            if (mSipAccount != null) {
                SipServiceCommand.getRegistrationStatus(this, mSipAccount.getIdUri());
            }
            String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
            loadConfiguredAccounts();*/
            loadData();
        }
    }

    private void clearAlNotification(){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
        nMgr.cancelAll();
    }

    private void startCamera() {
        try{
            camera = Camera.open();
        }catch(RuntimeException e){
            Log.e("startCamera", " init_camera: " + e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();
        //modify parameter
        param.setPreviewFrameRate(20);
        param.setPreviewSize(176, 144);
        camera.setParameters(param);
        try {
            camera.setPreviewDisplay(surfaceByDefaultCamera.getHolder());
            camera.startPreview();
            //camera.takePicture(shutter, raw, jpeg)
        } catch (Exception e) {
            Log.e("startCamera", " init_camera: " + e);
            return;
        }
    }

    private BroadcastEventReceiver sipEvents = new BroadcastEventReceiver() {

        @Override
        public void onRegistration(String accountID, int registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
                //Toast.makeText(CallActivity.this, "Registered", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(CallActivity.this, "Unregistered", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onVideoSize(int width, int height) {
            super.onVideoSize(width, height);
            Logger.debug("<<--Call3",
                    ", width: " + width +
                            ", height: " + height);
            mVideoPreview.getHolder().setFixedSize(width, height);
        }

        @Override
        public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
            super.onIncomingCall(accountID, callID, displayName, remoteUri, isVideo);
            Log.e("<<--onIncomingCall",
                    ", accountID: " + accountID +
                            ", callID: " + callID +
                            ", displayName: " + displayName +
                            ", remoteUri: " + remoteUri+
                            ", isVideo: " + isVideo);

        }

        @Override
        protected void onCallStats(int callID, int duration, String audioCodec, int callStatusCode, RtpStreamStats rx, RtpStreamStats tx) {
            super.onCallStats(callID, duration, audioCodec, callStatusCode, rx, tx);
            Logger.debug("<<--Call3",
                    ", audioCodec: " + audioCodec +
                            ", rx: " + rx +
                            ", tx: " + tx +
                            ", callStatusCode: " + callStatusCode);
        }

        @Override
        public void onCallMediaState(String accountID, int callID, MediaState stateType, boolean stateValue) {
            super.onCallMediaState(accountID, callID, stateType, stateValue);
            Logger.debug("<<--Call2", "callID: " + callID +
                    ", callStat: " + stateType +
                    ", stateValue: " + stateValue);
        }

        @Override
        protected void onCallReconnectionState(CallReconnectionState state) {
            super.onCallReconnectionState(state);
            if (state.name().equals("SUCCESS")){
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SipServiceCommand.stopVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID);
                        SipServiceCommand.startVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mSurfaceView.getHolder().getSurface());
                       // SipServiceCommand.setupIncomingVideoFeed(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mVideoPreview.getHolder().getSurface());
                        SipServiceCommand.getCallStatus(VideoActivity.this, mSipAccount.getIdUri(), mCallID);
                        progressBarView.setVisibility(View.GONE);
                        if (progress!=null && progress.isShowing()){
                            progress.dismiss();
                        }
                    }
                }, 1000);
            }
        }

        @Override
        public void onCallState(String accountID, int callID, int callStateCode, int callStatusCode,
                                long connectTimestamp, boolean isLocalHold, boolean isLocalMute, boolean isLocalVideoMute) {
            super.onCallState(accountID, callID, callStateCode, callStatusCode, connectTimestamp, isLocalHold, isLocalMute, isLocalVideoMute);
            Logger.debug("<<--Call", "  callID: onCallState-> " + callID +
                    ", callStat: " + callStateCode +
                    ", Timestamp: " + connectTimestamp +
                    ", isLocalHold: " + isLocalHold +
                    ", isLocalVideoMute: " + isLocalVideoMute +
                    ", isLocalMute: " + isLocalMute);


            mCallStateCode = callStateCode;
            isMute = isLocalMute;
            isStopPreview = isLocalVideoMute;
            //mCallID = callID;
            if (callStatusCode==pjsip_status_code.PJSIP_SC_RINGING){
                tv_call_status.setText("Ringing...");
                //startCamera();
            } else {
                tv_call_status.setText("Calling...");
            }

            if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                Log.d("sachin", String.valueOf(callStateCode));
                SipServiceCommand.stopVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID);

//                if (SipAccount.activeCalls.size() == 0) {
                //   iv_conf_call_info.setVisibility(View.GONE);
                // callLogDuration = elapsedTime.getText() + "";
                mAudioManager.setSpeakerphoneOn(false);
              //  SplashActivity.oncall = false;
                SplashScreenActivity.Companion.setOncall(false);
                // iv_speaker.setBackgroundResource(R.drawable.speaker);
                if (mCallID == callID) {
                    //   stopTimer();
                    share.setValue(SharedPrefrence.UPDATE_CALL, "1");

                 /*   if (mCallType.equals("OUT") && connectTimestamp == 0) {
                        delayindisconnect();
                    } else {*/
                    //                        cancelNotification(mCallID);
                    killApplication();
                    finish();
                    //  }
                }

                Intent in = new Intent("conf_call");
                sendBroadcast(in);
                /*} else {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                    Intent in = new Intent("conf_call");
                    sendBroadcast(in);
                }*/
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                if (mAudioManager!=null){
                    mAudioManager.setSpeakerphoneOn(true);
                }
                Log.d("sachin", String.valueOf(callStateCode));
               // tv_call_status.setText("Calling...");
                Log.d("gettingActiveCalls","  PJSIP_INV_STATE_CALLING-> "+ SipAccount.activeCalls.size());
                if (SipAccount.activeCalls.size() > 1) {

                } else {
                    addParticipantsEnableStatus = false;
                }

               /* if (SipAccount.activeCalls.size() > 1) {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                } else {
                    iv_conf_call_info.setVisibility(View.GONE);
                    showCalligComponant();
                    confirm = true;
//                accept_call_bar.setVisibility(View.GONE);
                    elapsedTime.setVisibility(View.GONE);
                    tv_call_status.setText("Calling...");
                    disableHold();
                    disabelMute();
                    disabelCallPark();
                }*/
//                ll_hold.setVisibility(View.GONE);
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                Log.d("sachin", String.valueOf(callStateCode));

                tv_call_status.setText("Incoming");

            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                Log.d("sachin", String.valueOf(callStateCode));

                Log.d("gettingActiveCalls","  PJSIP_INV_STATE_EARLY-> "+ SipAccount.activeCalls.size());


                if (!mCallType.equals("OUT")) {
                    tv_call_status.setText("Incoming");
                } else {
                   // tv_call_status.setText("Calling...");
                    addParticipantsEnableStatus = false;
                }

            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
            } else if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                if (mAudioManager!=null){
                    mAudioManager.setSpeakerphoneOn(true);
                }
                Log.d("sachin", String.valueOf(callStateCode));
                mUserDetailLL.setVisibility(View.GONE);
                addParticipantsEnableStatus = true;
               // Log.d("gettingActiveCalls","  PJSIP_INV_STATE_CONFIRMED-> "+ SipAccount.activeCalls.size());
                enableAllBtn();
                showIncomingComponent(false);
                try {
                    Set<Integer> activeCallIDs = SipService.mActiveSipAccounts.get(mSipAccount.getIdUri()).getCallIDs();
                    Log.e("activecallsss", "call1111-> " + activeCallIDs);
                    /*if (SipAccount.activeCalls.size()>1){
                        idCourseRV.setVisibility(View.GONE);
                        mSurfaceView.setVisibility(View.GONE);
                        mVideoPreview.setVisibility(View.GONE);
                    } else {
                        idCourseRV.setVisibility(View.GONE);
                        mSurfaceView.setVisibility(View.VISIBLE);
                        mVideoPreview.setVisibility(View.VISIBLE);
                      //  SipServiceCommand.stopVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID);
                        SipServiceCommand.startVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mSurfaceView.getHolder().getSurface());

                        SipServiceCommand.setupIncomingVideoFeed(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mVideoPreview.getHolder().getSurface());
                        onConfigurationChanged(getResources().getConfiguration());
                    }*/
                    idCourseRV.setVisibility(View.GONE);
                    mSurfaceView.setVisibility(View.VISIBLE);
                    mVideoPreview.setVisibility(View.VISIBLE);
                    //  SipServiceCommand.stopVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID);
                    SipServiceCommand.startVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mSurfaceView.getHolder().getSurface());

                    SipServiceCommand.setupIncomingVideoFeed(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mVideoPreview.getHolder().getSurface());
                    onConfigurationChanged(getResources().getConfiguration());
                    //getActiveCalls();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*SplashActivity.oncall = true;
                if (SipAccount.activeCalls.size() > 1) {
                    iv_conf_call_info.setVisibility(View.VISIBLE);
                } else {
                    iv_conf_call_info.setVisibility(View.GONE);

                    confirm = true;
                    *//*if (PhoneCallStateListener.statecheck == 2) {
                        onholdAct();
                    }*//*

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
                    mySharedPreferences.setValue(AppConstants.callStartTimeStamp, System.currentTimeMillis() + "");
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
                }*/

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
            //  checkSpeaker();

        }
    };

    public void createProgressDialog(){
        progress = new ProgressDialog(this);
      //  progress.setTitle("Please wait....");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage("Connecting...");
        progress.show();
    }

    public void loadSurfaceAdapter(ArrayList<ConfCallBean> al_call_id){
        AddedUserVideoConf adapter = new AddedUserVideoConf(al_call_id, this, screenHeight, mSipAccount.getIdUri());
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (al_call_id.size()==4){
                    return 1;
                } else {
                    if (position >0)
                        return 1;
                    else
                        return 2;
                }
            }
        });
        // at last set adapter to recycler view.
        idCourseRV.setLayoutManager(layoutManager);
        idCourseRV.setAdapter(adapter);

      //  onConfigurationChanged(getResources().getConfiguration());
    }

    public void getActiveCalls(){
        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = SipAccount.activeCalls;
        ArrayList<ConfCallBean> al_call_id = new ArrayList<>();
        try {
            for (int callid : activeCalls.keySet()) {
                SipCall call = activeCalls.get(callid);
                Log.d("gettingActiveCalls","  getActiveCalls-> "+ activeCalls + " -> " +
                        call.getInfo().getRemoteUri() + "  callId-> " + callid);
                //String contactName = getContactName(activeCalls, callid);
                ConfCallBean confCallBean = new ConfCallBean();
                confCallBean.setCallId(callid);
                confCallBean.setName(""+call.getInfo().getRemoteUri() + "  -> "+ callid);
                al_call_id.add(confCallBean);
            }

            if (al_call_id!=null && al_call_id.size()>0){
                conVideoAddedUser(al_call_id);
                if (SipAccount.activeCalls.size()>1){

                    ConfCallBean confCallBean = new ConfCallBean();
                    confCallBean.setCallId(mCallID);
                    confCallBean.setName(""+mSipAccount.getIdUri());
                    al_call_id.add(confCallBean);
                    loadSurfaceAdapter(al_call_id);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSipAccount != null) {
            outState.putParcelable(KEY_SIP_ACCOUNT, mSipAccount);
        }

        super.onSaveInstanceState(outState);
    }

    public void loadData(){
        if (mSipAccount==null) {
            loadConfiguredAccounts();
        }
        System.out.println("onResume 11 video-> " +mSipAccount);
        sipEvents.register(this);
        if (mSipAccount != null) {
            SipServiceCommand.getRegistrationStatus(this, mSipAccount.getIdUri());
        }
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
        loadConfiguredAccounts();

        SipServiceCommand.getRegistrationStatus(this, id);
        SipServiceCommand.getCallStatus(this, mSipAccount.getIdUri(), mCallID);
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                killApplication();
                finish();
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
                    //holdCallWhileGsmCall();
                } else {
                    if (mcallstate.equals("HOLD")) {
                        System.out.println("dsfjsklfsdhhhhhhh");
                        //  iv_hold.setBackgroundResource(R.drawable.hold_selected);
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

        // checkSpeaker();
        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        if (account == null)
            return;

        Set<Integer> activeCallIDs = account.getCallIDs();
        cancelNotification(mCallID);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setNotificationIdInSP();
            showNotificationForCall(mAccountID, mCallID, mNumber);
        }
        disableAllBtn();
        //onSpeaker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBarView.setVisibility(View.GONE);
        try {
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        GetdatafromActivity();

        Log.e("onResume", "onResume-> " +mSipAccount);

     //   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (mSipAccount!=null && mSipAccount.getIdUri()!=null){
                String accountID = mSipAccount.getIdUri();
                SipAccount account = SipService.mActiveSipAccounts.get(accountID);
                if (account == null)
                    return;

                Set<Integer> activeCallIDs = account.getCallIDs();
                if (activeCallIDs.size()>0) {
                  /*  Log.e("onResume", " onResume11111-> "+activeCallIDs.size() + " mCallStateCode-> " + mCallStateCode
                            + " --> "+ pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED +
                            " mCallID->> " + mCallID);*/

                    SipCall sipCall = account.getCall(mCallID);

                    if (sipCall == null) {
                        return;
                    } else {
                        mCallStateCode = sipCall.getCurrentState();
                    }

                    if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                        try {
                           // Log.e("activecallsss", "call1111-> " + activeCallIDs);
                            idCourseRV.setVisibility(View.GONE);
                            mSurfaceView.setVisibility(View.VISIBLE);
                            mVideoPreview.setVisibility(View.VISIBLE);
                            progressBarView.setVisibility(View.VISIBLE);
                            try {
                                final Handler handler = new Handler(Looper.getMainLooper());
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBarView.setVisibility(View.VISIBLE);
                                       // createProgressDialog();
                                        SipServiceCommand.reconnectCall(VideoActivity.this);
                                    }
                                }, 1000);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
   //     }

        cancelNotification(mCallID);
        clearAlNotification();
        setNotificationIdInSP();
        showNotificationForCall(mAccountID, mCallID, mNumber);
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
                        //SipServiceCommand.setCallHold(VideoActivity.this, mSIPACCOUNTID, sipCall.getId(), true);
                        return;
                    } else if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
                        SipServiceCommand.hangUpCall(VideoActivity.this, mSIPACCOUNTID, sipCall.getId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onSpeaker() {

        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (mAudioManager.isSpeakerphoneOn()) {

            // iv_speaker.setBackgroundResource(R.drawable.speaker);

        } else {
            // iv_speaker.setBackgroundResource(R.drawable.speaker_selected);

        }
        SipServiceCommand.getSpeaker(this, mSipAccount.getIdUri());
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
        if (getIntent().getExtras().getString("activeCallClickedWay")!=null && !getIntent().getExtras().getString("activeCallClickedWay").isEmpty()){
            activeCallClickedWay = getIntent().getExtras().getString("activeCallClickedWay");
            Log.e("activeCallClickedWay", "  -> "+ activeCallClickedWay);
        }

        if (mCallType.equalsIgnoreCase("Out")) {
            showIncomingComponent(false);
        } else {
            showIncomingComponent(true);
        }

        // get from way for android 12
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S)!=null
                    && !getIntent().getExtras().getString(PARAM_IS_FROM_WAY_FOR_VERSION_S).isEmpty()
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
                acceptIncomingCall(mButtonAccept);
            } else {
                String ns = Context.NOTIFICATION_SERVICE;
                NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
                nMgr.cancel(share.getIntValue(AppConstants.headsUpNotificationId));
            }
        }*/

        if (getIntent().hasExtra(PARAM_CALL_STATUS)) {
            mCallState = getIntent().getExtras().getString(PARAM_CALL_STATUS);
            isresume = true;

        }
        String numberWithoutIp = Utils.getidwithoutip(mNumber);

        try {
            ContactDataModel ithubContactsObjectBean = appDataBase.contactDao().getContactNameById(numberWithoutIp);
           // IthubContactsModel ithubContactsObjectBean = appDataBase.ithubContactDao().getIthubContactsObjectByDisplayname(numberWithoutIp);
            // number is the ithub contact
            if (ithubContactsObjectBean != null) {
              //  String nametodisplay = ithubContactsObjectBean.getFirstName() + " " + ithubContactsObjectBean.getLastName();
                String nametodisplay = ithubContactsObjectBean.getContactName();// + " " + ithubContactsObjectBean.getLastName();
//                Toast.makeText(CallActivity.this, "Name is = " + nametodisplay, Toast.LENGTH_LONG).show();
                tv_name.setText(nametodisplay);

            }
            // number is not the ithub user now check in the phone book
            else {
                String nametodisplay = Utils.getContactName(numberWithoutIp, VideoActivity.this);
//                Toast.makeText(CallActivity.this, "Name is = " + nametodisplay, Toast.LENGTH_LONG).show();
                // number not found in the phone book
                if (numberWithoutIp.equalsIgnoreCase(nametodisplay)) {
                    tv_name.setText(numberWithoutIp);
//                    tv_number.setVisibility(View.GONE);
                } else {
                    tv_name.setText(nametodisplay);
                    /*tv_number.setVisibility(View.VISIBLE);
                    tv_number.setText(numberWithoutIp);*/
                }
            }

//            Toast.makeText(CallActivity.this, "Number is = " + numberWithoutIp + " " + mNumber + " "+nametodisplay, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
//            Toast.makeText(CallActivity.this, "exc Number is = " + numberWithoutIp + " " + mNumber + " "+nametodisplay, Toast.LENGTH_LONG).show();
            tv_name.setText(numberWithoutIp);
           /* tv_number.setVisibility(View.GONE);
            e.printStackTrace();*/
        }

    }

    void showIncomingComponent(boolean type) {
        if (type) {
            mIncomingLL.setVisibility(View.VISIBLE);
            mOutgoingLL.setVisibility(View.GONE);
        } else {
            mIncomingLL.setVisibility(View.GONE);
            mOutgoingLL.setVisibility(View.VISIBLE);
        }
    }

    public void muteVideoCall() {
        if (isMute) {
            mMuteImgBtn.setBackgroundResource(R.drawable.ic_video_unhold);
            isMute = false;
        } else {
            mMuteImgBtn.setBackgroundResource(R.drawable.ic_video_hold);
            isMute = true;
        }
        Log.e("muteVideoCall" , " 111-> " + isMute);
       SipServiceCommand.setCallMute(this, mSipAccount.getIdUri(), mCallID, isMute);
      // SipServiceCommand.setVideoMute(this, mSipAccount.getIdUri(), mCallID, isMute);
       // SipServiceCommand.toggleCallMute(this, mSipAccount.getIdUri(), mCallID);
        SipServiceCommand.getCallStatus(VideoActivity.this, mSipAccount.getIdUri(), mCallID);

    }

    void disableAllBtn() {
        mMuteImgBtn.setEnabled(false);
        mStopPreview.setEnabled(false);
        mSwitchCam.setEnabled(false);

        mMuteImgBtn.setAlpha((float) .3);
        mStopPreview.setAlpha((float) .3);
        mSwitchCam.setAlpha((float) .3);
    }

    void enableAllBtn() {
        mMuteImgBtn.setEnabled(true);
        mStopPreview.setEnabled(true);
        mSwitchCam.setEnabled(true);

        mMuteImgBtn.setAlpha((float) 1);
        mStopPreview.setAlpha((float) 1);
        mSwitchCam.setAlpha((float) 1);
    }

    public void stopVideoPreview(boolean type) {

        if (type) {
            //isStopPreview = false;
            mStopPreview.setBackgroundResource(R.drawable.ic_vid_on);
            //SipServiceCommand.startVideoPreview(VideoActivity.this, mSipAccount.getIdUri(), mCallID, mSurfaceView.getHolder().getSurface());


        } else {
            //isStopPreview = true;
            mStopPreview.setBackgroundResource(R.drawable.ic_vid_off);
            //SipServiceCommand.stopVideoPreview(this, mSipAccount.getIdUri(), mCallID);

        }

        SipServiceCommand.setVideoMute(this, mSipAccount.getIdUri(), mCallID, type ? false : true);
        SipServiceCommand.getCallStatus(VideoActivity.this, mSipAccount.getIdUri(), mCallID);


    }

    public void switchCamera(boolean type) {
        if (type) {
            isSwitch = false;
            //mSwitchCam.setBackgroundResource(R.drawable.ic_cam_rotate2);
            mSwitchCam.setBackgroundResource(R.drawable.ic_cam_rotate1_new);
        } else {
            isSwitch = true;
            //mSwitchCam.setBackgroundResource(R.drawable.ic_cam_rotate1);
            mSwitchCam.setBackgroundResource(R.drawable.ic_cam_rotate1_new);

        }
        SipServiceCommand.switchVideoCaptureDevice(this, mSipAccount.getIdUri(), mCallID);
        //  SipServiceCommand.getCallStatus(VideoActivity.this, mSipAccount.getIdUri(), mCallID);

    }

    public void cancelNotification(int Callid) {

        try {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) this.getSystemService(ns);
            nMgr.cancel(Callid);
        } catch (Exception e) {
        }
    }

    private void killApplication() {
        if (mySharedPreferences.getValue(AppConstants.isAppKilled).equalsIgnoreCase(AppConstants.trueValue)) {
            ExitActivity.exitApplication(this);
        }
    }

    public void acceptIncomingCall(View view) {

        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        if (account == null)
            return;

        Set<Integer> activeCallIDs = account.getCallIDs();

        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = getCall(accountID, callID);

                if (sipCall != null) {
                    SipServiceCommand.acceptIncomingCall(this, mSipAccount.getIdUri(), sipCall.getId(), true);
                    return;
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        SipServiceCommand.acceptIncomingCall(this, mSipAccount.getIdUri(), mCallID, true);
    }

    private SipCall getCall(String accountID, int callID) {
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);

        if (account == null)
            return null;
        return account.getCall(callID);
    }


    private void loadConfiguredAccounts() {

        String TAG = SipService.class.getSimpleName();
        String PREFS_NAME = TAG + "prefs";

//        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

//        String accounts = prefs.getString("accounts", "");
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);


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

    public void stopring() {
        try {
            ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
            toneGenerator.stopTone();
            toneGenerator.release();
            toneGenerator = null;
        } catch (Exception e) {

        }
    }

    public void onTerminate() {
        CallActivity.confirm = false;

        Log.e("djdjd","which id here "+mAccountID);
        Log.e("callcut"," onTerminate-> "+ mCallID);
        //SplashActivity.oncall = false;
        SplashScreenActivity.Companion.setOncall(false);
        String accountID = mSipAccount.getIdUri();
        SipAccount account = SipService.mActiveSipAccounts.get(accountID);
        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = account.getActiveCalls();
//        SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
       // SipServiceCommand.hangUpActiveCalls(this, mAccountID);
       /* if (activeCalls.size()>0){
            SipServiceCommand.hangUpActiveCalls(this, mAccountID);
        } else {
            SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
        }*/
        SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
        mCallStateCode = pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED;

        stopring();
        share.setValue(SharedPrefrence.UPDATE_CALL, "1");
//        speakerTurnOff();
        try {
            for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
//            cancelNotification(mCallID);
            }
        } catch (Exception e) {
            //    cancelNotification(mCallID);
            e.printStackTrace();
        }
        mAudioManager.setSpeakerphoneOn(false);
        //  iv_speaker.setBackgroundResource(R.drawable.speaker);
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
        SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
//        SipServiceCommand.hangUpActiveCalls(this, mAccountID);
        mCallStateCode = pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED;

        //     stopring();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sipEvents.unregister(this);
        unregisterReceiver(networkReceiver);
        unregisterReceiver(callstateupdateReceiver);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        WindowManager wm;
        Display display;
        int rotation;

        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        rotation = display.getRotation();
        System.out.println("Device orientation changed: " + rotation);

        if (mSipAccount!=null && mSipAccount.getIdUri()!=null && !mSipAccount.getIdUri().isEmpty()){
            SipServiceCommand.changeVideoOrientation(VideoActivity.this, mSipAccount.getIdUri(), mCallID, rotation);
        }

        Log.d("sachin", "Screen orientation " + rotation);


        /*String sipAId=share.getValue(SharedPrefrence.SIPACCOUNTID);
        mSipA = SipService.mActiveSipAccounts.get(sipAId);
        mSipCall = getCall(sipAId, mCallID);

        if (mSipA.getService().getmEndpoint()!=null && mSipA!=null){

            try {
                AccountConfig cfg = mSipAccount.getAccountConfig();
                int cap_dev = cfg.getVideoConfig().getDefaultCaptureDevice();
                mSipA.getService().getmEndpoint().vidDevManager().setCaptureOrient(cap_dev, orient,
                        true);
            } catch (Exception e) {
                System.out.println(e);
            }
        }*/

/*
        if (mSipAccount != null && mSipAccount != null) {
            try {
                AccountConfig cfg = MainActivity.account.cfg;
                int cap_dev = cfg.getVideoConfig().getDefaultCaptureDevice();
                MyApp.ep.vidDevManager().setCaptureOrient(cap_dev, orient,
                        true);
            } catch (Exception e) {
                System.out.println(e);
            }
        }*/
    }

    public void getCallState() {
        if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_EARLY
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING
                || mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            if (mCallStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING) {
//            addNotificationIdAndCallIdInSharedPreference(mAccountID, mCallID, mNumber);
//            cancelNotification(getNotificationIdFromSP());
                cancelNotification(mCallID);
               // setNotificationIdInSP();
//            showNotificationForCall(mAccountID, getNotificationIdFromSP(), mNumber);
                //showNotificationForCall(mAccountID, mCallID, mNumber);
                //clearAlNotification();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    setNotificationIdInSP();
                    showNotificationForCall(mAccountID, mCallID, mNumber);
                }
            }
        }
    }

    public void setNotificationIdInSP() {
        int code = getRandomCode();
        sharedPrefrence.setIntValue(AppConstants.notificationIdForCall, code);
    }

    private static int getRandomCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900);
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

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(
                VideoActivity.this)
                .setContentTitle("Shield Crypt Call in Progress")
                .setSmallIcon(R.drawable.ic_baseline_call_24)
                .setContentText("Call");
      //  mBuilder.setColor(this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
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

        resultIntent = new Intent(VideoActivity.this, VideoActivity.class);

        resultIntent.putExtra(PARAM_ACCOUNT_ID, acId);
        resultIntent.putExtra(PARAM_CALL_ID, callid);
        resultIntent.putExtra(PARAM_NUMBER, mNumber);
        resultIntent.putExtra(PARAM_CALL_STATUS, "activie");
        resultIntent.putExtra(PARAM_CALL_TYPE, mCallType);
        resultIntent.putExtra("activeCallClickedWay", "activated");

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(VideoActivity.this);
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
}
