package net.gotev.sipservice;

import static net.gotev.sipservice.ObfuscationHelper.getValue;
import static net.gotev.sipservice.SipServiceConstants.PARAM_ACCOUNT_ID;
import static net.gotev.sipservice.SipServiceConstants.PARAM_CALL_ID;
import static net.gotev.sipservice.SipServiceConstants.PARAM_CALL_STATUS;
import static net.gotev.sipservice.SipServiceConstants.PARAM_CALL_TYPE;
import static net.gotev.sipservice.SipServiceConstants.PARAM_IS_FROM_WAY_FOR_VERSION_S;
import static net.gotev.sipservice.SipServiceConstants.PARAM_NUMBER;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.broadcast.ActionReceiver;
import com.advantal.shieldcrypt.broadcast.HeadsUpNotificationActionReceiver;
import com.advantal.shieldcrypt.sip.CallActivity;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.VideoActivity;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import database.my_database_pkg.db_table.MyAppDataBase;

/**
 * Wrapper around PJSUA2 Account object.
 * @author gotev (Aleksandar Gotev)
 */
public class SipAccount extends Account {

    private static final String LOG_TAG = SipAccount.class.getSimpleName();

    public static final HashMap<Integer, SipCall> activeCalls = new HashMap<>();
    private final SipAccountData data;
    private final SipService service;

    //by sv///////////////////////////////////////////
    public static boolean sendbusyhere = false;
    SharedPrefrence share;
    SharedPrefrence mySharedPreferences;
    //////////////////////////////////////////////////////////

    protected SipAccount(SipService service, SipAccountData data) {
        super();
        this.service = service;
        this.data = data;
        share = SharedPrefrence.getInstance(service);
        mySharedPreferences = SharedPrefrence.getInstance(service);
    }

    public SipService getService() {
        return service;
    }

    public SipAccountData getData() {
        return data;
    }

    public void create() throws Exception {
        create(data.getAccountConfig());
    }

    public void createGuest() throws Exception {
        create(data.getGuestAccountConfig());
    }

    protected void removeCall(int callId) {
        CallActivity.confirm = false;
        SipCall call = activeCalls.get(callId);

        if (call != null) {
            Logger.debug(LOG_TAG, "Removing call with ID: " + callId);
            activeCalls.remove(callId);
            System.out.println("call id update  removed " + callId);
        }
    }

    //by SV
    public void removeCalls() {
        activeCalls.clear();
    }

    public static HashMap<Integer, SipCall> getActiveCalls() {
        return activeCalls;
    }

    public SipCall getCall(int callId) {
        return activeCalls.get(callId);
    }

    public Set<Integer> getCallIDs() {
        return activeCalls.keySet();
    }

    public SipCall addIncomingCall(int callId) {

        SipCall call = new SipCall(this, callId);
        activeCalls.put(callId, call);
        Logger.debug(LOG_TAG, "Added incoming call with ID " + callId
                + " to " + getValue(service.getApplicationContext(), data.getIdUri())
        );
        return call;
    }

    public SipCall addOutgoingCall(final String numberToDial, boolean isVideo, boolean isVideoConference, boolean isTransfer) {

        // check if there's already an ongoing call
        int totalCalls = 0;
        for (SipAccount _sipAccount: SipService.getActiveSipAccounts().values()) {
            totalCalls += _sipAccount.getCallIDs().size();
        }

        // allow calls only if there are no other ongoing calls
    //    if (totalCalls <= (isTransfer ? 1 : 0)) {
            SipCall call = new SipCall(this);
            call.setVideoParams(isVideo, isVideoConference);

            CallOpParam callOpParam = new CallOpParam();
            try {
                if (numberToDial.startsWith("sip:")) {
                    call.makeCall(numberToDial, callOpParam);
                } else {
                    if ("*".equals(data.getRealm())) {
                       // call.makeCall("sip:" + numberToDial, callOpParam);
                        call.makeCall("sip:" + numberToDial + "@" + mySharedPreferences.getValue(AppConstants.sipIpDynamic), callOpParam);
                    } else {
                        call.makeCall("sip:" + numberToDial + "@" + data.getRealm(), callOpParam);
                    }
                }
                activeCalls.put(call.getId(), call);
                Logger.debug(LOG_TAG, "New outgoing call with ID: " + call.getId());

                return call;

            } catch (Exception exc) {
                Logger.error(LOG_TAG, "Error while making outgoing call", exc);
                return null;
            }
      //  }
      //  return null;
    }

