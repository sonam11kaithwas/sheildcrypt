package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentMyStatusBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.MyStatusAdapter
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.newgroup.NewGroupActivity
import com.advantal.shieldcrypt.tabs_pkg.mediastatus.MediaStoryFullActivity
import com.advantal.shieldcrypt.tabs_pkg.model.DataItem
import com.advantal.shieldcrypt.tabs_pkg.model.DeleteListStatusModel
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusModel
import com.advantal.shieldcrypt.tabs_pkg.model.MyStatusResponse
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap
import javax.inject.Inject


@AndroidEntryPoint
class MyStatusFragment : Fragment(), MyStatusAdapter.ItemClickListner {
    lateinit var binding: FragmentMyStatusBinding
    var adapter: MyStatusAdapter? = null
    var statuslist = ArrayList<MyStatusResponse>()
    @Inject
    lateinit var networkHelper: NetworkHelper
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStatusBinding.inflate(inflater, container, false)

//        binding.icBackarrow.setOnClickListener {
//            //  MyApp.getAppInstance().showToastMsg("test")
//            getFragmentManager()?.popBackStack()
////            Navigation.findNavController(it)
////                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)
//
//        }
        binding.icBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        myStatusList()

        mainViewModel.responceCallBack.observe(
            requireActivity(), Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.MY_STATUS_LIST_All -> {
                                    try {
                                        if (users!=null && users.data!=null){
                                            val listType =
                                                object : TypeToken<List<MyStatusResponse?>?>() {}.type

                                            adapter?.updateStatuslist(Gson().fromJson<List<MyStatusResponse>>(users.data, listType) as ArrayList<MyStatusResponse>)
                                        }
                                    } catch (e: Exception) {
                                    }
                                }

                                RequestApis.MY_STATUS_LISTDELETE -> {
                                    myStatusList()

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

        adapter = MyStatusAdapter(statuslist,this)


        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return binding.root
    }
    private fun myStatusList() {
//        mainViewModel.featchDataFromServerWithAuth(Gson().toJson(
//            MyStatusModel(model.username,"0",false)
//        ), RequestApis.my_status_list, RequestApis.MY_STATUS_LIST
//        )
        val hashMap: HashMap<String, Int> = HashMap<String, Int>()
        hashMap["userid"] = model.userid.toInt()
        mainViewModel.featchDataFromServerWithAuth(Gson().toJson(hashMap)
            , RequestApis.get_all_media_status_list, RequestApis.MY_STATUS_LIST_All
        )
    }

    override fun getItemSelected(invListItem: MyStatusResponse) {

        val intent = Intent (getActivity(), MediaStoryFullActivity::class.java)
        intent.putExtra("mystatusimage", invListItem.mediadetails)
        intent.putExtra("mymediastatusid", invListItem.mediastatusid.toString())
        intent.putExtra("count", invListItem.count.toString())
        activity?.startActivity(intent)

//        val bundle = Bundle()
//        bundle.putString("key_name",  mySelectedmedia.toString())
//        val myStatusFullPageViewFragment = MyStatusFullPageViewFragment()
//        myStatusFullPageViewFragment.setArguments(bundle)




//        myStatusDelete(invListItem.userid.toString())
    }

    fun myStatusDelete(deletestatus: String) {

        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(
                DeleteListStatusModel(deletestatus,model.username)
            ), RequestApis.my_status_list_delete, RequestApis.MY_STATUS_LISTDELETE
        )

    }

    override fun onResume() {
        super.onResume()
        myStatusList()
    }


}