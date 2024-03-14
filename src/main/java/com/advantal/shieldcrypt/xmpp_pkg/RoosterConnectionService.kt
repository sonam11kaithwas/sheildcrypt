package com.advantal.shieldcrypt.xmpp_pkg

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import java.io.IOException

/**
 * Created by Sonam on 26-09-2022 12:39.
 */
//@AndroidEntryPoint
class RoosterConnectionService(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

//@Inject
//lateinit var networkHelper: NetworkHelper
    var mActive: Boolean = false
    var mThread: Thread? = null
    var mTHandler: Handler? = null


    companion object {
        var TAG = "RoosterService"
        var isXmppServiceisRunning = false
        const val UI_AUTHENTICATED = "com.blikoon.rooster.uiauthenticated"
        const val SEND_MESSAGE = "com.blikoon.rooster.sendmessage"
        const val BUNDLE_MESSAGE_BODY = "b_body"
        const val BUNDLE_MESSAGE_ID = "messageID"
        const val BUNDLE_TO = "b_to"
        const val FILE_DOWNLOAD_RECEIVER = "com.filedownloaded"
        const val PRESENCE_CHANGE = "com.presence.change"

        const val NEW_MESSAGE = "com.blikoon.rooster.newmessage"
        const val BUNDLE_FROM_JID = "b_from"

        public var sConnectionState: XMPPConnectionListener.ConnectionState? = null
        var sLoggedInState: XMPPConnectionListener.LoggedInState? = null
        lateinit var mConnection: XMPPConnectionListener

        fun getState(): XMPPConnectionListener.ConnectionState? {
            return if (sConnectionState == null) {
                XMPPConnectionListener.ConnectionState.DISCONNECTED
            } else sConnectionState
        }



        fun getLoggedInState(): XMPPConnectionListener.LoggedInState? {
            return if (sLoggedInState == null) {
                XMPPConnectionListener.LoggedInState.LOGGED_OUT
            } else sLoggedInState
        }
    }

    private fun initConnection() {
        println("init connection called ")
        println("Work manager called init work")
        Log.d(TAG, "initConnection()")

        try {

//            if (networkHelper.isNetworkConnected()) {
            if (true) {
                mConnection = XMPPConnectionListener.getInstance()!!
                mConnection.initObject(applicationContext)
               // mConnection.connect()
                try {
                    mConnection.connect()
                }  catch (e: XMPPException ) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                } catch (e: SmackException.NotLoggedInException) {
                    e.printStackTrace()
                } catch (e: XMPPException) {
                    e.printStackTrace()
                } catch (e: SmackException.NoResponseException) {
                    e.printStackTrace()
                }
            }
        } catch (e: IOException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
            //Stop the service all together.
//            stopSelf();
        } catch (e: SmackException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
        } catch (e: XMPPException) {
            Log.d(
                TAG,
                "Something went wrong while connecting ,make sure the credentials are right and try again"
            )
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: SmackException.NotLoggedInException) {
            e.printStackTrace()
        } catch (e: XMPPException) {
            e.printStackTrace()
        } catch (e: SmackException.NoResponseException) {
            e.printStackTrace()
        }
    }

    fun start() {

//        setAlarm();
        if (!mActive) {
            mActive = true
            if (mThread == null || !mThread!!.isAlive) {
                mThread = Thread {
                    Looper.prepare()
                    mTHandler = Handler()
                  //  initConnection()
                    //THE CODE HERE RUNS IN A BACKGROUND THREAD.
                    Looper.loop()
                }
                mThread!!.start()
            }
        }
    }

    override fun doWork(): Result {
        isXmppServiceisRunning = true
        start()
        return Result.success()
    }
//    override fun doWork(): Result {
//Log.e("","")
//    }
}