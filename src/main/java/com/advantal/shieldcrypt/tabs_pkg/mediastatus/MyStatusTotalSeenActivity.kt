package com.advantal.shieldcrypt.tabs_pkg.mediastatus

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.databinding.ActivityMyStatusTotalSeenBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.MyStatusViewbyAdapter
import com.advantal.shieldcrypt.tabs_pkg.model.DataItemStatus
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MyStatusTotalSeenActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyStatusTotalSeenBinding
    @Inject
    lateinit var networkHelper: NetworkHelper
    var adapter: MyStatusViewbyAdapter? = null
    var statuslist = ArrayList<DataItemStatus>()
    var isLoading = false
    var mediastatusid = ""
    var count = ""
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStatusTotalSeenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val iin = intent
        val b = iin.extras

        if (b != null) {
            mediastatusid = b["mystatusid"] as String
            count = (b["count"] as String?).toString()
        }

        binding.icBack.setOnClickListener {
            onBackPressed()
        }
        binding.toolbar.setText("Viewed By    " + count)
        myviewStatusList()



        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.MY_STATUS_LIST -> {
//
                                    if (users!=null && users.data!=null) {
                                        try {
                                            val listType =
                                                object : TypeToken<List<DataItemStatus?>?>() {}.type
                                            val contactDataList: List<DataItemStatus> =
                                                Gson().fromJson<List<DataItemStatus>>(
                                                    users.data.toString(),
                                                    listType
                                                )

//                                    var str =
//                                        Gson().fromJson(it.data.data, MyStatusResponse::class.java).data
                                            Log.e("prashantlal: ", contactDataList.size.toString())

                                            adapter?.updateStatuslist(
                                                Gson().fromJson<List<DataItemStatus>>(
                                                    users.data.toString(),
                                                    listType
                                                ) as ArrayList<DataItemStatus>
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                    else{

                                    }

                                }

                                RequestApis.MY_STATUS_LISTDELETE -> {

                                    onBackPressed()

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

        adapter = MyStatusViewbyAdapter(statuslist)
        binding.viewStatusRecycler.adapter = adapter
        binding.viewStatusRecycler.layoutManager = LinearLayoutManager(this)


    }

    fun myviewStatusList() {

        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                MyStatusModel(model.username,mediastatusid,true)
            ), RequestApis.my_status_list, RequestApis.MY_STATUS_LIST
        )
    }
//    override fun onResume() {
//        super.onResume()
//        Timer().schedule(object : TimerTask() {
//            override fun run() {
//                TODO("Do something")
//            }
//        }, 2000)
//        // put your code here...
//    }
}