    public SipCall addOutgoingCall(final String numberToDial) {
        return addOutgoingCall(numberToDial, false, false, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SipAccount that = (SipAccount) o;

        return data.equals(that.data);

    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        service.getBroadcastEmitter().registrationState(data.getIdUri(), prm.getCode());
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        Logger.debug(LOG_TAG, "incoming call in background");
        Log.e("sachin", "bak incoming");

        SipCall call = addIncomingCall(prm.getCallId());

        for (Map.Entry<Integer, SipCall> entry : activeCalls.entrySet())
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().getCurrentState());

        // Send 603 Decline if in DND mode
        if (service.isDND()) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.declineIncomingCall();
                Logger.debug(LOG_TAG, "Decline call with ID: " + prm.getCallId());
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }

        // Send 486 Busy Here if there's an already ongoing call
        int totalCalls = 0;
        for (SipAccount _sipAccount : SipService.getActiveSipAccounts().values()) {
            totalCalls += _sipAccount.getCallIDs().size();
        }
//  Commented by SV
      /*  if (totalCalls > 1) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.sendBusyHereToIncomingCall();
                Logger.debug(LOG_TAG, "Sending busy to call ID: " + prm.getCallId() + call.isVideoCall());
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }*/
//Original Commented by SV

       /* try {
            // Answer with 180 Ringing
            CallOpParam callOpParam = new CallOpParam();
            callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            call.answer(callOpParam);
            Logger.debug(LOG_TAG, "Sending 180 ringing");

            String displayName, remoteUri;
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                displayName = contactInfo.getDisplayName();
                remoteUri = contactInfo.getRemoteUri();
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting caller info", ex);
                throw ex;
            }

            // check for video in remote SDP
            CallInfo callInfo = call.getInfo();
            boolean isVideo = (callInfo.getRemOfferer() && callInfo.getRemVideoCount() > 0);

            service.getBroadcastEmitter().incomingCall(data.getIdUri(), prm.getCallId(),
                            displayName, remoteUri, isVideo);

        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error while getting caller info", ex);
        }*/


