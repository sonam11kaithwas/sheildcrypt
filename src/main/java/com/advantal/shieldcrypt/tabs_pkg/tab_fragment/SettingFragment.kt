package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.Config
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.auth_pkg.model.LoginResModel
import com.advantal.shieldcrypt.broadcasting.BroadcastingMainPage
import com.advantal.shieldcrypt.databinding.FragmentSettingBinding
import com.advantal.shieldcrypt.entities.Account
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.splash_scr_pkg.SplashScreenActivity
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.SettingsAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.change_pass_pkg.ChangePasswordActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.profilemodel.ProfileReqModel
import com.advantal.shieldcrypt.tabs_pkg.model.LogoutModel
import com.advantal.shieldcrypt.tabs_pkg.model.SettingsDataModel
import com.advantal.shieldcrypt.ui.XmppActivity
import com.advantal.shieldcrypt.utils.ThemeHelper
import com.advantal.shieldcrypt.utils_pkg.*
import com.advantal.shieldcrypt.xmpp.Jid
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import database.my_database_pkg.db_table.MyAppDataBase
import net.gotev.sipservice.SipServiceCommand

@AndroidEntryPoint
class SettingFragment : Fragment(), SettingsAdapter.ChatSelectCallBack {

    lateinit var binding: FragmentSettingBinding
    val mainViewModel: MainViewModel by viewModels()
    var loginModel = MySharedPreferences.getSharedprefInstance().getLoginData()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    private var activity: XmppActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_setting, container, false)
        binding = FragmentSettingBinding.inflate(inflater, container, false)


        val arrayList: ArrayList<SettingsDataModel> = arrayListOf<SettingsDataModel>()

        arrayList.add(SettingsDataModel("Profile", R.drawable.profile_ic, R.drawable.arrow_ic))
        arrayList.add(
            SettingsDataModel(
                getString(R.string.change_password),
                R.drawable.ic_change_password_icon,
                R.drawable.arrow_ic
            )
        )

//        arrayList.add(SettingsDataModel("Chats", R.drawable.chats, R.drawable.arrow_ic))

        arrayList.add(
            SettingsDataModel(
                "Broadcast Message", R.drawable.ic_baseline_message_24, R.drawable.arrow_ic
            )
        )

        arrayList.add(
            SettingsDataModel(
                "Notifications", R.drawable.notifications_ic, R.drawable.arrow_ic
            )
        )
        arrayList.add(
            SettingsDataModel(
                "Privacy & Security", R.drawable.ic_privacy_sec, R.drawable.arrow_ic
            )
        )
        arrayList.add(SettingsDataModel("Help", R.drawable.help_ic, R.drawable.arrow_ic))
        arrayList.add(SettingsDataModel("Dark Mode", R.drawable.ic_dark_mode, R.drawable.arrow_ic))
        arrayList.add(
            SettingsDataModel(
                getString(R.string.logout), R.drawable.ic_logout_icon, R.drawable.arrow_ic
            )
        )

        val adapter = SettingsAdapter(arrayList, this)

        // Setting the Adapter with the recyclerview
        binding.settingsRecyclerView.adapter = adapter
        binding.settingsRecyclerView.layoutManager = LinearLayoutManager(activity)
