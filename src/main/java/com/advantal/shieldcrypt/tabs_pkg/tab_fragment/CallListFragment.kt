package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentCallListBinding
import com.advantal.shieldcrypt.listener.OnLoadMoreListener
import com.advantal.shieldcrypt.listener.RecyclerViewLoadMoreScroll
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.sip.utils.Utils
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.CallsAdpter
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.CallLogsResponse
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.ContentItem
import com.advantal.shieldcrypt.utils_pkg.*
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import net.gotev.sipservice.SipAccount
import net.gotev.sipservice.SipServiceCommand
import javax.inject.Inject


@AndroidEntryPoint
class CallListFragment : Fragment(), CallsAdpter.CallsSelectCallBack {
    lateinit var binding: FragmentCallListBinding
    var adapter: CallsAdpter?
    = null
    var contentlist: List<ContentItem> = listOf<ContentItem>()

@Inject
  lateinit var networkHelper: NetworkHelper
    var isLoading = true
    private val mainViewModel: MainViewModel by viewModels()
    var share: SharedPrefrence? = null
    var page = 0
    var size = 20
    private var scrollListener: RecyclerViewLoadMoreScroll? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_call_list, container, false)
        binding = FragmentCallListBinding.inflate(inflater, container, false)


        share = SharedPrefrence.getInstance(requireActivity())
        binding.pullRefreshLayout.setOnRefreshListener {
            page = 0
            loadMore()
            binding.pullRefreshLayout.isRefreshing = false
        }

        mainViewModel.responceCallBack.observe(requireActivity(), Observer {
            when (it.status) {

                Status.SUCCESS -> {
                    AppUtills.closeProgressDialog()

                    it.data?.let { users ->
                        when (users.requestCode) {
                            RequestApis.CALL_LOGS_APIS -> {
                                try {
                                    scrollListener!!.setLoaded()
                                    var callLogdResModel =
                                        Gson().fromJson(it.data.data, CallLogsResponse::class.java)
                                    Log.e("responce",it.data.data)
                                    if (it!=null && it.data!=null && it.data.data.length>0){
                                        contentlist = callLogdResModel.clmp!!
                                        setCallLogsData(contentlist, page)
                                    } else {
                                        isLoading = false
                                    }
                                    if (adapter!=null && adapter!!.itemCount>0){
                                        binding.msg.visibility = View.GONE
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            else -> {}
                        }
                    }

                }


                Status.LOADING -> {
                    AppUtills.setProgressDialog(requireActivity())
                }
                Status.ERROR -> {
                    MyApp.getAppInstance().showToastMsg(it.message.toString())
                }
            }
        })

        linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.callsRecyclerView.setLayoutManager(linearLayoutManager)

        page = 0
        isLoading = true
        loadMore()

        adapter = CallsAdpter(contentlist, this)


        binding.callsRecyclerView.adapter = adapter
       // binding.callsRecyclerView.layoutManager = LinearLayoutManager(activity)
        initScrollListener()
        return binding.root

    }

    private fun setCallLogsData(chatList: List<ContentItem>, page: Int) {
        adapter?.setTasks(chatList, page)
    }


    fun filtercallsList(query: String) {
        if (adapter != null) {
            if (!query.equals("")) adapter?.filter?.filter(query)
            else {

//                adapter?.setTasks(contentlist)
                page = 0
                size = 20
                loadMore()
            }
        }
    }

    private fun initScrollListener() {
        /*binding.callsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == (adapter?.itemCount
                            ?: 1) - 1) {
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })*/

        /*binding.callsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                        contentlist.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })*/

        scrollListener = RecyclerViewLoadMoreScroll(linearLayoutManager)
        scrollListener!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                if (isLoading) {
                    page++
                    loadMore()
                }
            }
        })
        binding.callsRecyclerView.addOnScrollListener(scrollListener!!)
    }


    fun loadMore() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()

        hashMap.put("page", page)
        hashMap.put("size", size)
        var callLogBeans=CallLogBeans(page,size, share!!.getValue(
            AppConstants.loggedInUserNumber))
        mainViewModel.featchDataFromServerWithoutAuth(
            Gson().toJson(callLogBeans), RequestApis.callLogs, RequestApis.CALL_LOGS_APIS
        )
    }

    override fun getSelectedUser() {
    }

    override fun clickedOnCall(selectedRow: ContentItem) {
        if (selectedRow!=null && selectedRow.src!=null && !selectedRow.src.isEmpty()){
            initiateCall(selectedRow.src)
            //showLogout(selectedRow.src)
        }
    }

    private fun showLogout(msg: String) {
        val items = arrayOf("Audio", "Video")
        val builder = AlertDialog.Builder(requireActivity())
        with(builder) {
            setTitle("Calling")
            setItems(items) { dialog, which ->
                if ( items[which].equals("Audio")){
                    initiateCall(msg)
                } else{

                }
            }

            setPositiveButton("OK", positiveButtonClick)
            show()
        }
    }

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(requireActivity(),
            android.R.string.no, Toast.LENGTH_SHORT).show()
    }

    data class CallLogBeans(var page:Int,var size:Int,var username:String)

    fun initiateCall(number: String) {
        Utils.hideKeyBoard(requireActivity())
        if (!Utils.checkInternetConn(requireActivity())) {
            Toast.makeText(
                requireActivity(), getString(R.string.internet_check), Toast.LENGTH_SHORT
            ).show()
        } else {
            if (checkValidation(number)) {
                if (SipAccount.activeCalls.size > 0) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.you_have_already_on_call),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onCall(number)
                }
            }
        }
    }

    fun checkValidation(number: String): Boolean {
        if (number.equals("", ignoreCase = true)) {
            Toast.makeText(
                requireActivity(), getString(R.string.please_enter_mobile), Toast.LENGTH_SHORT
            ).show()
            return false
        } else {
            return true
        }
    }

    fun onCall(number: String) {
        val id: String? = share?.getValue(SharedPrefrence.SIPACCOUNTID)
        AppConstants.TYPE = "CALL"
        /*if (number == null && number!!.isEmpty()) {
            number = "*9000"
        }*/
        Log.e("go id", "dsda $id")
        Log.e("c", "no $number")
        SipServiceCommand.makeCall(requireActivity(), id, number)
    }
}