        try {
            AudioManager manager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
            boolean voiceCall = manager.getMode() == AudioManager.STREAM_VOICE_CALL;
            boolean ring = manager.getMode() == AudioManager.STREAM_RING;

            CallInfo callInfo = call.getInfo();
            boolean isVideo = (callInfo.getRemOfferer() && callInfo.getRemVideoCount() > 0);


            if (isCallActive(service)) {
                sendbusyhere = true;
                call.sendBusyHereToIncomingCall();
            } else {
                // Answer with 180 Ringing
                CallOpParam callOpParam = new CallOpParam();
                callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                call.answer(callOpParam);
                Logger.debug(LOG_TAG, "Sending 180 ringing");
                CallerInfo contactInfo = new CallerInfo(call.getInfo());

                service.getBroadcastEmitter().incomingCall(data.getIdUri(), prm.getCallId(), contactInfo.getDisplayName(), contactInfo.getRemoteUri(), isVideo);
                if (activeCalls.size() > 2) {
                    sendbusyhere = true;
                    call.sendBusyHereToIncomingCall();
                } else if (activeCalls.size() > 1) {
                    sendbusyhere = true;
                    //String mAccountID = share.getValue(SharedPrefrence.SIPACCOUNTID);
                    //showNotificationForCall(mAccountID, prm.getCallId(), contactInfo.getDisplayName(), call, prm);
                    call.sendBusyHereToIncomingCall();
                } else {
                    try {

                        /*IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
                        filter.addAction(Intent.ACTION_SCREEN_OFF);
                        filter.addAction(Intent.ACTION_USER_PRESENT);
                        powerButtonReceiver = new PowerButtonReceiver();
                        service.getApplicationContext().registerReceiver(powerButtonReceiver, filter);*/


                        service.startRingtone();
                        String isAppForeground = mySharedPreferences.getValue(AppConstants.isAppForeground);
                        boolean isAppInBackground = checkAppState();
                        setHeadsUpNotificationIdInSP();
                        if (isAppInBackground) {
                            newSendHeadsUpNotification("Incoming Call From", prm, contactInfo, isVideo);
                        } else {
                            if (isVideo) {

                                Intent intentcall = new Intent(service, VideoActivity.class);
                                intentcall.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                                intentcall.putExtra(PARAM_CALL_ID, prm.getCallId());
//                                String s = data.getUsername();
//                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getDisplayName());
                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getRemoteUri());
                                intentcall.putExtra(PARAM_CALL_TYPE, "IN");
                                intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                service.startActivity(intentcall);
                            } else {

                                Intent intentcall = new Intent(service, CallActivity.class);
                                intentcall.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                                intentcall.putExtra(PARAM_CALL_ID, prm.getCallId());
//                                String s = data.getUsername();
//                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getDisplayName());
                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getRemoteUri());
                                intentcall.putExtra(PARAM_CALL_TYPE, "IN");
                                intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                service.startActivity(intentcall);
                            }

                        }
                        //by sonam
//                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                            /*if (isAppForeground.equalsIgnoreCase(AppConstants.trueValue)) {
                                Intent intentcall = new Intent(service, CallActivity.class);
                                intentcall.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                                intentcall.putExtra(PARAM_CALL_ID, prm.getCallId());
//                                String s = data.getUsername();
//                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getDisplayName());
                                intentcall.putExtra(PARAM_NUMBER, contactInfo.getRemoteUri());
                                intentcall.putExtra(PARAM_CALL_TYPE, "IN");
                                intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                service.startActivity(intentcall);
                            } else {
                                sendHeadsUpNotification("Incoming Call From", prm, contactInfo);
                            }*/
                        /*}else{
                            Intent intentcall = new Intent(service, CallActivity.class);
                            intentcall.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                            intentcall.putExtra(PARAM_CALL_ID, prm.getCallId());
                            intentcall.putExtra(PARAM_NUMBER, contactInfo.getDisplayName());
                            intentcall.putExtra(PARAM_CALL_TYPE, "IN");
                            intentcall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                            service.startActivity(intentcall);
                        }*/
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error while getting caller info", ex);
//            Crashlytics.logException(ex);
        }
    }

    //Custom method by SV

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotificationForCall(String acId, int callid, String mNumber, SipCall sipCall, OnIncomingCallParam prm) {
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "My Channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build());
        }

        Intent intentAction = new Intent(service, ActionReceiver.class);
        int callId = prm.getCallId();
        int oldCallId = 0;
        for (HashMap.Entry<Integer, SipCall> entry : activeCalls.entrySet()) {
            if (entry.getKey() != callId) {
                oldCallId = entry.getKey();
            }
        }

