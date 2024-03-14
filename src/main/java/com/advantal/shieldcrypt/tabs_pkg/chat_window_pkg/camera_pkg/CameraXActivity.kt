package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.camera_pkg

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityCameraBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity
import com.advantal.shieldcrypt.utils_pkg.MyApp
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


typealias LumaListener = (luma: Double) -> Unit

class CameraXActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val TAG = "CameraXActivity"
    }

    private var FLASH: Boolean = false

    private var name: String? = ""

    lateinit var binding: ActivityCameraBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        craeteDirctry()
        updateACtionBar()

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun updateACtionBar() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            var cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                if (FLASH)
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer
                    ).cameraControl.enableTorch(true)
                else
                    cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture, imageAnalyzer
                    )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))


    }

    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

            val buffer = image.planes[0].buffer
            val data = buffer.toByteArray()
            val pixels = data.map { it.toInt() and 0xFF }
            val luma = pixels.average()

            listener(luma)

            image.close()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                this.finish()
            }
            R.id.img_camerax_flash -> {
                FLASH = !FLASH
                startCamera()
            }
            R.id.img_capture_image -> {

                takePhoto()
            }
            R.id.camera_preview -> {

                cameraSelector = if (CameraSelector.DEFAULT_BACK_CAMERA == cameraSelector) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                startCamera()
            }
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "ShieldCrypt/CameraX-Image")
                Log.e("Not Execute", "Not Execute")

            }
//            else {
//                Log.e("Not Execute", "Not Execute.......................")
//                 MyApp.getAppInstance().showToastMsg("Not Execute......................")
//                val directory = File("ShieldCrypt/CameraX-Image")
//
//                if (!directory.exists()) {
//                    directory.mkdir()
//                }
//
//            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    var inten = Intent(this@CameraXActivity, ChatsActivity::class.java)
                    inten.putExtra("uri", output.savedUri.toString())
                    inten.putExtra("name", name)
                    startActivity(inten)
                    finish()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun craeteDirctry() {
        val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
        val dir = File(extStorageDirectory)
        if (!dir.exists())
            dir.mkdirs()
        val file: File
        file = File(extStorageDirectory, "/TestFolder")

        if (file.exists()) {
            file.delete()
            file.createNewFile()
        } else {
            file.createNewFile()
        }
    }


}