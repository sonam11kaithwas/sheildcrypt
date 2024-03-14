package com.advantal.shieldcrypt.xmpp_pkg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.google.gson.Gson;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XmppSendMessage {
    static String oldMessageId = "";
    Context context;
    boolean isOfflineMessageSending = false;
    private BroadcastReceiver uiThreadMessageReceiver;

    public XmppSendMessage() {
    }

    public XmppSendMessage(Context context) {
        this.context = context;
        setupUiThreadBroadCastMessageReceiver();
        if (!isOfflineMessageSending) {
            sendOfflineMessages();
        }
    }


    //    // Receive the broadcast when user send the message from the chatwindow
    private void setupUiThreadBroadCastMessageReceiver() {
        uiThreadMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check if the Intents purpose is to send the message.
                String action = intent.getAction();
                if (action.equals(RoosterConnectionService.SEND_MESSAGE)) {
                    //Send the message.
                    MyMessageModel chatModel = (MyMessageModel) intent.getSerializableExtra("chatModel");
                    String messageId = chatModel.getMessageIdNew();
                    if (!oldMessageId.equalsIgnoreCase(messageId)) {
                        oldMessageId = messageId;
                        sendMessage(chatModel);
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(RoosterConnectionService.SEND_MESSAGE);
        LocalBroadcastManager.getInstance(context).registerReceiver(uiThreadMessageReceiver, filter);
    }

    //    // send offline messages which were not sent because of no internet or xmpp connection issue
    private void sendOfflineMessages() {
        isOfflineMessageSending = true;
        List<MyMessageModel> al_chat = new ArrayList<>();
//        al_chat = MyAppDataBase.Companion.getUserDataBaseAppinstance(context).messageDao().getAllOfflineMessages(AppConstants.xepMessageNotSent);
        for (int i = 0; i < al_chat.size(); i++) {
            if (AppUtills.Companion.isXmppWorkScheduled(context)
//                    && (MyApp.Companion.getAppInstance().getNetWokAvailabe()
//            )
            ) {
                int messageType = 0;
                int uploadingStatus = 0;
                MyMessageModel chatModel = al_chat.get(i);
                messageType = chatModel.getContentType();
                uploadingStatus = chatModel.getFileTransferStatus();
                if (messageType == AppConstants.CODE_TEXT || messageType == AppConstants.CODE_CONTACT
                        || messageType == AppConstants.CODE_LOCATION) {
                    sendMessage(chatModel);
//                    MyAppDataBase.Companion.getUserDataBaseAppinstance(context).messageDao()
//                            .updateXepMessageStatus(chatModel.getMessageIdNew(), AppConstants.xepMessageSent);
                } else {
                    if (uploadingStatus == AppConstants.uploaded) {
                        sendMessage(chatModel);
//                        MyAppDataBase.Companion.getUserDataBaseAppinstance(context).messageDao().updateXepMessageStatus(chatModel.getMessageIdNew(), AppConstants.xepMessageSent);
                    }
                }
            } else {
                isOfflineMessageSending = false;
                return;
            }
        }
        isOfflineMessageSending = false;
    }


    public void sendMessage(MyMessageModel chatModel) {
        int messageType = chatModel.getContentType();
        int isGroupChat = 1;//chatModel.getT
        EntityBareJid jid = null;
        ChatManager chatManager = ChatManager.getInstanceFor(XMPPConnectionListener.Companion.getMConnection());


        try {
            // if Message is One to One
            if (isGroupChat == AppConstants.INDIVIDUAL)
            {
                jid = JidCreate.entityBareFrom(chatModel.getThreadBareJid() + "@"
                        + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());
            } else if (isGroupChat == AppConstants.GROUP) { // if Message for group
                jid = JidCreate.entityBareFrom(chatModel.getThreadBareJid() + AppConstants.atTheRateBroadcast +
                        MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());
            }
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        Chat chat = chatManager.chatWith(jid);


//        try {
////            ChatStateManager.getInstance(XMPPConnectionListener.Companion.getMConnection()).setCurrentState(ChatState.composing, chat);
//            ChatStateManager.getInstance(XMPPConnectionListener.Companion.getMConnection()).addChatStateListener(new ChatStateListener() {
//                @Override
//                public void stateChanged(Chat chat, ChatState state, Message message) {
//                    Log.e("", "");
////                    MyApp.Companion.getAppInstance().showToastMsg("Sona..............");
//                }
//            });
//        } catch (SmackException.NotConnectedException e) {
//            MyApp.Companion.getAppInstance().showToastMsg(e.getMessage());
//
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            MyApp.Companion.getAppInstance().showToastMsg(e.getMessage());
//
//            e.printStackTrace();
//        }

        try {
            Message message = new Message(jid, new Gson().toJson(chatModel));
            message = getMessageObjectForSendMessage(message, messageType, chatModel);
            message.setStanzaId(chatModel.getMessageIdNew());
            String receiptId = DeliveryReceiptRequest.addTo(message);
            chat.send(message);
            XMPPConnectionListener.Companion.getMConnection().sendStanza(message);   //send message


//            Chat newChat = chatManager.createChat(jid,new MessageListenerImpl());


            if (isGroupChat == AppConstants.INDIVIDUAL) {
//                MyAppDataBase.Companion.getUserDataBaseAppinstance(context).messageDao().
//                        updateXepMessageStatus(chatModel.getMessageIdNew(), AppConstants.xepMessageSent);
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        getRoaster();

    }

    private void setDelRecListener() {
        DeliveryReceiptManager d = DeliveryReceiptManager.getInstanceFor(XMPPConnectionListener.Companion.getMConnection());
        d.addReceiptReceivedListener(new ReceiptReceivedListener() {
            @Override
            public void onReceiptReceived(Jid fromJid, Jid toJid, String receiptId, Stanza receipt) {

            }
        });
    }

    private void getRoaster() {
        XMPPConnectionListener.Companion.getRoster().addRosterListener(new RosterListener() {

            @Override
            public void entriesAdded(Collection<Jid> addresses) {
                Log.e("", "");
            }

            @Override
            public void entriesUpdated(Collection<Jid> addresses) {
                Log.e("", "");
            }

            @Override
            public void entriesDeleted(Collection<Jid> addresses) {
                Log.e("", "");
            }

            @Override
            public void presenceChanged(final Presence presence) {
                Log.e("", "");
                System.out.println("Presence changed: " + presence.getFrom()
                        + " " + presence);
            }


        });
    }


    public Message getMessageObjectForSendMessage(Message message, int messageType, MyMessageModel chatModel) {
        String groupName = "";
        switch (messageType) {
            case AppConstants.CODE_TEXT:
//                message.setBody(chatModel.getContent());
                message.setBody(new Gson().toJson(chatModel));
                message.setSubject(AppConstants.CODE_TEXT + "");
                message.setThread(chatModel.getOwnerBareJid());
                break;
            case AppConstants.CODE_IMAGE:
//                message.setBody(chatModel.getContent() + AppConstants.SEPERATOR_GROUP_JID + Utils.convertBtyeArrayToBase64(chatModel.getThumbOfMedia()));
//                message.setSubject(AppConstants.CODE_IMAGE + "");
//                message.setThread(chatModel.getOwnerBareJid());
                break;
            case AppConstants.CODE_VIDEO:
//                message.setBody(chatModel.getContent() + AppConstants.SEPERATOR_GROUP_JID + Utils.convertBtyeArrayToBase64(chatModel.getThumbOfMedia()));
//                message.setSubject(AppConstants.CODE_VIDEO + "");
//                message.setThread(chatModel.getOwnerBareJid());
                break;
            case AppConstants.CODE_DOCUMENT:
                message.setBody("");
                message.setSubject(AppConstants.CODE_DOCUMENT + "");
                message.setThread(chatModel.getContent());
                break;
            case AppConstants.CODE_CONTACT:
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.CODE_CONTACT + "");
                message.setThread(chatModel.getOwnerBareJid());
                break;
            case AppConstants.CODE_AUDIO:
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.CODE_AUDIO + "");
                message.setThread(chatModel.getOwnerBareJid());
                break;
            case AppConstants.CODE_LOCATION:
//                message.setBody(Utils.convertBtyeArrayToBase64(chatModel.getThumbOfMedia()));
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.CODE_LOCATION + "");
//                message.setThread(chatModel.getContent());
                break;
            case AppConstants.CODE_STICKER:
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.CODE_STICKER + "");
                message.setThread("");
                break;

            // Group Chat
            case AppConstants.GROUP_CODE_TEXT:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_TEXT + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_IMAGE:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent() + AppConstants.SEPERATOR_GROUP_JID +
//                        Utils.convertBtyeArrayToBase64(chatModel.getThumbOfMedia()));
//                message.setSubject(AppConstants.GROUP_CODE_IMAGE + "");
//                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_VIDEO:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent() + AppConstants.SEPERATOR_GROUP_JID + Utils.convertBtyeArrayToBase64(chatModel.getThumbOfMedia()));
//                message.setSubject(AppConstants.GROUP_CODE_VIDEO + "");
//                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_DOCUMENT:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_DOCUMENT + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_CONTACT:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_CONTACT + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_AUDIO:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_AUDIO + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_LOCATION:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_LOCATION + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_STICKER:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_LOCATION + "");
                message.setThread(chatModel.getThreadBareJid() + AppConstants.SEPERATOR_GROUP_JID + groupName);
                break;
            case AppConstants.GROUP_CODE_CREATE_GROUP:
                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
                message.setBody(chatModel.getContent());
                message.setSubject(AppConstants.GROUP_CODE_CREATE_GROUP + "");
                message.setThread("" + AppConstants.SEPERATOR_GROUP_JID + chatModel.getThreadBareJid());
                break;
//            case AppConstants.GROUP_CODE_ADD_MEMBER:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent());
//                message.setSubject(AppConstants.GROUP_CODE_ADD_MEMBER + "");
//                message.setThread("" + AppConstants.SEPERATOR_GROUP_JID + chatModel.getThreadBareJid());
//                break;
//            case AppConstants.GROUP_CODE_REMOVE_MEMBER:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent());
//                message.setSubject(AppConstants.GROUP_CODE_REMOVE_MEMBER + "");
//                message.setThread("" + AppConstants.SEPERATOR_GROUP_JID + chatModel.getThreadBareJid());
//                break;
//            case AppConstants.GROUP_CODE_EXIT_GROUP:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent());
//                message.setSubject(AppConstants.GROUP_CODE_EXIT_GROUP + "");
//                message.setThread("" + AppConstants.SEPERATOR_GROUP_JID + chatModel.getThreadBareJid());
//                break;
//            case AppConstants.GROUP_CODE_CHANGE_GROUP_NAME:
//                groupName = getGroupNameByGroupId(chatModel.getThreadBareJid());
//                message.setBody(chatModel.getContent());
//                message.setSubject(AppConstants.GROUP_CODE_CHANGE_GROUP_NAME + "");
//                message.setThread("" + AppConstants.SEPERATOR_GROUP_JID + chatModel.getThreadBareJid());
//                break;
        }
        return message;
    }


    public String getGroupNameByGroupId(String groupId) {
        String groupName = "";
//        groupName = MyAppDataBase.Companion.getUserDataBaseAppinstance(context).groupDao().getGroupName(groupId);

        return groupName;

    }

}
/*
        MamManager manager = MamManager.getInstanceFor(XMPPConnectionListener.Companion.getMConnection());
        MamManager.MamQueryResult r = null;
        try {
            r = manager.mostRecentPage(jid, 80);
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
        if (r.forwardedMessages.size() >= 1) //printing first of them
        {
            Message message = (Message) r.forwardedMessages.get(0).getForwardedStanza();
            Log.i("mam", "message received" + message.getBody());
        }
*/