//This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action", "hangup");
        intentAction.putExtra("callid", callid);
        intentAction.putExtra("oldcallid", oldCallId);
        intentAction.putExtra("number", mNumber);

       // PendingIntent snoozePendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), intentAction, 0);
        PendingIntent snoozePendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            snoozePendingIntent = PendingIntent.getBroadcast(service,
                    getRandomCode(), intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            snoozePendingIntent = PendingIntent.getBroadcast(service,
                    getRandomCode(), intentAction, 0);
        }
        ////////////// Accept Call Pending Intent //////////////
        Intent acceptCallIntentAction = new Intent(service, ActionReceiver.class);
        acceptCallIntentAction.putExtra("action", "Accept");
        acceptCallIntentAction.putExtra("callid", callid);
        acceptCallIntentAction.putExtra("oldcallid", oldCallId);
        acceptCallIntentAction.putExtra("number", mNumber);
       // PendingIntent acceptCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), acceptCallIntentAction, 0);
        PendingIntent acceptCallPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                    getRandomCode(), acceptCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                    getRandomCode(), acceptCallIntentAction, 0);
        }
        ///////////// Accept Call Pending Intent //////////////
        Notification.Builder mBuilder = new Notification.Builder(service);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder.setContentTitle("Incoming Call")
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setContentText(mNumber);
            mBuilder.setOngoing(true);
            mBuilder.setChannelId(CHANNEL_ID);
            mBuilder.setAutoCancel(false);

            Notification.BigTextStyle bigText = new Notification.BigTextStyle();
            bigText.bigText("Incoming Call");
            bigText.setBigContentTitle("New Call");
            bigText.setSummaryText(mNumber);
            mBuilder.setStyle(bigText);
        } else {
            mBuilder.setContentTitle("Incoming Call")
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setContentText(mNumber);
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
            mBuilder.setOngoing(true);
            mBuilder.setAutoCancel(false);

            Notification.BigTextStyle bigText = new Notification.BigTextStyle();
            bigText.bigText("Incoming Call");
            bigText.setBigContentTitle("New Call");
            bigText.setSummaryText(mNumber);
            mBuilder.setStyle(bigText);
        }
        Intent resultIntent = null;

        resultIntent = new Intent();

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
        stackBuilder.addParentStack(CallActivity.class);
        stackBuilder.addNextIntent(resultIntent);
      //  PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.addAction(0, "Hangup",
                snoozePendingIntent);
        mBuilder.addAction(0, "Accept",
                acceptCallPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(mChannel);
        }
        mNotificationManager.notify(callid, mBuilder.build());
    }

    public static boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return manager.getMode() == AudioManager.MODE_IN_CALL;
    }

    private static int getRandomCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    public boolean checkAppState() {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        boolean isInBackground = myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
        if (isInBackground) {
            System.out.println("Appstate check " + "background");
        } else {
            System.out.println("Appstate check " + "foreground");
        }
        return isInBackground;
    }

    //by arvind
    private void newSendHeadsUpNotification(String message, OnIncomingCallParam prm, CallerInfo contactInfo,
                                            boolean isVideo) {
        try {
            System.out.println("PUsh === send pushnotification " + Utils.getidwithoutip(contactInfo.getRemoteUri()
          + " -> " + prm.getRdata()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent intent;
                if (isVideo){
                    intent = new Intent(service, VideoActivity.class);
                } else {
                    intent = new Intent(service, com.advantal.shieldcrypt.sip.CallActivity.class);
                }
                intent.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                intent.putExtra(PARAM_CALL_ID, prm.getCallId());
                intent.putExtra(PARAM_NUMBER, Utils.getidwithoutip(contactInfo.getRemoteUri()));
                intent.putExtra(PARAM_CALL_TYPE, "IN");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(service,
                            getRandomCode(), intent,
                            PendingIntent.FLAG_IMMUTABLE
                                    | PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    pendingIntent = PendingIntent.getActivity(service,
                            getRandomCode(), intent, 0);
                }
                // Accept Call Pending Intent
                //Intent acceptCallIntentAction = new Intent(service, CallActivity.class);
                Intent acceptCallIntentAction;
                if (isVideo){
                    acceptCallIntentAction = new Intent(service, VideoActivity.class);
                } else {
                    acceptCallIntentAction = new Intent(service, CallActivity.class);
                }
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
                stackBuilder.addParentStack(CallActivity.class);
                acceptCallIntentAction.putExtra(PARAM_IS_FROM_WAY_FOR_VERSION_S, "yes");
                acceptCallIntentAction.putExtra(PARAM_ACCOUNT_ID, mySharedPreferences.getValue(SharedPrefrence.SIPACCOUNTID));
                acceptCallIntentAction.putExtra(PARAM_CALL_ID, prm.getCallId());
                acceptCallIntentAction.putExtra(PARAM_NUMBER, ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));
                acceptCallIntentAction.putExtra(PARAM_CALL_STATUS, "activie");
                acceptCallIntentAction.putExtra(PARAM_CALL_TYPE, "IN");
                acceptCallIntentAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                acceptCallIntentAction.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);

               /* JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", "Accept");
                jsonObject.put("callid",  prm.getCallId());
                jsonObject.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
                acceptCallIntentAction.setAction(new Gson().toJson(jsonObject));*/


                PendingIntent acceptCallPendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                   /* acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), acceptCallIntentAction,
                            PendingIntent.FLAG_IMMUTABLE
                                    | PendingIntent.FLAG_UPDATE_CURRENT);*/
                    stackBuilder.addNextIntentWithParentStack(acceptCallIntentAction);
                    acceptCallPendingIntent = stackBuilder.getPendingIntent(getRandomCode(),
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), acceptCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                Notification.Action action = new Notification.Action.Builder(0, "Answer", acceptCallPendingIntent).build();

                // Decline Call Pending Intent
                Intent declineCallIntentAction = new Intent(service, HeadsUpNotificationActionReceiver.class);
                JSONObject jsonObjectDecline = new JSONObject();
                jsonObjectDecline.put("action", "hangup");
                jsonObjectDecline.put("callid",  prm.getCallId());
                jsonObjectDecline.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
                declineCallIntentAction.setAction(new Gson().toJson(jsonObjectDecline));
                // PendingIntent declineCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), declineCallIntentAction, 0);
                PendingIntent declineCallPendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    declineCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), declineCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    declineCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), declineCallIntentAction, 0);
                }

                Notification.Action declineCallAction = new Notification.Action.Builder(0, "Decline", declineCallPendingIntent).build();


                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = "My Channel";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build());
                }
                Notification.Builder mBuilder = new Notification.Builder(service);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification;

                String contactName = "";
                contactName = getcontactName(Utils.getidwithoutip(contactInfo.getRemoteUri()), service);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = mBuilder
                            .setAutoCancel(true)
                            .setContentTitle("ShieldCrypt")
                            .setContentIntent(pendingIntent)
