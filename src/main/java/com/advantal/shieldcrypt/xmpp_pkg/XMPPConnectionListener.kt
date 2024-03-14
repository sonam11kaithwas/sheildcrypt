package com.advantal.shieldcrypt.xmpp_pkg

import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.service.ServerPingWithAlarmManager
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences.Companion.getSharedprefInstance
import org.jivesoftware.smack.*
import org.jivesoftware.smack.SmackException.NotConnectedException
import org.jivesoftware.smack.chat2.ChatManager
import org.jivesoftware.smack.filter.StanzaTypeFilter
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smack.packet.Presence
import org.jivesoftware.smack.packet.Stanza
import org.jivesoftware.smack.roster.Roster
import org.jivesoftware.smack.roster.RosterListener
import org.jivesoftware.smack.tcp.XMPPTCPConnection
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration
import org.jivesoftware.smackx.ping.PingManager
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest
import org.jxmpp.jid.EntityBareJid
import org.jxmpp.jid.Jid
import org.jxmpp.jid.impl.JidCreate
import org.jxmpp.stringprep.XmppStringprepException
import java.io.IOException
import java.net.InetAddress
import javax.inject.Inject
import javax.net.ssl.HostnameVerifier


/**
 * Created by Sonam on 16-08-2022 11:32.
 */

class XMPPConnectionListener : ConnectionListener {

    /******** Now implement XMPPConnectionListener class.
     *  It executes after connection is made with XMPP Server.
     *  It is used to check that connection is made successfully with server or not.
     ******/
    var isOfflineMessageSending = false
    lateinit var context: Context
    private var mUsername: String? = ""
    private var mPassword: String? = ""
    private var mServiceName: String? = ""
    var xmppIncomingMessages: XmppIncomingMessages? = null

    @Inject
    lateinit var networkHelper: NetworkHelper
    lateinit var deliveryReceiptManager: DeliveryReceiptManager
    var xmppSendMessage: XmppSendMessage? = null

    enum class ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    enum class LoggedInState {
        LOGGED_OUT, LOGGED_IN
    }

    companion object {
        var mConnection: XMPPTCPConnection? = null
        var xmppConnect: XMPPConnectionListener? = null

        var roster: Roster? = null
        fun setmConnection(mConnections: XMPPTCPConnection) {
            mConnection = mConnections
        }

        fun getInstance(): XMPPConnectionListener? {
            if (xmppConnect == null) {
                xmppConnect = XMPPConnectionListener()
            }
            return xmppConnect
        }


        fun sendPresenceOffline() {
            val presence: Presence
            presence = Presence(Presence.Type.unavailable)
//             Presence(Presence.Type)
            try {
                Log.e("offline", "ofline")

                mConnection?.sendStanza(presence)
            } catch (e: NotConnectedException) {
                e.printStackTrace()
                Log.e("offline", e.message!!)

            } catch (e: InterruptedException) {
                Log.e("offline", e.message!!)

                e.printStackTrace()
            }
        }

        fun sendPresenceOnline() {
            val presence = Presence(Presence.Type.available)
            presence.priority = 24
            try {
                mConnection?.sendStanza(presence)
                Log.e("online", "online")
            } catch (e: NotConnectedException) {
                e.printStackTrace()
                Log.e("online", e.message!!)

            } catch (e: InterruptedException) {
                e.printStackTrace()
                Log.e("online", e.message!!)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("online", e.message!!)

                println("Xmpp data $e")
            }
        }

    }


