package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.privacy_help_pkg

import android.content.Context
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.advantal.shieldcrypt.databinding.ActivityWebViewBinding
import com.advantal.shieldcrypt.network_pkg.RequestApis


class WebViewActivity : AppCompatActivity() {
    lateinit var binding: ActivityWebViewBinding
    private var isLoaded: Boolean = false
    private var doubleBackToExitPressedOnce = false

    // private var mPictureInPictureParamsBuilder: PictureInPictureParams.Builder? = null
    private var meetingBaseUrl =
    //"http://znzmask.com:8080/MaskingAdmin/payment/paypal.jsp"
    // "https://stackoverflow.com/questions/47872078/how-to-load-an-url-inside-a-webview-using-android-kotlin" // Change it with your URL
    // "http://92.204.128.15:7070/ofmeet/abc"
    //"https://92.204.128.15:7443/ofmeet/abc"
        //"https://meet.jit.si/test123"
        ""+RequestApis.meetingBaseUrl

//           "https://157.23.156.141:7443/ofmeet/"
    var selectedRoomName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
        }*/
        val iin = intent
        val mtngid = iin.extras
        if (mtngid != null) {
            selectedRoomName = mtngid["selectedRoomName"] as String
            meetingBaseUrl = mtngid["meetingBaseUrl"] as String
        }

        loadMyWebView()
    }

    private fun loadMyWebView() {
        binding.webPdf.settings.javaScriptEnabled = true
        // binding.webPdf.settings.loadWithOverviewMode=true
        binding.webPdf.settings.useWideViewPort = true
        binding.webPdf.settings.javaScriptCanOpenWindowsAutomatically = true
        //  binding.webPdf.settings.loadWithOverviewMode=true
        //  binding.webPdf.settings.useWideViewPort=true
        binding.webPdf.settings.domStorageEnabled = true
        binding.webPdf.settings.mediaPlaybackRequiresUserGesture = false
        if (Build.VERSION.SDK_INT >= 19) {
            binding.webPdf.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webPdf.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
        // binding.webPdf.addJavascriptInterface(JavaScriptInterface(this), "handleVideoConferenceLeft")

        // binding.webPdf.clearSslPreferences()
        if (selectedRoomName != null && selectedRoomName.isNotEmpty()) {
            binding.webPdf.loadUrl(meetingBaseUrl + "" + selectedRoomName)
            Log.e("selectedWebViewUrl", " witRoomName->>" + meetingBaseUrl + "" + selectedRoomName)
            binding.webPdf.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url.toString()
                    view?.loadUrl(url)
                    Log.e("selectedWebViewUrl", " shouldOverrideUrlLoading->>" + url)
                    if (meetingBaseUrl.equals("" + url)) {
                        //onBackPressed()
                        finish()
                    }
                    return super.shouldOverrideUrlLoading(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.e("selectedWebViewUrl", " onPageStarted->>" + url)
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("selectedWebViewUrl", " onPageFinished->>" + url)
                    super.onPageFinished(view, url)
                }

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    handler?.proceed()
                    if (handler != null) {
                        Log.e("selectedWebViewUrl", " onReceivedSslError-> " + error?.certificate)
                        handler.proceed()
                    }
                    //  super.onReceivedSslError(view, handler, error)
                }
            }

            binding.webPdf.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    binding.progressBar.progress = newProgress
                    if (newProgress == 100) {
                        binding.progressBar.visibility = View.GONE

                    }
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    /*if (consoleMessage!=null && consoleMessage.message()!=null && consoleMessage.message().toString().isNotEmpty()){
                        Log.e("selectedWebViewUrl", " onConsoleMessage-> " + consoleMessage?.message())
                        var strRemoveEvent = ""+consoleMessage?.message()
                        if (strRemoveEvent!=null && strRemoveEvent!=null && strRemoveEvent.toString().isNotEmpty() && strRemoveEvent.length>0){
                            val splited: List<String> = strRemoveEvent.split(" ")
                            if (splited!=null && splited.size>0){
                                Log.e("selectedWebViewUrl", " iffffff-> " + splited[0])
                                if (splited[0].equals("removeParticipant")){
                                    Log.e("selectedWebViewUrl", " findingDone-> " + splited[0])
                                }
                            }
                        }
                    }*/
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    //super.onPermissionRequest(request)
                    Log.d("selectedWebViewUrl", " onPermissionRequest")
                    this@WebViewActivity.runOnUiThread(Runnable {
                        Log.d("selectedWebViewUrl", " " + request!!.origin.toString())
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            request.grant(request.resources)
                        }
                    })
                }
            }
        }
        //binding.webPdf.loadUrl(webURL)

    }

    class JavaScriptInterface internal constructor(c: Context) {
        var mContext: Context

        @JavascriptInterface
        fun handleVideoConferenceLeft() {
            Toast.makeText(mContext, " You have clicked on meeting end.", Toast.LENGTH_SHORT).show()
        }

        init {
            mContext = c
        }
    }

    /*override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (binding.webPdf.canGoBack()) {
                    binding.webPdf.goBack()
                } else {
                    showToastToExit()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }*/

    private fun showToastToExit() {
        /* when {
             doubleBackToExitPressedOnce -> {
                 onBackPressed()
             }
             else -> {
                 doubleBackToExitPressedOnce = true
                  MyApp.getAppInstance().showToastMsg("Exit")
                 Handler(Looper.myLooper()!!).postDelayed(
                     { doubleBackToExitPressedOnce = false },
                     2000
                 )
             }
         }*/
        onBackPressed()

    }

    /*override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        //when user presses home button, if not in PIP mode, enter in PIP, requires Android N and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            minimize()
        }
    }

    fun minimize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("onPictureInPictureModeChanged", " 1111--> ")
            if (mPictureInPictureParamsBuilder == null) {
                mPictureInPictureParamsBuilder = PictureInPictureParams.Builder()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mPictureInPictureParamsBuilder!!.setAutoEnterEnabled(true)
                mPictureInPictureParamsBuilder!!.setSeamlessResizeEnabled(true)
            }
            enterPictureInPictureMode(mPictureInPictureParamsBuilder!!.build())
        } else {
            //Toast.makeText(this, "Your device doesn't supports PIP", Toast.LENGTH_LONG).show()
        }
    }*/


}