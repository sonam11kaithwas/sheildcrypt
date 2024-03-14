//@file: JvmName("MyKotlinFileName")
package com.advantal.shieldcrypt.utils_pkg

//import androidx.work.OneTimeWorkRequest
//import androidx.work.WorkManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.xmpp_pkg.MyMessageModel
import com.advantal.shieldcrypt.xmpp_pkg.RoosterConnectionService
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import database.my_database_pkg.db_table.MyAppDataBase
import java.util.*
import java.util.Random

/**
 * Created by Sonam on 30-06-2022 15:59.
 */
public class AppUtills {
    companion object {
        private var dialog: AlertDialog? = null

        //        var threadName: String? = ""
        var NewVarthreadName: String? = ""
        fun getUserFromJid(jid: String): String? {
            var contactName = ""
            contactName = if (jid.contains("@")) {
                jid.split("@".toRegex()).toTypedArray()[0]
            } else {
                jid
            }
            return contactName
        }

        fun getTokenForAPiCall(): HashMap<String, String> {
            var hashMap: HashMap<String, String> = HashMap<String, String>()
            MySharedPreferences.getSharedprefInstance().getLoginData().token?.let {
                hashMap.put(
                    "token", it
                )
            }
            MySharedPreferences.getSharedprefInstance().getLoginData().token?.let {
                Log.e(
                    "Token", it
                )
            }
            return hashMap
        }

        fun stringToJsonObject(modelstr: String): JsonObject {
            val parser = JsonParser()
            return parser.parse(modelstr).asJsonObject
        }

        fun closeProgressDialog() {
            dialog.let {
                dialog?.dismiss()
            }
        }

        fun setDialog(
            context: Context,
            diaMsg: String,
            positiveBtn: String,
            callbackAlertDialog: CallbackAlertDialog
        ) {
            val dialog: AlertDialog.Builder = AlertDialog.Builder(context)

            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view: View = layoutInflater.inflate(R.layout.alert_dialog_layout, null, false)


            var dia_msg = view.findViewById<TextView>(R.id.dia_msg)
            dia_msg.text = diaMsg

            dialog.setCancelable(false)

            dialog.setView(view)


            dialog.setPositiveButton(positiveBtn) { dialog, which ->
                callbackAlertDialog.onPossitiveCall()
                dialog.dismiss()
            }

            dialog.setNegativeButton(R.string.cancel) { dialog, which ->
                callbackAlertDialog.onNegativeCall()
                dialog.dismiss()
            }

            dialog.show()

        }

        fun setProgressDialog(context: Context) {
            // Creating a Linear Layout
            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER
            ll.layoutParams = llParam

            // Creating a ProgressBar inside the layout
            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam
            llParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            llParam.gravity = Gravity.CENTER

            // Creating a TextView inside the layout
            val tvText = TextView(context)
            tvText.text = "Loading ..."
            tvText.setTextColor(Color.parseColor("#000000"))
            tvText.textSize = 20f
            tvText.layoutParams = llParam
            ll.addView(progressBar)
            ll.addView(tvText)

            // Setting the AlertDialog Builder view
            // as the Linear layout created above
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setView(ll)

            // Displaying the dialog
            dialog = builder.create()
            dialog?.show()

            val window: Window? = dialog?.window
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog?.window?.attributes)
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog?.window?.attributes = layoutParams

                // Disabling screen touch to avoid exiting the Dialog
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }

        }


        /*****************Check Xmpp Connection and make connection if not connected*****************/

        fun isXmppWorkScheduled(context: Context): Boolean {
            println("NetworkSchedulerService connected inside isXmppworkdscheduler utils")
            var isRunning = false

            try {
                if (XMPPConnectionListener.mConnection == null) {
                    println("xmpp connection status null")
                    val oneTimeWorkRequest: OneTimeWorkRequest =
                        OneTimeWorkRequest.Builder(RoosterConnectionService::class.java)
                            .addTag(AppConstants.xmppWorkManagerTag).build()
                    WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
                    isRunning = false
                } else {
                    var i: XMPPConnectionListener.ConnectionState =
                        RoosterConnectionService.getState()!!
                    if (!XMPPConnectionListener.mConnection!!.isConnected && !RoosterConnectionService.getState()
                            ?.equals(XMPPConnectionListener.ConnectionState.CONNECTING)!!
                    ) {
                        println("xmpp connection status not connected")
                        val oneTimeWorkRequest: OneTimeWorkRequest =
                            OneTimeWorkRequest.Builder(RoosterConnectionService::class.java)
                                .addTag(AppConstants.xmppWorkManagerTag).build()
                        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
                        isRunning = false
                    } else if (RoosterConnectionService.getState()//conncted
                            ?.equals((XMPPConnectionListener.ConnectionState.CONNECTING))!!//conectting
                    ) {
                        println("xmpp connection status connecting")
                        isRunning = false
                    } else {
                        println("xmpp connection status already connected")
                        println("RoosterConnection xmpp connected")
                        isRunning = true
                    }
                }
            } catch (e: Exception) {
                println("xmpp connection status " + e.message)
                e.printStackTrace()
            }
            if (isRunning) XMPPConnectionListener.sendPresenceOnline()

            return isRunning
        }

        fun getRandomCode(): Int {
            val rnd = Random()
            return 100 + rnd.nextInt(9000)
        }


        fun insertChatToDb(
            myMessageModel: MyMessageModel, unReadCount: Int, SENER: Boolean
        ) {
//            MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.messageDao()
//                ?.insert(myMessageModel)
//            insertRecentChat(myMessageModel, unReadCount, SENER)
        }


        private fun insertRecentChat(
            myMessageModel: MyMessageModel, unReadCount: Int, SENER: Boolean
        ) {

            if (SENER) {
                var ownId = myMessageModel.ownerBareJid
                var threId = myMessageModel.threadBareJid
                myMessageModel.ownerBareJid = threId
                myMessageModel.threadBareJid = ownId
            }

//            var content: String = getMessageType(
//                myMessageModel.contentType, myMessageModel.content.toString()
//            )

//            var recentChatMessageModelList =
//                MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
//                    ?.recentChatMessageDao()?.getThreadExist(myMessageModel.threadBareJid!!)
            var tempCount = 0
//            Add unread count value
//            if (recentChatMessageModelList != null && recentChatMessageModelList.isNotEmpty()) {
//                tempCount = recentChatMessageModelList[0].unreadCount + unReadCount
//            }

            var threadName: String? = "Sender"
            if (myMessageModel.myType?.toInt() ?: 0 == AppConstants.INDIVIDUAL) {
                threadName =
                    (MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.contactDao()
                        ?.getContactNameById(myMessageModel.threadBareJid!!))?.username
                if (threadName == null || threadName.equals("")) {
                    threadName = myMessageModel.threadBareJid
                }
            } else if
                           (myMessageModel.myType?.toInt() ?: 0 == AppConstants.GROUP) {

//                threadName = (myMessageModel.threadBareJid?.let {
//                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())?.groupDao()
//                        ?.getGrpName(it)
//                })?.groupName
//                if (threadName == null || threadName.equals("")) {
//                    threadName = myMessageModel.threadBareJid
//                }
            }

//            var recentChatMessageModel = RecentChatMessageModel(
//                myMessageModel.threadBareJid.toString(),
//                content,
//                myMessageModel.contentType,
//                myMessageModel.messageTimestamp,
//                tempCount,
//                myMessageModel.deliveryStatus,
//                myMessageModel.messageIdNew.toString(),
//                myMessageModel.myType,
//                myMessageModel.isOutBound,
//                threadName!!
//            )

//            if ((recentChatMessageModelList != null) && recentChatMessageModelList.isEmpty()) {
//                MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
//                    ?.recentChatMessageDao()?.insertRecentChat(recentChatMessageModel)
            }
