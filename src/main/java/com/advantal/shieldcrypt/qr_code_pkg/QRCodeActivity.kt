package com.advantal.shieldcrypt.qr_code_pkg

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.advantal.shieldcrypt.databinding.ActivityQrcodeBinding
import com.advantal.shieldcrypt.utils_pkg.MyApp


class QRCodeActivity : AppCompatActivity(), View.OnClickListener, TabLayout.OnTabSelectedListener {
    lateinit var binding: ActivityQrcodeBinding


    var sectionsPagerAdapter: SectionsPagerAdapter? = null
    private val onPageChangeListener: ViewPager.OnPageChangeListener? = object :
        ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            Log.d(
                "TAG",
                "onPageScrolled: Position: $position. Position Offset: $positionOffset. Position Offset Pixels: $positionOffsetPixels"
            )
        }

        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> {

                }
                1 -> {

                }


            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }


    fun initalizeViews() {

        sectionsPagerAdapter = SectionsPagerAdapter(
            supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.currentItem = 0


        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.getTabAt(0)?.text = "MY CODE"
        binding.tabLayout.getTabAt(1)?.text = "SCAN CODE"

//        intializeTabFirst()


        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))


    }

    override fun onStart() {
        super.onStart()
        onPageChangeListener?.let { binding.viewPager.addOnPageChangeListener(it) }
    }

    override fun onStop() {
        super.onStop()
        onPageChangeListener?.let { binding.viewPager.removeOnPageChangeListener(it) }
    }

    class SectionsPagerAdapter(fm: FragmentManager, behavior: Int) :
        FragmentPagerAdapter(fm, behavior) {
        var mFragmentList = ArrayList<Fragment>()
        var firstFragment: MyQrCodeFragment? = null
        var secondFragment: SCanQrCodeFragment? = null


        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return NUMBER_OF_FRAGMENTS
        }

        companion object {
            const val NUMBER_OF_FRAGMENTS = 2
        }

        init {
            firstFragment = MyQrCodeFragment()
            secondFragment = SCanQrCodeFragment()
            mFragmentList.add(firstFragment!!)
            mFragmentList.add(secondFragment!!)
        }
    }


    override fun onTabSelected(tab: TabLayout.Tab?) {
        binding.viewPager.currentItem = tab!!.position
        val tabIconColor = ContextCompat.getColor(this, com.advantal.shieldcrypt.R.color.white)
        tab.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        val tabIconColor = ContextCompat.getColor(this, com.advantal.shieldcrypt.R.color.colorPrimary)
        tab!!.icon!!.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /********Hide default icons for Flash & Focus***********/
//        scannerView.isFlashButtonVisible = false
//        scannerView.isAutoFocusButtonVisible=false

        onClickRequestPermission()

        /*    codeScanner = CodeScanner(this, binding.scannerView)

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
                runOnUiThread {
                    Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                }
            }
            codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
                runOnUiThread {
                    Toast.makeText(
                        this, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            binding.scannerView.setOnClickListener {
                codeScanner.startPreview()
            }*/
        initalizeViews()
//        turnOn()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
                 MyApp.getAppInstance().showToastMsg("Permission:  Granted")
                initalizeViews()
            } else {
                Log.i("Permission: ", "Denied")
                 MyApp.getAppInstance().showToastMsg("Permission:  Denied")
                onClickRequestPermission()


            }
        }

/*
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }
*/

    fun onClickRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
//                initalizeViews()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                onClickRequestPermission()
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
                 MyApp.getAppInstance().showToastMsg("Open Camera")

            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
        }
    }

    override fun onBackPressed() {
        this.finish()
    }

//    fun turnOff() {
////        camera.stopPreview()
////        camera.release()
//    }

//    fun turnOn() {
//        var camera = Camera.open()
//        try {
//            val parameters: Camera.Parameters = camera.parameters
//            parameters.flashMode = getFlashOnParameter(camera)
//            camera.parameters = parameters
//            camera.setPreviewTexture(SurfaceTexture(0))
//            camera.startPreview()
//            camera.autoFocus(this)
//        } catch (e: Exception) {
//             MyApp.getAppInstance().showToastMsg("We are expecting this to happen on devices that don't support autofocus.")
//            // We are expecting this to happen on devices that don't support autofocus.
//        }
//    }
//
//    private fun getFlashOnParameter(camera: Camera): String? {
//        val flashModes = camera.parameters.supportedFlashModes
//        if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
//            return Camera.Parameters.FLASH_MODE_TORCH
//        } else
//        if (flashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
//            return Camera.Parameters.FLASH_MODE_ON
//
//        } else if (flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
////            Camera.Parameters.CAM
//            return Camera.Parameters.FLASH_MODE_AUTO
//        }
//        throw RuntimeException()
//    }


}



