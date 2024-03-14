package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.databinding.ActivityProfileAboutBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.Data
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.InvListItem
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.ProfileAboutResponse
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profileaboutmodel.ProfileAboutStatusModel
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profileaboutstatus_pkg.profilestatusaboutadapter.ProfileAboutStatusAdapter
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.ContentItem
import com.advantal.shieldcrypt.utils_pkg.AppConstants
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notify
import javax.inject.Inject

@AndroidEntryPoint
class ProfileAboutActivity : AppCompatActivity(), ProfileAboutStatusAdapter.ItemClickListner {

    lateinit var binding: ActivityProfileAboutBinding
    var adapter: ProfileAboutStatusAdapter? = null
    var invlistitem = ArrayList<InvListItem>()
    var statuslist: List<InvListItem> = listOf<InvListItem>()
    var str = ""
    var myprofileabout = ""

    @Inject
    lateinit var networkHelper: NetworkHelper
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.saveStatus.setOnClickListener {
//            onCustomSavestatus()
//        }

        binding.icBackarrow.setOnClickListener {


            val intent =  Intent()
            intent.putExtra("aboutprofile",  binding.customStatusEdt.text.toString())
//            Log.e("onCreatePrashant: ", binding.customStatusEdt.text.toString() )
            setResult(RESULT_OK, intent)
            onBackPressed()
//            Navigation.findNavController(it)
//                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)

        }

        binding.customStatusEdt.setOnKeyListener { v, keyCode, event ->

            when {

                //Check if it is the Enter-Key,      Check if the Enter Key was pressed down
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {


                    //perform an action here e.g. a send message button click
                    onCustomSavestatus(0)

                    //return true
                    return@setOnKeyListener true
                }
                else -> false
            }


        }

        defaultStatuslist()

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.DEFAULT_STATUS_LIST -> {

                                    var statusAboutResModel = Gson().fromJson(it.data.data, Data::class.java)
                                    var strFinalList = ArrayList<InvListItem>()
                                    Log.e("responce",it.data.data)
                                    if (it!=null && it.data!=null && it.data.data.length>0){
                                        strFinalList.addAll(statusAboutResModel.invList!!)
                                        adapter?.updateStatuslist(strFinalList)
                                    } else {

                                    }
////                                    var statusAboutResModel = Gson().fromJson(it.data.data, Data::class.java)
//
//                                    var strFinalList = ArrayList<InvListItem>()
////                                    invlistitem = statusAboutResModel.data?.invList!!
//                                    try {
//                                        if (it!=null && it.data!=null && it.data.data.length>0){
//                                            var str = Gson().fromJson(it.data.data, Data::class.java).invList
//                                            var strDef = Gson().fromJson(it.data.data, Data::class.java).defaultstatus
//                                            if (str != null && str.size > 0) {
////                                            Log.e("statusAboutResModel: ", str[0].name.toString())
//                                                AppConstants.MYPROFILESTATUS =  str[0].name.toString()
//                                                //  myprofileabout = str[0].name.toString()
//                                                binding.customStatusEdt.setText(str[0].name.toString())
//
//                                                strFinalList.addAll(str)
//                                            } else {
//                                                MyApp.getAppInstance().showToastMsg("Data not found")
//                                            }
//                                            if (strDef!=null && strDef.size>0){
//                                                strFinalList.addAll(strDef)
//                                            }
//                                            adapter?.updateStatuslist(strFinalList)
//                                        }
//                                    } catch (e: Exception) {
//                                    }
//////
////                                    var str =
////                                        Gson().fromJson(it.data.data, Data::class.java).invList
//
////
////
////
//                                    /*adapter?.updateStatuslist(Gson().fromJson(it.data.data, Data::class.java).invList
//                                    )*/
//
//

                                }

                                RequestApis.CUSTOM_STATUS_SAVE -> {

                                    defaultStatuslist()

//                                    MyApp.getAppInstance().showToastMsg(it.data.toString())

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




//        initScrollListener()
        adapter = ProfileAboutStatusAdapter(invlistitem, this)


        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)


    }


//    fun defaultStatuslist() {
////        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
//        mainViewModel.featchDataFromServerWithAuth(
//            Gson().toJson(AboutProfileModel(model.userid.toString().toInt())),
//            RequestApis.default_status_list,
//            RequestApis.DEFAULT_STATUS_LIST
//        )
//    }

    private fun defaultStatuslist() {
        mainViewModel.getDataFromAutoQueryServerWithAuth(
            model.userid.toString().toInt(), RequestApis.default_status_list, RequestApis.DEFAULT_STATUS_LIST
        )
    }

    fun onCustomSavestatus(id: Int) {
        mainViewModel.featchDataFromServerWithAuth(

            Gson().toJson(
                ProfileAboutStatusModel(
                    id,
                    binding.customStatusEdt.text.toString(),model.userid
                )
            ), RequestApis.custon_status_save, RequestApis.CUSTOM_STATUS_SAVE
        )
    }

    override fun getItemSelected(invListItem: InvListItem, apiLoginStatus: Int) {
//        MyApp.getAppInstance().showToastMsg("sonam")
        Log.e("getItemSelected: ", invListItem.defaultstatus?.name.toString())
        binding.customStatusEdt.setText(invListItem.defaultstatus?.name.toString())

        if (apiLoginStatus==2){
            invListItem.id?.let { onCustomSavestatus(it) }
        }
//        adapter?.notifyDataSetChanged()



    }

    override fun onBackPressed()
    {
        val intent =  Intent()
        intent.putExtra("aboutprofile",  binding.customStatusEdt.text.toString())
        setResult(RESULT_OK, intent)
        this.finish()
    }






}