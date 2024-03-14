package com.advantal.shieldcrypt.xmpp_pkg;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.my_database_pkg.db_table.GroupDataModel;
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.CustomExtensionModel;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.EntityBareJid;

import java.util.Random;

import database.my_database_pkg.db_table.MyAppDataBase;


public class XmppIncomingMessages {


    public static XMPPTCPConnection mConnection;
    Context context;
    String isNotificationEnabled;

    public XmppIncomingMessages() {
    }

    public XmppIncomingMessages(Context context) {
        this.context = context;
        Log.e("sachin", "msg");

        enableIncomingMessageListner();
    }

    public void enableIncomingMessageListner() {
        if (XMPPConnectionListener.Companion.getMConnection() == null)
            return;

        // Called when new message came from other user
        ChatManager.getInstanceFor(XMPPConnectionListener.
                Companion.getMConnection()).addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid messageFrom, Message message, Chat chat) {
                Log.e("ALPHA1", "advnatal msg------" + message);
                Log.e("sarvesh", "advnatal" + message.getBody());

                String friendJid = messageFrom.toString().split("@")[0];
                String ownerJid = MySharedPreferences.getSharedprefInstance().getLoginData().getMobileNumber();
//                isNotificationEnabled = mySharedPreferences.getValue(AppConstants.isEnableNotification);
                MyMessageModel chatModel = new MyMessageModel();


//                int messageType = Integer.parseInt(message.getSubject());
                sendCustomPacket(message);
                if (MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getIsBlockedUser(friendJid)) {
                    return;
                }
                int messageCount = 0;//MyAppDataBase.Companion.getUserDataBaseAppinstance(context).messageDao().checkMessageExist(message.getStanzaId());
                if (messageCount > 0) {
                    return;
                } else {
                    if ((message.toXML("").toString().contains("<delay xmlns='urn:xmpp:delay'"))) {
                        try {
//                            String ss = message.toXML("urn:xmpp:delay");
                            String s = "";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    chatModel = new Gson().fromJson(message.getBody(), MyMessageModel.class);
                }


                boolean isUserChatOpen = false;

                String fromUser = AppUtills.Companion.getUserFromJid(message.getFrom().toString());


                if (MySharedPreferences.getSharedprefInstance().getChatWindowOpen()
                        && ChatsActivity.Companion.getFriendJid().equalsIgnoreCase(fromUser)) {
                    isUserChatOpen = true;
                } else if (MySharedPreferences.getSharedprefInstance().getChatWindowOpen()
                        && (ChatsActivity.Companion.getFriendJid().equalsIgnoreCase(message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1]))) {
                    isUserChatOpen = true;
                }

                if (chatModel.getThreadBareJid().equals
                        (MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber)){

                }
//                String oweId = chatModel.getThreadBareJid();
//                String friId = chatModel.getOwnerBareJid();
//                chatModel.setOwnerBareJid(oweId);
//                chatModel.setThreadBareJid(friId);


                if (isUserChatOpen)
                    AppUtills.Companion.insertChatToDb(chatModel, 0,true);
                else
                    AppUtills.Companion.insertChatToDb(chatModel, 1,true);

//                if (isNotificationEnabled.equalsIgnoreCase(AppConstants.trueValue)) {
                if (!isUserChatOpen)
                    showNotification(message);
//                }

                String from = message.getFrom().toString();

                String contactJid = "";
                if (from.contains("/")) {
                    contactJid = from.split("/")[0];
                } else {
                    contactJid = from;
                }

                //Bundle up the intent and send the broadcast. refund.bombay.hospital.indore 2004993
                Intent intent = new Intent(RoosterConnectionService.NEW_MESSAGE);
                intent.setPackage(context.getPackageName());
                intent.putExtra(RoosterConnectionService.BUNDLE_FROM_JID, contactJid);
                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_BODY, message.getBody());
                intent.putExtra(RoosterConnectionService.BUNDLE_MESSAGE_ID, message.getStanzaId());
                intent.putExtra("chatmodel", chatModel);
                context.sendBroadcast(intent);
            }
        });


    }

    public void sendCustomPacket(Message messageObj) {
        Message message = new Message();
        message.setType(Message.Type.chat);
        message.setStanzaId(messageObj.getStanzaId());
        message.setFrom(messageObj.getFrom());
        message.setTo(messageObj.getTo());
        CustomExtensionModel customExtensionModel = new CustomExtensionModel();
        customExtensionModel.setText(messageObj.getStanzaId());
        customExtensionModel.setElement("client_receipt");
        message.addExtension(customExtensionModel);
        try {
            XMPPConnectionListener.Companion.getMConnection().sendStanza(message);
        } catch (SmackException.NotConnectedException e) {
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showNotification(Message message) {
        Log.e("ALPHA6", "advnatal");

        String messageBody = "";
        String fromUser = AppUtills.Companion.getUserFromJid(message.getFrom().toString());
        int messageSubject = Integer.parseInt(message.getSubject());
        switch (messageSubject) {
            case AppConstants.CODE_TEXT:
                messageBody = new Gson().fromJson(message.getBody(), MyMessageModel.class).getContent();

                Log.e("body", "advnatal" + message.getBody());
                break;
            case AppConstants.CODE_IMAGE:
                messageBody = "Image";
                break;
            case AppConstants.CODE_VIDEO:
                messageBody = "Video";
                break;
            case AppConstants.CODE_AUDIO:
                messageBody = "Audio";
                break;
            case AppConstants.CODE_DOCUMENT:
                messageBody = "Document";
                break;
            case AppConstants.CODE_LOCATION:
                messageBody = "Location";
                break;
            case AppConstants.CODE_CONTACT:
                messageBody = "Contact";
                break;
            case AppConstants.CODE_STICKER:
                messageBody = "Sticker";
                break;
            case AppConstants.GROUP_CODE_TEXT:
                messageBody = message.getBody();
                break;
            case AppConstants.GROUP_CODE_IMAGE:
                messageBody = "Image";
                break;
            case AppConstants.GROUP_CODE_VIDEO:
                messageBody = "Video";
                break;
            case AppConstants.GROUP_CODE_AUDIO:
                messageBody = "Audio";
                break;
            case AppConstants.GROUP_CODE_DOCUMENT:
                messageBody = "Document";
                break;
            case AppConstants.GROUP_CODE_LOCATION:
                messageBody = "Location";
                break;
            case AppConstants.GROUP_CODE_CONTACT:
                messageBody = "Contact";
                break;
            case AppConstants.GROUP_CODE_STICKER:
                messageBody = "Sticker";
                break;
            case AppConstants.GROUP_CODE_CREATE_GROUP:
                messageBody = "New Group Created";
                break;
        }

        String username = MyAppDataBase.Companion.getUserDataBaseAppinstance(context).contactDao().getContactNameById(fromUser).getContactName(); //
        if (username == null) {
            username = fromUser;
        }
        Log.e("ALPHA7", "advnatal");

        Intent in = new Intent(context, ChatsActivity.class);
        in.putExtra("chatUser", message.getBody());
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);

//        PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, in, PendingIntent.FLAG_IMMUTABLE);
        }

        int notificationId = getRandomCode();
        Notification.Builder mBuilder;
        String CHANNEL_ID = "channel1";
        CharSequence name = "Channel";
        NotificationChannel mChannel = null;
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("ALPHA8", "advnatal");

            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableVibration(true);
            mChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                            .build());
            // Builder class for devices targeting API 26+ requires a channel ID
            mBuilder = new Notification.Builder(context, "myChannelId")
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setContentTitle("ShieldCrypt")
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_color_icon))
                    .setContentIntent(pendingIntent)
                    .setShowWhen(true)
                    .setContentText(username + ": " + messageBody);
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            // this Builder class is deprecated
            Log.e("ALPHA9", "advnatal");

            mBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setContentTitle("ShieldCrypt")
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.notification_color_icon))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setSound(defaultSoundUri)
                    .setContentText(username + ": " + messageBody);
        }
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private int getRandomCode() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(900000);
    }

    //     Set the data to chat object when new message received from xmpp server
    public MyMessageModel getChatModelForIncomingMessage(int messageType, Message message, String ownerJid, String friendJid) {
        MyMessageModel chatModel = new MyMessageModel();
        switch (messageType) {
            case AppConstants.CODE_TEXT:
//                chatModel = AppUtills.Companion.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getBody(),
//                        AppConstants.CODE_TEXT, false, AppConstants.INDIVIDUAL, false, null,
//                        System.currentTimeMillis(), 0, null, 0, null,
//                        null, 0, false, null, null,
//                        null, 0, 0, null,
//                        0, 0);
                break;
         /*   case AppConstants.CODE_IMAGE:
                String imageThumb = "";
                String imageUrl = "";
                String imageBody = message.getBody();
                String[] imageData = imageBody.split(AppConstants.SEPERATOR_GROUP_JID);
                if (imageData != null && imageData.length > 0) {
                    imageUrl = imageData[0];
                    imageThumb = imageData[1];
                }
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, imageUrl,
                        AppConstants.CODE_IMAGE, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, Utils.converyBase64ToByteArray(imageThumb),
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;
            case AppConstants.CODE_VIDEO:
                String videoThumb = "";
                String videoUrl = "";
                String videoBody = message.getBody();
                String[] videoData = videoBody.split(AppConstants.SEPERATOR_GROUP_JID);
                if (videoData != null && videoData.length > 0) {
                    videoUrl = videoData[0];
                    videoThumb = videoData[1];
                }
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, videoUrl,
                        AppConstants.CODE_VIDEO, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, Utils.converyBase64ToByteArray(videoThumb),
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;
            case AppConstants.CODE_DOCUMENT:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getThread(),
                        AppConstants.CODE_DOCUMENT, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, null,
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;
            case AppConstants.CODE_CONTACT:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getBody(),
                        AppConstants.CODE_CONTACT, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;
            case AppConstants.CODE_AUDIO:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getBody(),
                        AppConstants.CODE_AUDIO, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, null,
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;
            case AppConstants.CODE_LOCATION:
//                String latLong = message.getThread().split("#:#")[1];

                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0],
                        AppConstants.CODE_LOCATION, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
//                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getThread(),
//                        AppConstants.CODE_LOCATION, false, AppConstants.INDIVIDUAL, false, null,
//                        System.currentTimeMillis(), 0, null, 0, Utils.converyBase64ToByteArray(message.getThread()),
//                        null, 0, false, null, null,
//                        null, 0, 0, null,
//                        0, 0);
                break;
            case AppConstants.CODE_STICKER:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, friendJid, null, message.getBody(),
                        AppConstants.CODE_IMAGE, false, AppConstants.INDIVIDUAL, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null,
                        0, 0);
                break;

            // Group Chat

            case AppConstants.GROUP_CODE_TEXT:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_TEXT, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_IMAGE:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0], AppConstants.GROUP_CODE_IMAGE, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, Utils.converyBase64ToByteArray(message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[1]),
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_VIDEO:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0], AppConstants.GROUP_CODE_VIDEO, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, Utils.converyBase64ToByteArray(message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[1]),
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_DOCUMENT:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_DOCUMENT, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_CONTACT:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_CONTACT, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_AUDIO:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_AUDIO, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, AppConstants.filedownloadfailed, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_LOCATION:
//                String latLong = message.getThread().split("#:#")[1];

                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0], AppConstants.GROUP_CODE_LOCATION, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
//
//                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
//                        message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0], AppConstants.GROUP_CODE_LOCATION, false, AppConstants.GROUP, false, null,
//                        System.currentTimeMillis(), 0, null, 0, Utils.converyBase64ToByteArray(message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[1]),
//                        null, 0, false, null, null,
//                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_STICKER:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[0], friendJid,
                        message.getThread(), AppConstants.GROUP_CODE_STICKER, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, Utils.converyBase64ToByteArray(message.getBody()),
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_CREATE_GROUP:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_CREATE_GROUP, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                addGroup(message, chatModel);
                break;
            case AppConstants.GROUP_CODE_ADD_MEMBER:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_ADD_MEMBER, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_REMOVE_MEMBER:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_REMOVE_MEMBER, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_EXIT_GROUP:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_EXIT_GROUP, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, Utils.converyBase64ToByteArray(message.getBody()),
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;
            case AppConstants.GROUP_CODE_CHANGE_GROUP_NAME:
                chatModel = Utils.getChatModelObjectFromChatType(message.getStanzaId(), ownerJid, message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1], friendJid,
                        message.getBody(), AppConstants.GROUP_CODE_CHANGE_GROUP_NAME, false, AppConstants.GROUP, false, null,
                        System.currentTimeMillis(), 0, null, 0, null,
                        null, 0, false, null, null,
                        null, 0, 0, null, 0, 0);
                break;*/
        }
        return chatModel;
    }

    public void addGroup(Message message, MyMessageModel chatModel) {
        String groupJid = message.getThread().split(AppConstants.SEPERATOR_GROUP_JID)[1];
        GroupDataModel groupModel = new GroupDataModel();

        // Insert new group in table
//        groupModel = AppUtills.Companion.getGroupModel(message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[1], "",
//                groupJid, "", message.getBody().split(AppConstants.SEPERATOR_GROUP_JID)[0]);
//        appDataBase.groupDao().insertGroup(groupModel);
//        checkValidationToGetGroupDetail(groupJid);
    }

