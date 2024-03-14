package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.advantal.sheildcrypt.network_pkg.StatusPictureUpload
import com.google.gson.Gson
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentTextStatusBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.model.TextStatusModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.vanniktech.emoji.EmojiPopup
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class TextStatusFragment : Fragment(), UploadCallBack {
    lateinit var binding: FragmentTextStatusBinding
    var clicks: Int = 1;
    var buttonbackgrmd: Int = 1;
    private val mainViewModel: MainViewModel by viewModels()
    val APP_TAG = "ShieldCrypt"
    private var bitmap: Bitmap? = null
    private var craetedFile: File? = null
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextStatusBinding.inflate(inflater, container, false)

        val popup = EmojiPopup.Builder.fromRootView(binding.relay).build(binding.edittextStatus)

        binding.emoji.setOnClickListener { popup.toggle() }

        Log.e("onCreateView: ", binding.emoji.setOnClickListener { popup.toggle() }.toString())
        verifyStoragePermission(requireActivity())

//        binding.emoji.setOnClickListener { emojiPopup!!.toggle() }


//        var buttonbackgrmd = 0

        // When button is clicked
        binding.changebackground.setOnClickListener {
            // Variable is incrementedbi
            buttonbackgrmd += 1
            // Enters when loop and changes typeface   clicks = 0
            when (buttonbackgrmd) {
                1 -> {
                    binding.relay.setBackgroundResource(R.color.colorGreyBlue)
                    buttonbackgrmd = 1
                }

                2 -> {
                    binding.relay.setBackgroundResource(R.color.eblue)
                    buttonbackgrmd = 2
                }

                3 -> {
                    binding.relay.setBackgroundResource(R.color.eorange)

                    buttonbackgrmd = 3
                }
                4 -> {
                    binding.relay.setBackgroundResource(R.color.edarkgreen)
                    buttonbackgrmd = 4
                }
                5 -> {
                    binding.relay.setBackgroundResource(R.color.elightgreen)
                    buttonbackgrmd = 5
                }
                6 -> {
                    binding.relay.setBackgroundResource(R.color.black_clr)
                    buttonbackgrmd = 6
                }
                7 -> {
                    binding.relay.setBackgroundResource(R.color.enormalblue)
                    buttonbackgrmd = 7
                }
                8 -> {
                    binding.relay.setBackgroundResource(R.color.egreylight)
                    buttonbackgrmd = 8
                }
                9 -> {
                    binding.relay.setBackgroundResource(R.color.egreenlight)
                    buttonbackgrmd = 9
                }


//                4-> {binding.edittextStatus.setTypeface(Typeface.SERIF)}
//
//                5-> {binding.edittextStatus.setTypeface(Typeface.DEFAULT)
                // Returns back to the top (Monospace)

            }

        }

//        var clicks = 0

        // When button is clicked
        binding.textfont.setOnClickListener {
            // Variable is incremented
            clicks += 1
            // Enters when loop and changes typeface   clicks = 0
            when (clicks) {
                1 -> {
                    binding.edittextStatus.setTypeface(Typeface.DEFAULT)
                    clicks = 1
                }

                2 -> {
                    binding.edittextStatus.setTypeface(Typeface.SANS_SERIF)
                    clicks = 2
                }

                3 -> {
                    binding.edittextStatus.setTypeface(Typeface.DEFAULT_BOLD)
                    clicks = 3
                }

                4 -> {
                    binding.edittextStatus.setTypeface(Typeface.SERIF)
                    clicks = 4
                }

                5 -> {
                    binding.edittextStatus.setTypeface(Typeface.MONOSPACE)
                    // Returns back to the top (Monospace)
                    clicks = 5
                }
            }

        }
        binding.edittextStatus.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (binding.edittextStatus.length() > 300) {
                    binding.edittextStatus.textSize = 18f
                } else if (binding.edittextStatus.length() < 300) {
                    binding.edittextStatus.textSize = 30f
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.sendpencilimage.setOnClickListener {

            if (binding.edittextStatus.text.toString() == "") {
                MyApp.getAppInstance().showToastMsg("Please Write ")
            } else {
                binding.emoji.visibility = View.GONE
                binding.textfont.visibility = View.GONE
                binding.changebackground.visibility = View.GONE
                binding.sendpencilimage.visibility = View.GONE
                binding.edittextStatus.isCursorVisible = false
                takeScreenShot(binding.relay)
            }

        }

        mainViewModel.responceCallBack.observe(
            requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.PENCIL_SAVE_STATUS -> {
//
                                    binding.edittextStatus.setText("")
//                                    getFragmentManager()?.popBackStack()

                                }


                                else -> {}
                            }
                        }

                    }


                    Status.LOADING -> {
//                        AppUtills.setProgressDialog(requireActivity())
                    }
                    Status.ERROR -> {
                        MyApp.getAppInstance().showToastMsg(it.message.toString())
                    }
                }
            })




        return binding.root

    }

    private fun pencilStatus(text: Editable?, buttonbackgrmd: Int, clicks: Int) {

        if (text.toString().equals("")) {
            MyApp.getAppInstance().showToastMsg("Please Write ")
        } else {

            mainViewModel.featchDataFromServerWithAuth(
                Gson().toJson(
                    TextStatusModel(model.username, true, clicks, buttonbackgrmd, text.toString())
                ), RequestApis.pencil_save_status, RequestApis.PENCIL_SAVE_STATUS
            )
        }

    }



    private fun takeScreenShot(view: View) {
        bitmap = ScreenshotUtil.getInstance().takeScreenshotForView(view)
        val path =  requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            .toString() + APP_TAG+"/"+"${System.currentTimeMillis()}.jpg"
        craetedFile = FileUtil.getInstance().storeBitmap(bitmap, path)
        try {
            //FOR GET IMAGE ACTUAL HEIGHT AND WIDTH
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(File(Uri.fromFile(craetedFile).path).absolutePath, options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val imageFile = File(path)
        uploadPhoto(imageFile)
    }

    private fun uploadPhoto(file: File) {
        AppUtills.setProgressDialog(requireContext())
        StatusPictureUpload().ProfilePhotoUpload(
            this@TextStatusFragment,
            model.username,
            false,
            0,
            0,
            "",
            file,
            requireActivity()
        )
    }

    private fun getScreenShot(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas)
        else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

    //Permissions Check
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSION_STORAGE = arrayOf<String>(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun verifyStoragePermission(activity: Activity?) {
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                PERMISSION_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun imageUploadCallBack(url: String) {
        ActivityFragment.ComingFromTextStatus = true
        requireActivity().onBackPressed()
    }

}