package com.advantal.shieldcrypt.xmpp_pkg;

import android.util.Log;

import com.advantal.shieldcrypt.utils_pkg.MyApp;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.chatstates.ChatState;
import org.jivesoftware.smackx.chatstates.ChatStateListener;
//import org.jivesoftware.smack.Cha


/**
 * Created by Sonam on 20-10-2022 13:15.
 */
public class MessageListenerImpl  implements MessageListener, ChatStateListener {



    @Override
    public void processMessage(Message message) {
        System.out.println("Received message: " + message);
        MyApp.Companion.getAppInstance().showToastMsg("Sona..............");

    }

    @Override
    public void stateChanged(Chat arg0, ChatState arg1, Message message) {
        if (ChatState.composing.equals(arg1)) {
            Log.d("Chat State",arg0.getXmppAddressOfChatPartner() + " is typing..");
            MyApp.Companion.getAppInstance().showToastMsg("Sona..............");

        } else if (ChatState.gone.equals(arg1)) {
            MyApp.Companion.getAppInstance().showToastMsg("Sona..............");

            Log.d("Chat State",arg0.getXmppAddressOfChatPartner() + " has left the conversation.");
        } else {
            Log.d("Chat State",arg0.getXmppAddressOfChatPartner() + ": " + arg1.name());
            MyApp.Companion.getAppInstance().showToastMsg("Sona..............");

        }
    }
}