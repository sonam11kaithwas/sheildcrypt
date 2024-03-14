package com.advantal.shieldcrypt.tabs_pkg.mediastatus

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.R
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.databinding.ActivityMediaStoryFullBinding
import com.advantal.shieldcrypt.databinding.ActivityOthersMediaStoryFullBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.model.DataItem
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusModel
import com.advantal.shieldcrypt.tabs_pkg.model.OthersStatusSaveModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class OthersMediaStoryFullActivity : AppCompatActivity() {

    lateinit var binding: ActivityOthersMediaStoryFullBinding
@Inject
  lateinit var networkHelper: NetworkHelper
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var othersmediausername = ""
    var othersmediaid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOthersMediaStoryFullBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val iin = intent
        val othrstatus = iin.extras
        if (othrstatus != null) {
            val otherstatusview = othrstatus["otherstatusimage"] as String?
            othersmediaid = othrstatus["othermediastatusid"] as String
            othersmediausername = othrstatus["otherMediausername"] as String
            val penciltext = othrstatus["penciltext"] as String?
            val background = othrstatus["background"] as String?
            val colour = othrstatus["font"] as String?
//            Textv.setText(j)

            if (otherstatusview.equals("")) {

                binding.mystatusimg.visibility = View.GONE
                binding.mystatutext.visibility = View.VISIBLE
                binding.mystatutext.setText(penciltext)
                binding.mystatutext.setBackgroundColor(Color.BLUE)

                val backgroundcolor = background.toString()
                val font = colour.toString()
                if (backgroundcolor.equals("1")) {
                    binding.mystatutext.setBackgroundResource(R.color.colorGreyBlue)
                } else if (backgroundcolor.equals("2")) {
                    binding.mystatutext.setBackgroundResource(R.color.eblue)
                } else if (backgroundcolor.equals("3")) {
                    binding.mystatutext.setBackgroundResource(R.color.eorange)
                } else if (backgroundcolor.equals("4")) {
                    binding.mystatutext.setBackgroundResource(R.color.edarkgreen)
                } else if (backgroundcolor.equals("5")) {
                    binding.mystatutext.setBackgroundResource(R.color.elightgreen)
                } else if (backgroundcolor.equals("6")) {
                    binding.mystatutext.setBackgroundResource(R.color.black_clr)
                } else if (backgroundcolor.equals("7")) {
                    binding.mystatutext.setBackgroundResource(R.color.enormalblue)
                } else if (backgroundcolor.equals("8")) {
                    binding.mystatutext.setBackgroundResource(R.color.egreylight)
                } else if (backgroundcolor.equals("9")) {

                    binding.mystatutext.setBackgroundResource(R.color.egreenlight)
                } else {

                }

                if (font.equals("1")) {
                    binding.mystatutext.setTypeface(Typeface.DEFAULT)
                } else if (font.equals("2")) {
                    binding.mystatutext.setTypeface(Typeface.SANS_SERIF)
                } else if (font.equals("3")) {
                    binding.mystatutext.setTypeface(Typeface.DEFAULT_BOLD)
                } else if (font.equals("4")) {
                    binding.mystatutext.setTypeface(Typeface.SERIF)
                } else if (font.equals("5")) {
                    binding.mystatutext.setTypeface(Typeface.MONOSPACE)
                } else {

                }

            }
            else{
                //            Textv.setText(j)
                binding.mystatusimg.visibility = View.VISIBLE
                binding.mystatutext.visibility = View.GONE
                Glide.with(this)
                    .load(otherstatusview)
                    .into(binding.mystatusimg)

                otherstatusview?.let { Log.e( "onCreateprashant: ", it) }
                othersmediaid?.let { Log.e( "othermediastatusid: ", it) }
                othersmediausername?.let { Log.e( "otherMediausername: ", it) }
            }



        }

        otherviewStatusList()

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.OTHER_SAVE_STATUS -> {
//

//                                    val listType =
//                                        object : TypeToken<List<DataItem?>?>() {}.type
//                                    val contactDataList: List<DataItem> =
//                                        Gson().fromJson<List<DataItem>>(users.data.toString(), listType)
//
////                                    var str =
////                                        Gson().fromJson(it.data.data, MyStatusResponse::class.java).data
//                                    Log.e("prashantlal: ", contactDataList.size.toString())
//                                    binding.viewseenby.setText(contactDataList.size.toString())


                                }



                                else -> {}
                            }
                        }

                    }


                    Status.LOADING -> {
                        AppUtills.setProgressDialog(this)
                    }
                    Status.ERROR -> {
                        MyApp.getAppInstance().showToastMsg(it.message.toString())
                    }
                }
            })

        binding.viewlay.setOnClickListener{
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_send_msg, null)
            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

    }

    fun otherviewStatusList() {

        mainViewModel.featchDataFromServerWithAuth(Gson().toJson(
            OthersStatusSaveModel(othersmediausername,othersmediaid,true)
        ), RequestApis.other_save_status, RequestApis.OTHER_SAVE_STATUS
        )
    }
}