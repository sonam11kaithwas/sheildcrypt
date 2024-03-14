package com.advantal.shieldcrypt.tabs_pkg.mediastatus

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityMediaStoryFullBinding
import com.advantal.shieldcrypt.databinding.ActivityNewGroupBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.GetAllMediaStatusAdapter
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.MyStatusViewbyAdapter
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.WhatsAppContactsActivity
import com.advantal.shieldcrypt.tabs_pkg.model.*
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MediaStoryFullActivity : AppCompatActivity() {

    lateinit var binding: ActivityMediaStoryFullBinding

    @Inject
    lateinit var networkHelper: NetworkHelper
    var adapter: MyStatusViewbyAdapter? = null
    var statuslist = ArrayList<DataItemStatus>()
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var mediastatusid = ""
    var mediaimage = ""
    var count = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaStoryFullBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val iin = intent
        val b = iin.extras
        if (b != null) {
            mediastatusid = b["mymediastatusid"] as String
            val mediaimage = b["mystatusimage"] as String?
            count = (b["count"] as String?).toString()
            if (mediaimage.equals(null)) {
                binding.mystatusimg.visibility = View.GONE
                binding.mystatutext.visibility = View.VISIBLE
                binding.mystatutext.setBackgroundColor(Color.BLUE)
            } else {
                binding.mystatusimg.visibility = View.VISIBLE
                binding.mystatutext.visibility = View.GONE
                binding.viewseenby.text = count
                Glide.with(this)
                    .load(mediaimage)
                    .into(binding.mystatusimg)
//                mediaimage?.let { Log.e("onCreateprashant: ", it) }
            }

        }

//        myviewStatusList()

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
//                                RequestApis.MY_STATUS_LIST -> {
////
//
//                                    try {
//                                        val listType =
//                                            object : TypeToken<List<DataItemStatus?>?>() {}.type
//                                        val contactDataList: List<DataItemStatus> =
//                                            Gson().fromJson<List<DataItemStatus>>(
//                                                users.data.toString(),
//                                                listType
//                                            )
//
////                                    var str =
////                                        Gson().fromJson(it.data.data, MyStatusResponse::class.java).data
//                                        Log.e("prashantlal: ", contactDataList.size.toString())
//                                        binding.viewseenby.setText(contactDataList.size.toString())
//
//                                        seen = contactDataList.size.toString()
//                                    } catch (e: Exception) {
//                                    }
//
////                                    adapter?.updateStatuslist(Gson().fromJson<List<DataItemStatus>>(users.data.toString(), listType) as ArrayList<DataItemStatus>)
//
//
//                                }

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

        binding.viewlay.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_view_status, null)

//            adapter = MyStatusViewbyAdapter(statuslist)
//            val getAllStatusRecyclerView = view.findViewById<RecyclerView>(R.id.viewStatusRecycler)
//            getAllStatusRecyclerView.adapter = adapter
//            getAllStatusRecyclerView.layoutManager = LinearLayoutManager(this)

            val viewedby = view.findViewById<LinearLayout>(R.id.linview)
            val deleteby = view.findViewById<LinearLayout>(R.id.lindelete)
            val shareby = view.findViewById<LinearLayout>(R.id.linshare)
            val noofviewed = view.findViewById<TextView>(R.id.noofviewed)
            noofviewed.setText(count)


            viewedby.setOnClickListener {

                if(count> 0.toString()){
                    val intent = Intent(this, MyStatusTotalSeenActivity::class.java)
                    intent.putExtra("mystatusid", mediastatusid)
                    intent.putExtra("count", count.toString())
                    this?.startActivity(intent)
                }
                else{

                }
            }




            deleteby.setOnClickListener {
                myStatusDelete(mediastatusid)
            }

            shareby.setOnClickListener {
                val intent = Intent(this, WhatsAppContactsActivity::class.java)
                intent.putExtra("mystatusimageid", mediaimage)
                this?.startActivity(intent)
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()


        }

    }


    fun myviewStatusList() {

        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                MyStatusModel(model.username, mediastatusid, true)
            ), RequestApis.my_status_list, RequestApis.MY_STATUS_LIST
        )
    }

    fun myStatusDelete(deletestatus: String) {

        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                DeleteListStatusModel(deletestatus, model.username)
            ), RequestApis.my_status_list_delete, RequestApis.MY_STATUS_LISTDELETE
        )

    }
}