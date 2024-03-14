package com.advantal.shieldcrypt.qr_code_pkg

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.*
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentSCanQrCodeBinding
import com.advantal.shieldcrypt.utils_pkg.MyApp


class SCanQrCodeFragment : Fragment(), View.OnClickListener, Camera.AutoFocusCallback {
    private lateinit var codeScanner: CodeScanner
    lateinit var binding: FragmentSCanQrCodeBinding

    //public Camera camera = Camera.open();
//    public Camera.Parameters Flash = camera.getParameters();
    var camera = Camera.open()
    var flash = camera.parameters
    var FLASH = false
    var CAMERATYPE = false

    var camManager: CameraManager? = null
    fun turnOff() {
        camera.stopPreview()
        camera.release()
    }

    fun turnOn() {
        camera = Camera.open()
        try {
            val parameters: Camera.Parameters = camera.parameters
            parameters.flashMode =
//                Camera.Parameters.FLASH_MODE_TORCH
                getFlashOnParameter(camera)
            camera.parameters = parameters
            camera.setPreviewTexture(SurfaceTexture(0))
            camera.startPreview()
            camera.autoFocus(this)
        } catch (e: Exception) {
             MyApp.getAppInstance().showToastMsg("We are expecting this to happen on devices that don't support autofocus.")
            // We are expecting this to happen on devices that don't support autofocus.
        }
    }

    override fun onAutoFocus(success: Boolean, camera: Camera?) {
         MyApp.getAppInstance().showToastMsg("Test")
    }

    private fun getFlashOnParameter(camera: Camera): String? {
        val flashModes = camera.parameters.supportedFlashModes
        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
            return Camera.Parameters.FLASH_MODE_TORCH
        } else
            if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                return Camera.Parameters.FLASH_MODE_ON
            } else
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    return Camera.Parameters.FLASH_MODE_AUTO
                }
        throw RuntimeException()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSCanQrCodeBinding.inflate(inflater, container, false)

//        binding.imgFlash.setOnClickListener(this)
//        binding.cameraPreview.setOnClickListener(this)


//        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


//        codeScanner.startPreview()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        val activity = requireActivity()
        codeScanner = CodeScanner(activity, binding.scannerView)


        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = false // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                 MyApp.getAppInstance().showToastMsg(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            activity.runOnUiThread {
                 MyApp.getAppInstance().showToastMsg("SUccesss")
            }
        }



        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                 MyApp.getAppInstance().showToastMsg("Error")
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    private fun frontFlashOn(i: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (camManager == null)
              var  camManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            var cameraId: String? = null
           var check= (i==1)
            try {
                /*******cameraId 1 for Front ON & cameraId 0 for back ON*********/
                camManager?.cameraIdList!![0]?.also { cameraId = it }

                cameraId?.let { camManager.setTorchMode(it,
                   check) }


            } catch (e: CameraAccessException) {
                e.message?.let {  MyApp.getAppInstance().showToastMsg(it) }
                e.printStackTrace()
            }
        }
    }

    private fun backFlashOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (camManager == null)
            var  camManager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
            var cameraId: String? = null
            try {
                /*******cameraId 1 for Front ON & cameraId 0 for back ON*********/
                camManager?.cameraIdList!![0]?.also { cameraId = it }

                cameraId?.let { camManager.setTorchMode(it, true) }


            } catch (e: CameraAccessException) {
                e.message?.let {  MyApp.getAppInstance().showToastMsg(it) }
                e.printStackTrace()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }
}

//https://androiderrors.com/how-to-turn-on-front-flash-light-programmatically-in-android/