//                        .setOngoing(true)
                            .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                            .setSmallIcon(R.drawable.notification_icon_grey)
                            .setActions(action, declineCallAction)
                            .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                            .setContentText(message + " " + contactName)
                            .setChannelId(CHANNEL_ID)
                            .setOngoing(true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(pendingIntent, true)
                            .setShowWhen(true)
                            .build();
                } else {
                    notification = mBuilder
                            .setAutoCancel(true)
                            .setContentTitle("ShieldCrypt")
                            .setContentIntent(pendingIntent)
                            .setSound(defaultSoundUri)
                            .addAction(0, "Answer", acceptCallPendingIntent)
                            .addAction(0, "Decline", declineCallPendingIntent)
                            .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.notification_icon_grey)
                            .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                            .setContentText(message + " " + contactName)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setOngoing(true)
                            .setFullScreenIntent(pendingIntent, true)
                            .setShowWhen(true)
                            .build();
                }

                NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(mChannel);
                }
                setHeadsUpNotificationIdInSP();
                notificationManager.notify(getHeadsUpNotificationIdFromSP(), notification);
            } else {
                //            Intent intent = new Intent(Intent.ACTION_MAIN, null);
                Intent intent;
                if (isVideo){
                    intent = new Intent(service, VideoActivity.class);
                } else {
                    intent = new Intent(service, CallActivity.class);
                }
                intent.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                intent.putExtra(PARAM_CALL_ID, prm.getCallId());
                intent.putExtra(PARAM_NUMBER, Utils.getidwithoutip(contactInfo.getRemoteUri()));
                intent.putExtra(PARAM_CALL_STATUS, "activie");
                intent.putExtra(PARAM_CALL_TYPE, "IN");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setClass(service, CallActivity.class);
                // PendingIntent pendingIntent = PendingIntent.getActivity(service, getRandomCode(), intent, 0);
                PendingIntent pendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    pendingIntent = PendingIntent.getActivity(service,
                            getRandomCode(), intent,
                            PendingIntent.FLAG_IMMUTABLE
                                    | PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    pendingIntent = PendingIntent.getActivity(service,
                            getRandomCode(), intent, 0);
                }
                // Accept Call Pending Intent

              // Intent acceptCallIntentAction = new Intent(service, HeadsUpNotificationActionReceiver.class);
                Intent acceptCallIntentAction;
                if (isVideo){
                    acceptCallIntentAction = new Intent(service, VideoActivity.class);
                } else {
                    acceptCallIntentAction = new Intent(service, CallActivity.class);
                }
                acceptCallIntentAction.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
                acceptCallIntentAction.putExtra(PARAM_CALL_ID, prm.getCallId());
                acceptCallIntentAction.putExtra(PARAM_NUMBER, Utils.getidwithoutip(contactInfo.getRemoteUri()));
                acceptCallIntentAction.putExtra(PARAM_CALL_STATUS, "activie");
                acceptCallIntentAction.putExtra(PARAM_CALL_TYPE, "IN");
                acceptCallIntentAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                acceptCallIntentAction.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
            /*acceptCallIntentAction.putExtra("action", "Accept");
            acceptCallIntentAction.putExtra("callid", prm.getCallId());
            acceptCallIntentAction.putExtra("number", contactInfo.getDisplayName());*/
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("action", "Accept");
                jsonObject.put("callid",  prm.getCallId());
                jsonObject.put("isVideo",  isVideo);
                jsonObject.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
                acceptCallIntentAction.setAction(new Gson().toJson(jsonObject));
                //PendingIntent acceptCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), acceptCallIntentAction, 0);
                PendingIntent acceptCallPendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), acceptCallIntentAction,
                            PendingIntent.FLAG_IMMUTABLE
                                    | PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), acceptCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT);
                }
                Notification.Action action = new Notification.Action.Builder(0, "Answer", pendingIntent).build();

                // Decline Call Pending Intent
                Intent declineCallIntentAction = new Intent(service, HeadsUpNotificationActionReceiver.class);
            /*declineCallIntentAction.putExtra("action", "hangup");
            declineCallIntentAction.putExtra("callid", prm.getCallId());
            declineCallIntentAction.putExtra("number", contactInfo.getDisplayName());*/
                JSONObject jsonObjectDecline = new JSONObject();
                jsonObjectDecline.put("action", "hangup");
                jsonObjectDecline.put("callid",  prm.getCallId());
                jsonObjectDecline.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
                declineCallIntentAction.setAction(new Gson().toJson(jsonObjectDecline));
                // PendingIntent declineCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), declineCallIntentAction, 0);
                PendingIntent declineCallPendingIntent;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    declineCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), declineCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                } else {
                    declineCallPendingIntent = PendingIntent.getBroadcast(service,
                            getRandomCode(), declineCallIntentAction, 0);
                }

                Notification.Action declineCallAction = new Notification.Action.Builder(0, "Decline", declineCallPendingIntent).build();


                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = "My Channel";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                    mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build());
                }
                Notification.Builder mBuilder = new Notification.Builder(service);

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification;

                String contactName = "";
                contactName = getcontactName(Utils.getidwithoutip(contactInfo.getRemoteUri()), service);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = mBuilder
                            .setAutoCancel(true)
                            .setContentTitle("ShieldCrypt")
                            .setContentIntent(pendingIntent)