//    private void checkValidationToGetGroupDetail(String groupJid) {
//        if (Utils.checkInternetConn(context)) {
//            createJsonToGetGroupDetail(groupJid);
//        }
//    }

    private void createJsonToGetGroupDetail(String groupJid) {
        JSONObject jsonObject = new JSONObject();
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            jsonObject.put("roomName", groupJid);
            gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());
//            getGroupDetailApi(gsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void getGroupDetailApi(JsonObject body) {
//        apiservice = ApiClient.getClient().create(ApiInterface.class);
//        Call<GroupGetGroupDetailResponseBean> call = apiservice.getGroupDetailApi(Utils.getBasicAuthenticationString(), body);
//        call.enqueue(new Callback<GroupGetGroupDetailResponseBean>() {
//            @Override
//            public void onResponse(Call<GroupGetGroupDetailResponseBean> call, Response<GroupGetGroupDetailResponseBean> response) {
//
//                try {
//                    String responseMessage = response.body().getMessage();
//                    if (response.body().getStatus().equalsIgnoreCase(AppConstants.successResponse)) {
//                        String groupJid = response.body().getGroupDetailObjectBean().getRoomName();
//                        GetGroupAdminObjectBean getGroupAdminObjectBean = new GetGroupAdminObjectBean();
//                        getGroupAdminObjectBean = response.body().getGroupDetailObjectBean().getGetGroupAdminObjectBean();
//                        String groupAdminUsername = "";
//                        if (getGroupAdminObjectBean != null) {
//                            groupAdminUsername = getGroupAdminObjectBean.getUsername();
//                        }
//                        List<IthubContactsObjectBean> al_participants = new ArrayList<>();
//                        al_participants = response.body().getGroupDetailObjectBean().getParticipants();
//                        int groupId = 0;
//                        groupId = appDataBase.groupDao().getGroupId(groupJid);
//                        for (int i = 0; i < al_participants.size(); i++) {
//                            GroupParticipantModel groupParticipantModel = new GroupParticipantModel();
//                            if (groupAdminUsername.equalsIgnoreCase(al_participants.get(i).getUsername())) {
//                                groupParticipantModel = Utils.getGroupParticipantsModel(groupId, al_participants.get(i).getUsername(), 1);
//                            } else {
//                                groupParticipantModel = Utils.getGroupParticipantsModel(groupId, al_participants.get(i).getUsername(), 0);
//                            }
//                            appDataBase.groupParticipantDao().insertGroupParticipants(groupParticipantModel);
//                        }
//                    } else if (response.body().getStatus().equalsIgnoreCase(AppConstants.failResponse)) {
//
//                    }
//                } catch (Exception e) {
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GroupGetGroupDetailResponseBean> call, Throwable t) {
//
//            }
//        });
//    }
}