    fun disconnect() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getAppInstance())
        prefs.edit().putBoolean("xmpp_logged_in", false).commit()
        mConnection?.disconnect()
        if (mConnection != null) {
            if (MyApp.getAppInstance().getNetWokAvailabe()) {
//                Log.d("com.ithub.xmpp.XmppConnect.TAG", "Xmpp data Reconnecting from server")
//                DialogUtility.showLOG("Xmpp data XMPP Reconnecting from server $mServiceName")
                val reconnectionManager =
                    ReconnectionManager.getInstanceFor(mConnection)
                reconnectionManager.enableAutomaticReconnection()
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY)
                reconnectionManager.setFixedDelay(3)
                ServerPingWithAlarmManager.onCreate(MyApp.getAppInstance())
                ServerPingWithAlarmManager.getInstanceFor(mConnection).isEnabled =
                    true
            } else {
            }
        }
    }


    @Throws(IOException::class, XMPPException::class, SmackException::class)
    fun connect() {

        var v2 = MySharedPreferences.getSharedprefInstance().getLoginData().xmpip
        var v1 = "https://"
        val addr =
            InetAddress.getByName(MySharedPreferences.getSharedprefInstance().getXSip())
        val verifier =
            HostnameVerifier { hostname, session -> false }
        val serviceName = JidCreate.domainBareFrom(
            MySharedPreferences.getSharedprefInstance().getLoginData().xmpip
        )
        val conf: XMPPTCPConnectionConfiguration = XMPPTCPConnectionConfiguration.builder()
            .setHost("$v1$v2")
            .setUsernameAndPassword(mUsername, mPassword)
            .setPort(
                MySharedPreferences.getSharedprefInstance().getLoginData().xmpport?.toInt()
                    ?: "5222".toInt()
            )
            .setResource("Advantal")
            .setKeystoreType(null) //This line seems to get rid of the problem
            .setSendPresence(true)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .setXmppDomain(serviceName)
            .setHostnameVerifier(verifier)
            .setHostAddress(addr)
            .build()


        mConnection?.setUseStreamManagement(true)
        mConnection?.setUseStreamManagementResumption(true)


        SASLAuthentication.blacklistSASLMechanism("SCRAM-SHA-1")
        SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5")
        SASLAuthentication.unBlacklistSASLMechanism("PLAIN")
        //Set up the ui thread broadcast message receiver.
//        setupUiThreadBroadCastMessageReceiver()
        mConnection = XMPPTCPConnection(conf)
        mConnection!!.addConnectionListener(this)
        try {
            Log.d("TAG", "Calling connect() ")
            try {
                mConnection!!.connect()
            } catch (e: XMPPException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // mConnection!!.connect()

            if (mConnection!!.isConnected) {
                Log.d("XMPP", "Connected")
            }


//            mConnection?.login("8109383638", "AmAn@123456")
//            mConnection?.login("7509384950", "ArVi@123456")
            mConnection?.login(mUsername, mPassword)
            if (mConnection?.isAuthenticated!!) {
                Log.d("XMPP", "Authenticated")
                roster = Roster.getInstanceFor(mConnection)
                xmppSendMessage = XmppSendMessage(context)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: SmackException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: SmackException.NotLoggedInException) {
            e.printStackTrace()
        } catch (e: XMPPException) {
            e.printStackTrace()
        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        } catch (e: SmackException.ConnectionException) {
            e.printStackTrace()
        } catch (e: SmackException.NotConnectedException) {
            e.printStackTrace()
        }


        val reconnectionManager =
            ReconnectionManager.getInstanceFor(mConnection)
        ReconnectionManager.setEnabledPerDefault(true)
        reconnectionManager.enableAutomaticReconnection()
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY)
        reconnectionManager.setFixedDelay(3)
        ReconnectionManager.setEnabledPerDefault(true)
        // Add ping manager here

        // Add ping manager here
        PingManager.getInstanceFor(mConnection)
            .registerPingFailedListener {
                /*tempraryly comment*/
//                disconnect()
                try {
                    //connect()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: XMPPException) {
                    e.printStackTrace()
                } catch (e: SmackException) {
                    e.printStackTrace()
                }
            }


        if (mConnection!!.isConnected) {
            try {

                mConnection!!.disconnect()
            } catch (e: java.lang.Exception) {
                e.message
            }
        }

        mConnection!!.replyTimeout = 40000


        try {
            if (mConnection != null) {
                mConnection!!.connect()
                setmConnection(mConnection!!)
                if (mUsername == null || mPassword == null) {

                    mUsername =
                        MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
                    mPassword = MySharedPreferences.getSharedprefInstance().getLoginData().password

                }
                mConnection!!.login(mUsername, mPassword)
                deliveryReceiptManager =
                    DeliveryReceiptManager.getInstanceFor(mConnection)
                deliveryReceiptManager.autoReceiptMode =
                    DeliveryReceiptManager.AutoReceiptMode.always
                deliveryReceiptManager.autoAddDeliveryReceiptRequests()
                deliveryReceiptManager.addReceiptReceivedListener { fromJid, toJid, receiptId, receipt ->
                    changeMessageDeliveryStatus(
                        receiptId,
                        AppConstants.xepMessageDelivered
                    )
                }
                val pm = PingManager.getInstanceFor(mConnection)
                pm.pingInterval = 10
            } else {
               // connect()
                mConnection = XMPPTCPConnection(conf)
                mConnection!!.addConnectionListener(this)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }


        //        setupUiThreadBroadCastMessageReceiver();


        mConnection!!.addConnectionListener(object : ConnectionListener {
            override fun connected(connection: XMPPConnection) {
                RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED
            }

            override fun authenticated(connection: XMPPConnection, resumed: Boolean) {
                RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED

            }

            override fun connectionClosed() {
                println("roster connectionClosed ")
                RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED

            }

            override fun connectionClosedOnError(e: java.lang.Exception) {
                println("roster connectionClosedOnError " + e)
                RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED
            }
        })
        roster = Roster.getInstanceFor(mConnection)

        roster?.addRosterListener(object : RosterListener {
            override fun entriesAdded(addresses: Collection<Jid>) {
                println("roster update " + addresses.size)
            }

            override fun entriesUpdated(addresses: Collection<Jid>) {
                println("roster update " + addresses.size)
            }

            override fun entriesDeleted(addresses: Collection<Jid>) {
                println("roster delete " + addresses.size)
            }

            override fun presenceChanged(presence: Presence) {
                val intent = Intent(RoosterConnectionService.PRESENCE_CHANGE)
                MyApp.getAppInstance().sendBroadcast(intent)
            }
        })

        var my = MessageListenerImpl()
    }

    fun initObject(context: Context) {
        try {
            this.context = context
            println("NetworkSchedulerService connected xmppconnect initobject")
//            Log.d("XMPPConnectionListener: ", "XmppConnect Constructor called.")
            mUsername = MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber
            mPassword = MySharedPreferences.getSharedprefInstance().getLoginData().password

            mServiceName =
                MySharedPreferences.getSharedprefInstance().getLoginData().xmpip//xmppIpDynamic
        } catch (ex: Exception) {
//            Log.d("XMPPConnectionListener: ", "<-- XmppConnect initObject fail.")
        }
    }

    // Acknowledge the message has been delivered to user
    fun changeMessageDeliveryStatus(packetID: String, new_status: Int) {
//        MyAppDataBase.getUserDataBaseAppinstance(context)?.messageDao()
//            ?.updateDeliveryStatus(packetID, new_status)
    }

    override fun connected(connection: XMPPConnection?) {


        Log.d("xmpp", "Connected!")
        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED
        // send offline messages which were not sent because of no internet or xmpp connection issue
        if (!isOfflineMessageSending) {
            sendOfflineMessages()
        }
    }


    override fun authenticated(connection: XMPPConnection?, resumed: Boolean) {
        Log.d("authenticated", "Authenticated Successfully")

        RoosterConnectionService.sConnectionState = ConnectionState.CONNECTED

        roster = Roster.getInstanceFor(mConnection)

        sendPresenceOnline()

        // Will receive the server receipts

        mConnection?.addSyncStanzaListener(
            object : StanzaListener {
                override fun processStanza(packet: Stanza?) {
                    Log.e("", "")
                    if (packet is Message) {//(message.type).name.equals("chat") //(message.type).name.equals("normal")
                        val message = packet
                        if ((message.type).name.equals("chat", ignoreCase = true)) {
//                            MyApp.getAppInstance().showToastMsg("Typing")
                            Log.e("name", "Typing")

                        } else if ((message.type).name.equals("normal", ignoreCase = true)) {
//                            MyApp.getAppInstance().showToastMsg("Send message")
                            Log.e("name", "normal")

                        }
                        Log.e("name", (message.type).name.toString())
//                        MyApp.getAppInstance().showToastMsg((message.type).name.toString())

                        if (message.type.equals(StanzaTypeFilter.PRESENCE)) {
                            val friendJid =
                                message.from.toString().split("@".toRegex()).toTypedArray()[0]
                        } else {
                            val messageId = message.stanzaId
                        }
                    }
                }
            }, null
        )


        /*
        mConnection?.addSyncStanzaListener(StanzaListener { packet -> //                System.out.println("stanza --- new stanza");

            if (packet is Message) {
                val message = packet
                if (message.type.equals(StanzaTypeFilter.PRESENCE)) {
                    val friendJid = message.from.toString().split("@".toRegex()).toTypedArray()[0]
                } else {
                    val messageId = message.stanzaId
                }
            }
        }, null)
*/


        xmppIncomingMessages = XmppIncomingMessages(context)
//        getRoasterConnection()

    }

    override fun connectionClosed() {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED

        Log.d("xmpp", "ConnectionCLosed!")
    }

    override fun connectionClosedOnError(e: Exception?) {
        RoosterConnectionService.sConnectionState = ConnectionState.DISCONNECTED
        Log.d("xmpp", "ConnectionClosedOn Error!")
    }

    // send offline messages which were not sent because of no internet or xmpp connection issue
    private fun sendOfflineMessages() {
        isOfflineMessageSending = true
        var al_chat: List<MyMessageModel> = ArrayList<MyMessageModel>()
//        al_chat = MyAppDataBase.getUserDataBaseAppinstance(context)?.messageDao()
//            ?.getAllOfflineMessages(AppConstants.xepMessageNotSent)!!
        for (i in al_chat.indices) {
            if (AppUtills.isXmppWorkScheduled(context)
//                && (MyApp.Companion.getAppInstance().getNetWokAvailabe())
            ) {
                var messageType = 0
                var uploadingStatus = 0
                val chatModel: MyMessageModel = al_chat[i]
                messageType = chatModel.contentType
                uploadingStatus = chatModel.fileTransferStatus
                if (messageType == AppConstants.CODE_TEXT || messageType == AppConstants.CODE_CONTACT || messageType == AppConstants.CODE_LOCATION) {
//                    sendMessage(chatModel)
                    xmppSendMessage?.sendMessage(chatModel)
//                    MyAppDataBase.getUserDataBaseAppinstance(context)?.messageDao()
//                        ?.updateXepMessageStatus(
//                            chatModel.messageIdNew!!,
//                            AppConstants.xepMessageSent
//                        )
                } else {
                    if (uploadingStatus == AppConstants.uploaded) {
                        sendMessage(chatModel)
//                        MyAppDataBase.getUserDataBaseAppinstance(context)?.messageDao()
//                            ?.updateXepMessageStatus(
//                                chatModel.messageIdNew!!,
//                                AppConstants.xepMessageSent
//                            )
                    }
                }
            } else {
                isOfflineMessageSending = false
                return
            }
        }
        isOfflineMessageSending = false
    }

    private fun sendMessage(chatModel: MyMessageModel) {
        val messageType = chatModel.contentType
        val isGroupChat = 1 //chatModel.getT
        var jid: EntityBareJid? = null
        val chatManager = ChatManager.getInstanceFor(mConnection)
        try {
            // if Message is One to One
            if (isGroupChat == AppConstants.INDIVIDUAL) {
                jid =
                    JidCreate.entityBareFrom(chatModel.threadBareJid + "@" + getSharedprefInstance().getLoginData().xmpip) //Pramod pramodsadh@shieldcrypt.co.in
                //                jid = JidCreate.entityBareFrom(chatModel.getThreadBareJid() + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());//Pramod pramodsadh@shieldcrypt.co.in
            } else if (isGroupChat == AppConstants.GROUP) { // if Message for group
                jid = JidCreate.entityBareFrom(
                    chatModel.threadBareJid + AppConstants.atTheRateBroadcast +
                            getSharedprefInstance().getLoginData().xmpip
                )
            }
        } catch (e: XmppStringprepException) {
            e.printStackTrace()
        }

        // ChatManager chatManager=new ChatManager();
        val chat = chatManager.chatWith(jid)
//        chatManager.
        try {
//            for (int i = 0; i < 2000; i++) {
            var message = Message(jid, Message.Type.chat)

//            String s = System.currentTimeMillis() + "";
//            message = getMessageObjectForSendMessage(message, messageType, chatModel)
            message.stanzaId = chatModel.messageIdNew
            val receiptId = DeliveryReceiptRequest.addTo(message)
            println("Stanzaaaa  " + message.toXML(""))
            chat.send(message)
//            mConnection.sendM(message)
            if (isGroupChat == AppConstants.INDIVIDUAL) {
//                getUserDataBaseAppinstance(context)!!
//                    .messageDao()
//                    .updateXepMessageStatus((chatModel.messageIdNew)!!, AppConstants.xepMessageSent)
            }
            //            }
        } catch (e: NotConnectedException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

//    fun getRoasterConnection() {
////    val roster: Roster = xmppConnection.getRoster()
//        val groups = roster?.groups
//        if (groups != null) {
//            for (group in groups) {
//                println("Group Name:- " + group.name)
//                val entries: Collection<RosterEntry> = roster!!.entries
//                for (entry1 in entries) {
//                    println("UserID:- " + entry1.user)
//                    println("Name:- " + entry1.name)
//                    System.out.println("Status:- " + entry1.name)
//                    println("type:- " + entry1.type)
//                }
//            }
//        }
//    }
}


//https://discourse.igniterealtime.org/t/how-to-get-offline-messages-asmack-android-real-time/85091

//https://stackoverflow.com/questions/8614640/xmpp-unable-to-set-up-a-listener