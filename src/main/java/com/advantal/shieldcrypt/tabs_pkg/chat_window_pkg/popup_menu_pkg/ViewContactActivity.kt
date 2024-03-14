package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityViewContactBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel
import com.advantal.shieldcrypt.tabs_pkg.model.ViewContactBlocknUnblockModel
import com.advantal.shieldcrypt.utils_pkg.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase
import net.gotev.sipservice.SipAccount
import net.gotev.sipservice.SipServiceCommand
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ViewContactActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityViewContactBinding
    lateinit var myDataModel: ContactDataModel
    val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var userId = ""
    var number: String? = ""
    var share: SharedPrefrence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        share = SharedPrefrence.getInstance(this)
        var strUser: String? = intent.getStringExtra("chatUser") // 2
        var myDataModel = Gson().fromJson(strUser, ContactDataModel::class.java)
        userId = myDataModel.id.toString()
        number = myDataModel.mobileNumber.toString()


        val dto: ContactDataModel =
            MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.getAppInstance())!!
                .contactDao()
                .getDataModel(userId.toInt())

        binding.nameEdt.text = dto.contactName
        binding.phoneNoView.text = dto.mobileNumber
        binding.createdByView.text = changeDateFormatString(dto.createdBy)
        if (dto.blocked) {
            binding.blockText.text = "Block"

        } else {
            binding.blockText.text = "Unblock"
        }



        binding.blockView.setOnClickListener {
            if (dto.blocked) {
                showLogout(AppConstants.ARE_YOU_SURE_YOU_WANT_BLOCK)
            } else {
                doBlock(true)
            }
        }




        if (myDataModel.image != null && !myDataModel.image.isEmpty()) {
            Glide.with(this)
                .load(myDataModel.image)
                .placeholder(R.drawable.one_person)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
//                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
//                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.userImg)
        }

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.BLOCK_N_UBLOCK -> {
                                    val v =
                                        Gson().fromJson(it.data.data, ContactDataModel::class.java)
                                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                                        ?.contactDao()?.update(v.blocked, v.id)

                                    if (v.blocked) {
                                        binding.blockText.text = "Block"

                                    } else {
                                        binding.blockText.text = "Unblock"
                                    }

                                }
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

    }

    private fun showCustomPopUp() {
        var mypopupWindow: PopupWindow
        val inflater: LayoutInflater =
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        val view: ViewGroup =
            inflater.inflate(
                com.advantal.shieldcrypt.R.layout.popup_window_view_contact,
                null,
                false
            ) as ViewGroup


        mypopupWindow = PopupWindow(
            view,
            300,
//            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        )


//        mypopupWindow.showAtLocation(
//            binding.threeDotsImg, Gravity.TOP,
//            320, 220
//        )


        var edit_lable = view.findViewById(com.advantal.shieldcrypt.R.id.edit_lable) as TextView
        var share_lable = view.findViewById(com.advantal.shieldcrypt.R.id.share_lable) as TextView


        edit_lable.setOnClickListener {
            MyApp.getAppInstance().showToastMsg("Edit")
            mypopupWindow.dismiss()
        }

        share_lable.setOnClickListener {
            MyApp.getAppInstance().showToastMsg("Share")

            mypopupWindow.dismiss()
        }

    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                //this.finish()
                onBackPressed()
            }
            R.id.chat_lay -> {

                onBackPressed()

            }
            R.id.audio_lay -> {

                initiateCall()
            }
            R.id.vedio_lay -> {
                if (checkValidation()) {
                    if (SipAccount.activeCalls.size > 0) {
                        Toast.makeText(
                            this@ViewContactActivity,
                            getString(R.string.you_have_already_on_call),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onVideoCall()
                    }
                }
            }

            R.id.three_dots_img -> {
                showCustomPopUp()
            }
        }
    }

    fun onVideoCall() {
        AppConstants.TYPE = "CALL"
        val id: String? = share?.getValue(SharedPrefrence.SIPACCOUNTID)
        if (number!!.isEmpty()) {
            number = "*9000"
        }
        SipServiceCommand.makeCall(this@ViewContactActivity, id, number, true, false)
    }

    fun initiateCall() {
        Utils.hideKeyBoard(this)
        if (!Utils.checkInternetConn(this)) {
            Toast.makeText(
                this@ViewContactActivity, getString(R.string.internet_check), Toast.LENGTH_SHORT
            ).show()
        } else {
            if (checkValidation()) {
                if (SipAccount.activeCalls.size > 0) {
                    Toast.makeText(
                        this@ViewContactActivity,
                        getString(R.string.you_have_already_on_call),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onCall()
                }
            }
        }
    }

    fun checkValidation(): Boolean {
        if (number.equals("", ignoreCase = true)) {
            Toast.makeText(
                this@ViewContactActivity,
                getString(R.string.please_enter_mobile),
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            return true
        }
    }

    fun onCall() {
        val id: String? = share?.getValue(SharedPrefrence.SIPACCOUNTID)
        AppConstants.TYPE = "CALL"
        if (number == null && number!!.isEmpty()) {
            number = "*9000"
        }
        Log.e("go id", "dsda $id")
        Log.e("c", "no $number")
        SipServiceCommand.makeCall(this, id, number)
    }

    private fun showLogout(msg: String) {
        AppUtills.setDialog(
            this,
            msg, resources.getString(R.string.yes),
            object : CallbackAlertDialog {
                override fun onPossitiveCall() {
                    if (binding.blockText.text.toString().equals("Block", ignoreCase = true)) {
                        doBlock(false)
                    }
//                    doBlock()

                }

                override fun onNegativeCall() {

                }
            })
    }


    private fun doBlock(b: Boolean) {
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                ViewContactBlocknUnblockModel(
                    userId, b
                )
            ), RequestApis.blocknunlock, RequestApis.BLOCK_N_UBLOCK
        )
    }

    private fun doUnBlock() {
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                ViewContactBlocknUnblockModel(
                    userId, false
                )
            ), RequestApis.blocknunlock, RequestApis.BLOCK_N_UBLOCK
        )
    }

    private fun changeDateFormatString(strCurrentDate: String): String {
        var format = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val newDate: Date = format.parse(strCurrentDate)
        format = SimpleDateFormat("MMM dd,yyyy")
        val date: String = format.format(newDate)
        return date
    }
}