//                        .setOngoing(true)
                            .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                            .setSmallIcon(R.drawable.notification_icon_grey)
                            .setActions(action, declineCallAction)
                            .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                            .setContentText(message + " " + contactName)
                            .setChannelId(CHANNEL_ID)
                            .setOngoing(true)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setFullScreenIntent(pendingIntent, true)
                            .setShowWhen(true)
                            .build();
                } else {
                    notification = mBuilder
                            .setAutoCancel(true)
                            .setContentTitle("ShieldCrypt")
                            .setContentIntent(pendingIntent)
                            .setSound(defaultSoundUri)
                            .addAction(0, "Answer", acceptCallPendingIntent)
                            .addAction(0, "Decline", declineCallPendingIntent)
                            .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setSmallIcon(R.drawable.notification_icon_grey)
                            .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                            .setContentText(message + " " + contactName)
                            .setCategory(NotificationCompat.CATEGORY_CALL)
                            .setOngoing(true)
                            .setFullScreenIntent(pendingIntent, true)
                            .setShowWhen(true)
                            .build();
                }

                NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.createNotificationChannel(mChannel);
                }
                setHeadsUpNotificationIdInSP();
                notificationManager.notify(getHeadsUpNotificationIdFromSP(), notification);
            }
        } catch (Exception d) {
            d.printStackTrace();
        }
    }

    private void sendHeadsUpNotification(String message, OnIncomingCallParam prm, CallerInfo contactInfo) {
        try {
            System.out.println("PUsh === send pushnotification " + Utils.getidwithoutip(contactInfo.getRemoteUri()));

//            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            Intent intent = new Intent(service, CallActivity.class);
            intent.putExtra(PARAM_ACCOUNT_ID, data.getIdUri());
            intent.putExtra(PARAM_CALL_ID, prm.getCallId());
            intent.putExtra(PARAM_NUMBER, Utils.getidwithoutip(contactInfo.getRemoteUri()));
            intent.putExtra(PARAM_CALL_TYPE, "IN");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setClass(service, CallActivity.class);
           // PendingIntent pendingIntent = PendingIntent.getActivity(service, getRandomCode(), intent, 0);
            PendingIntent pendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(service,
                        getRandomCode(), intent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(service,
                        getRandomCode(), intent, 0);
            }
            // Accept Call Pending Intent

            Intent acceptCallIntentAction = new Intent(service, HeadsUpNotificationActionReceiver.class);
            /*acceptCallIntentAction.putExtra("action", "Accept");
            acceptCallIntentAction.putExtra("callid", prm.getCallId());
            acceptCallIntentAction.putExtra("number", contactInfo.getDisplayName());*/
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", "Accept");
            jsonObject.put("callid",  prm.getCallId());
            jsonObject.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
            acceptCallIntentAction.setAction(new Gson().toJson(jsonObject));
            //PendingIntent acceptCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), acceptCallIntentAction, 0);
            PendingIntent acceptCallPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                        getRandomCode(), acceptCallIntentAction,
                        PendingIntent.FLAG_IMMUTABLE
                                | PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                acceptCallPendingIntent = PendingIntent.getBroadcast(service,
                        getRandomCode(), acceptCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            Notification.Action action = new Notification.Action.Builder(0, "Answer", acceptCallPendingIntent).build();

            // Decline Call Pending Intent
            Intent declineCallIntentAction = new Intent(service, HeadsUpNotificationActionReceiver.class);
            /*declineCallIntentAction.putExtra("action", "hangup");
            declineCallIntentAction.putExtra("callid", prm.getCallId());
            declineCallIntentAction.putExtra("number", contactInfo.getDisplayName());*/
            JSONObject jsonObjectDecline = new JSONObject();
            jsonObjectDecline.put("action", "hangup");
            jsonObjectDecline.put("callid",  prm.getCallId());
            jsonObjectDecline.put("number", ""+Utils.getidwithoutip(contactInfo.getRemoteUri()));//contactInfo.getDisplayName());
            declineCallIntentAction.setAction(new Gson().toJson(jsonObjectDecline));
           // PendingIntent declineCallPendingIntent = PendingIntent.getBroadcast(service, getRandomCode(), declineCallIntentAction, 0);
            PendingIntent declineCallPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                declineCallPendingIntent = PendingIntent.getBroadcast(service,
                        getRandomCode(), declineCallIntentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                declineCallPendingIntent = PendingIntent.getBroadcast(service,
                        getRandomCode(), declineCallIntentAction, 0);
            }

            Notification.Action declineCallAction = new Notification.Action.Builder(0, "Decline", declineCallPendingIntent).build();


            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = "My Channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                        .build());
            }
            Notification.Builder mBuilder = new Notification.Builder(service);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Notification notification;

            String contactName = "";
            contactName = getcontactName(Utils.getidwithoutip(contactInfo.getRemoteUri()), service);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = mBuilder
                        .setAutoCancel(true)
                        .setContentTitle("ShieldCrypt")
                        .setContentIntent(pendingIntent)
//                        .setOngoing(true)
                        .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                        .setSmallIcon(R.drawable.notification_icon_grey)
                        .setActions(action, declineCallAction)
                        .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                        .setContentText(message + " " + contactName)
                        .setChannelId(CHANNEL_ID)
                        .setOngoing(true)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setFullScreenIntent(pendingIntent, true)
                        .setShowWhen(true)
                        .build();
            } else {
                notification = mBuilder
                        .setAutoCancel(true)
                        .setContentTitle("ShieldCrypt")
                        .setContentIntent(pendingIntent)
                        .setSound(defaultSoundUri)
                        .addAction(0, "Answer", acceptCallPendingIntent)
                        .addAction(0, "Decline", declineCallPendingIntent)
                        .setStyle(new Notification.BigTextStyle().bigText(message + " " + contactName))
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.notification_icon_grey)
                        .setLargeIcon(BitmapFactory.decodeResource(service.getResources(), R.drawable.notification_color_icon))
                        .setContentText(message + " " + contactName)
                        .setCategory(NotificationCompat.CATEGORY_CALL)
                        .setOngoing(true)
                        .setFullScreenIntent(pendingIntent, true)
                        .setShowWhen(true)
                        .build();
            }

            NotificationManager notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(mChannel);
            }
            setHeadsUpNotificationIdInSP();
            notificationManager.notify(getHeadsUpNotificationIdFromSP(), notification);
        } catch (Exception d) {
            d.printStackTrace();
        }

    }

    public String getcontactName(String number, SipService sipService) {
        String contactName = "";
        try {
            MyAppDataBase appDataBase = MyAppDataBase.Companion.getUserDataBaseAppinstance(sipService);

            ContactDataModel ithubContactsObjectBean = appDataBase.contactDao().getContactNameById(number);
            // number is the ithub contact
            if (ithubContactsObjectBean != null) {
              //  contactName = ithubContactsObjectBean.getFirstName() + " " + ithubContactsObjectBean.getLastName();
                contactName = ithubContactsObjectBean.getContactName();// + " " + ithubContactsObjectBean.getLastName();
            } else {
                contactName = Utils.getContactName(number, sipService);
            }
        } catch (Exception e) {
            contactName = number;
            e.printStackTrace();
        }
        return contactName;
    }

    public int getHeadsUpNotificationIdFromSP() {
        int notificationId = mySharedPreferences.getIntValue(AppConstants.headsUpNotificationId);
        return notificationId;
    }

    public void setHeadsUpNotificationIdInSP() {
        int code = getRandomCode();
        mySharedPreferences.setIntValue(AppConstants.headsUpNotificationId, code);
    }

    /*@Override
    public void onIncomingCall(OnIncomingCallParam prm) {

        SipCall call = addIncomingCall(prm.getCallId());

        // Send 603 Decline if in DND mode
        if (service.isDND()) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.declineIncomingCall();
                Logger.debug(LOG_TAG, "Decline call with ID: " + prm.getCallId());
            } catch(Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }

        // Send 486 Busy Here if there's an already ongoing call
        int totalCalls = 0;
        for (SipAccount _sipAccount: SipService.getActiveSipAccounts().values()) {
            totalCalls += _sipAccount.getCallIDs().size();
        }

        if (totalCalls > 1) {
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                service.getBroadcastEmitter().missedCall(contactInfo.getDisplayName(), contactInfo.getRemoteUri());
                call.sendBusyHereToIncomingCall();
                Logger.debug(LOG_TAG, "Sending busy to call ID: " + prm.getCallId());
            } catch(Exception ex) {
                Logger.error(LOG_TAG, "Error while getting missed call info", ex);
            }
            return;
        }

        try {
            // Answer with 180 Ringing
            CallOpParam callOpParam = new CallOpParam();
            callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            call.answer(callOpParam);
            Logger.debug(LOG_TAG, "Sending 180 ringing");

            String displayName, remoteUri;
            try {
                CallerInfo contactInfo = new CallerInfo(call.getInfo());
                displayName = contactInfo.getDisplayName();
                remoteUri = contactInfo.getRemoteUri();
            } catch (Exception ex) {
                Logger.error(LOG_TAG, "Error while getting caller info", ex);
                throw ex;
            }

            // check for video in remote SDP
            CallInfo callInfo = call.getInfo();
            boolean isVideo = (callInfo.getRemOfferer() && callInfo.getRemVideoCount() > 0);

            service.getBroadcastEmitter().incomingCall(data.getIdUri(), prm.getCallId(),
                            displayName, remoteUri, isVideo);

        } catch (Exception ex) {
            Logger.error(LOG_TAG, "Error while getting caller info", ex);
        }
    }*/
}