//        else {
////                MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
////                    ?.recentChatMessageDao()?.updateRecentsChat(recentChatMessageModel)
//
//            }

        }

        fun getMessageType(messageType: Int, content: String): String {
            var formatedContent = ""
            when (messageType) {
                AppConstants.CODE_TEXT, AppConstants.GROUP_CODE_TEXT -> {
                    formatedContent = content
                }
                AppConstants.CODE_IMAGE, AppConstants.GROUP_CODE_IMAGE -> {
                    formatedContent = "Image"
                }
                AppConstants.CODE_VIDEO, AppConstants.GROUP_CODE_VIDEO -> {
                    formatedContent = "Video"
                }
                AppConstants.CODE_DOCUMENT, AppConstants.GROUP_CODE_DOCUMENT -> {
                    formatedContent = "Document"
                }
                AppConstants.CODE_CONTACT, AppConstants.GROUP_CODE_CONTACT -> {
                    formatedContent = "Contact"
                }
                AppConstants.CODE_AUDIO, AppConstants.GROUP_CODE_AUDIO -> {
                    formatedContent = "Audio"
                }
                AppConstants.CODE_LOCATION, AppConstants.GROUP_CODE_LOCATION -> {
                    formatedContent = "Location"
                }
                AppConstants.GROUP_CODE_CREATE_GROUP -> {
                    formatedContent = "New Group Created"
                }

                else -> {
                    formatedContent = content
                }

            }

            return formatedContent
        }

        fun getidwithoutip(ID: String): String? {
            return if (ID.contains("@")) {
                ID.split("@".toRegex()).toTypedArray()[0]
            } else {
                ID
            }
        }

        fun getChatModelObjectFromChatType(
            messageId: String?,
            ownerBareJid: String?,
            threadBareJid: String?,
            rosterBareJid: String?,
            content: String?,
            contentType: Int,
            isOutBound: Boolean,
            isChatThreadType: Int,
            isMessageRead: Boolean,

            messageDate: Date?,
            messageTimestamp: Long,
            deliveryStatus: Int,
            localFilePath: String?,
            fileTransferStatus: Int,
            thumbOfMedia: ByteArray?,
            latLong: String?,
            actionOnMessage: Int,
            isMessageStared: Boolean,
            commentOnMedia: String?,
            commentOfOldMessage: String?,
            messageIdOfOldMessage: String?,
            priorityLevel: Int,
            repliedOnWhichContent: Int,
            thumbOfOldMedia: ByteArray?,
            evapExpireDuration: Int,
            forwardedMessageCount: Int
        ): MyMessageModel? {
            val chatModel = MyMessageModel()
            chatModel.messageIdNew = messageId.toString()
            chatModel.ownerBareJid = ownerBareJid
            chatModel.threadBareJid = threadBareJid
            chatModel.rosterBareJid = rosterBareJid
            chatModel.content = content
            chatModel.contentType = contentType
            chatModel.isOutBound = isOutBound
            chatModel.myType = isChatThreadType.toString()
            chatModel.isMessageRead = isMessageRead
            chatModel.messageDate = messageDate.toString()
            chatModel.messageTimestamp = messageTimestamp
            chatModel.deliveryStatus = deliveryStatus
            chatModel.localFilePath = localFilePath
            chatModel.fileTransferStatus = fileTransferStatus
//            chatModel.thumbOfMedia = thumbOfMedia!!
            chatModel.latLong = latLong
            chatModel.actionOnMessage = actionOnMessage
            chatModel.isMessageStared = isMessageStared
            chatModel.commentOnMedia = commentOnMedia
            chatModel.commentOfOldMessage = commentOfOldMessage
            chatModel.messageIdOfOldMessage = messageIdOfOldMessage
            chatModel.priorityLevel = priorityLevel
            chatModel.repliedOnWhichContent = repliedOnWhichContent
//            chatModel.thumbOfOldMedia = thumbOfOldMedia!!
            chatModel.evapExpireDuration = evapExpireDuration
            chatModel.forwardedMessageCount = forwardedMessageCount
            return chatModel
        }


    }


//}