//        adapter.listener = this

        mainViewModel.responceCallBack.observe(requireActivity(), Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppUtills.closeProgressDialog()
                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.LOGOUT_REQ -> {

                                try {
                                    if (activity != null && activity?.xmppConnectionService != null) {
                                        activity?.xmppConnectionService?.logoutAndSave(true)
                                        activity?.xmppConnectionService?.deletedatabaseBackend()
                                        var accoutn = getSelectedAccount()
//                                        if (accoutn != null) activity?.xmppConnectionService?.deleteAccount(
//                                            accoutn
//                                        )
                                    }
                                } catch (e: Exception) {
                                }

                                MySharedPreferences.getSharedprefInstance()
                                    .setAutoLoginStatus(false)
                                clearLocalDetailsAfterLogout()
//                                (requireActivity() as TabLayoutFragment).stopService()
//                                (requireActivity() as TabLayoutFragment).stopService()
                                /*This line add by SONAM KAITHWAS*/
                                MySharedPreferences.getSharedprefInstance().setLoginData("")
                                try {
                                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                                        ?.contactDao()?.delete()
                                    MyAppDataBase.getUserDataBaseAppinstance(MyApp.getAppInstance())
                                        ?.groupDao()?.delete()


                                } catch (e: Exception) {
                                }
                                val intent =
                                    Intent(requireActivity(), SplashScreenActivity::class.java)
                                intent.putExtra("stpservice", "stop")
                                startActivity(intent)
                                requireActivity().finish()
                            }
                            RequestApis.VIEW_PROFILE_BY_ID_REQ -> {
                                model = Gson().fromJson(users.data, LoginResModel::class.java)
                                model.token =
                                    MySharedPreferences.getSharedprefInstance().getLoginData().token
                                MySharedPreferences.getSharedprefInstance()
                                    .setLoginData(Gson().toJson(model))

                                if (model.xmpip.toString() != null && !model.xmpip.toString()
                                        .equals("")
                                ) {
                                    MySharedPreferences.getSharedprefInstance()
                                        .setChatip(model.xmpip.toString())
                                }

                                binding.personName.text = model.firstName + " " + model.lastName
                                binding.msg.text = model.mobileNumber

                                try {
                                    if (model.profileUrl != null && !model.profileUrl.isEmpty()) {
                                        binding.progressBar.visibility = View.VISIBLE
                                        Glide.with(this).load(model.profileUrl)
                                            .placeholder(R.drawable.one_person)
                                            .listener(object : RequestListener<Drawable> {
                                                override fun onLoadFailed(
                                                    e: GlideException?,
                                                    model: Any?,
                                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.progressBar.visibility = View.GONE
                                                    return false
                                                }

                                                override fun onResourceReady(
                                                    resource: Drawable?,
                                                    model: Any?,
                                                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                                                    dataSource: DataSource?,
                                                    isFirstResource: Boolean
                                                ): Boolean {
                                                    binding.progressBar.visibility = View.GONE
                                                    return false
                                                }
                                            }).diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
                                            // .override(1000, 1000)
                                            .into(binding.img)
                                    } else {

                                    }
                                } catch (e: Exception) {
                                    e.message
                                }
                            }
                            else -> {}
                        }
                    }
                }
                Status.LOADING -> {
//                    AppUtills.setProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    MyApp.getAppInstance().showToastMsg(it.message.toString())
                }
            }
        })

        viewProfile()

        return binding.root

    }

    fun viewProfile() {
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                ProfileReqModel(
                    (MySharedPreferences.getSharedprefInstance().getLoginData().userid).toInt()
                )
            ), RequestApis.view_Profile_ById, RequestApis.VIEW_PROFILE_BY_ID_REQ
        )
    }

    override fun onResume() {
        super.onResume()
        try {
            if (MyObject.profileChangedStatus != null && MyObject.profileChangedStatus) {
                MyObject.profileChangedStatus = false
                viewProfile()
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } finally {

        }
    }

    override fun getSelectedUser(position: Int) {
        when (position) {
            0 -> {
                val intent = Intent(requireActivity(), ProfileActivity::class.java)
                startActivity(intent)
            }
            1 -> {
                val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            2 -> {
                val intent = Intent(requireActivity(), BroadcastingMainPage::class.java)
                startActivity(intent)
            }

            7-> {
                showLogout(AppConstants.ARE_YOU_SURE_YOU_WANT_LOGOUT)
            }
        }
    }

    private fun showLogout(msg: String) {
        AppUtills.setDialog(requireActivity(),
            msg,
            requireActivity().resources.getString(R.string.yes),
            object : CallbackAlertDialog {
                override fun onPossitiveCall() {
                    doLogout()
                }

                override fun onNegativeCall() {

                }
            })
    }

    private fun doLogout() {
        mainViewModel.featchDataFromServerWithoutAuth(
            Gson().toJson(
                LogoutModel(loginModel.username)
            ), RequestApis.logoutUser, RequestApis.LOGOUT_REQ
        )
    }

    fun clearLocalDetailsAfterLogout() {
        // remove account which is registered in the app
        try {
            SipServiceCommand.resetAccount(requireActivity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // mySharedPreferences.clearSharedPrefernces(activity)
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    object MyObject {
        @kotlin.jvm.JvmField
        var profileChangedStatus: Boolean = false
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("", "")
        if (context is XmppActivity) {
            Log.e("", "")
            this.activity = context
        } else {
            throw IllegalStateException("Trying to attach fragment to activity that is not an XmppActivity")
        }
    }

    fun getSelectedAccount(): Account? {
        return if (activity?.xmppConnectionService != null) {
            val jid: Jid
            jid = try {
                if (Config.DOMAIN_LOCK != null) {
                    Jid.ofEscaped(
                        MySharedPreferences.Companion.getSharedprefInstance()
                            .getLoginData().mobileNumber + "@" + MySharedPreferences.Companion.getSharedprefInstance()
                            .getXSip(), Config.DOMAIN_LOCK, null
                    )
                } else {
                    Jid.ofEscaped(
                        MySharedPreferences.Companion.getSharedprefInstance()
                            .getLoginData().mobileNumber + "@" + MySharedPreferences.Companion.getSharedprefInstance()
                            .getXSip()
                    )
                }
            } catch (e: IllegalArgumentException) {
                return null
            }
            val service = activity?.xmppConnectionService ?: return null
            service.findAccountByJid(jid)
        } else {
            null
        }
    }


}

