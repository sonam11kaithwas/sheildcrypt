package com.advantal.shieldcrypt.meeting.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.ParseException
import com.advantal.shieldcrypt.databinding.ActivityMeetingDetailsBinding
import com.advantal.shieldcrypt.databinding.ActivityMeetingMainBinding
import com.advantal.shieldcrypt.meeting.adapter.MeetingMainAdapter
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.meeting.adapter.AddedAttenderShowAdapter
import com.advantal.shieldcrypt.meeting.adapter.AutocompleteCustomArrayAdapter
import com.advantal.shieldcrypt.meeting.adapter.CustomDropDownAdapter
import com.advantal.shieldcrypt.meeting.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.tabs_pkg.model.OthersStatusSaveModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


@AndroidEntryPoint
class MeetingDetailsActivity : AppCompatActivity(), AddedAttenderShowAdapter.ItemClickListner {
    lateinit var binding: ActivityMeetingDetailsBinding
    var Date = ""
    var adapter: MeetingMainAdapter? = null
    var invlistitem = ArrayList<DataItem>()
    var attenderAdapter: AutocompleteCustomArrayAdapter? = null
    var searchAttendersList = ArrayList<DataItemL>()
    var addedAttenderShowList = ArrayList<DataItemL>()
    var addedAttenderShowAdapter: AddedAttenderShowAdapter? = null
    var meetingId = 0
@Inject
  lateinit var networkHelper: NetworkHelper
    var isLoading = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var meetingid = ""
    var organizerId =0
    var meetingidfinal = ""
    var strSelectedMeetingObject = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val iin = intent
        val mtngid = iin.extras
        if (mtngid != null) {
            meetingid = mtngid["meetingid"] as String
//            meetingidfinal = meetingid.toString()
        }
        loadAddedAttenderShowAdapter()
        //getMeetingListbyId()
        listener()
        binding.ivBackButton.setOnClickListener{
            onBackPressed()
        }

        binding.editMeetingBtn.setOnClickListener {
            val intent = Intent (this, EditMeetingScheduleActivity::class.java)
            intent.putExtra("selectedMeetingObj", strSelectedMeetingObject)
            startActivity(intent)
        }

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.MEETING_DETAILS_VIEWBYID -> {
                                    if (it!=null && it.data!=null && it.data.data!=null){
                                        strSelectedMeetingObject = it.data.data
                                        var str = Gson().fromJson(it.data.data, Data::class.java)
                                        Log.e("onCreate: ",str.title.toString() )

                                        binding.meetingTitle.setText(str.title)
                                        val startdate = convertISOTimeToDate(str.start.toString())
                                        val enddate = convertISOTimeToDate(str.end.toString())

                                        meetingId = str.id!!

                                        binding.startDate.setText(startdate)
                                        binding.endDate.setText(enddate)
                                        binding.startTime.setText(str.start.toString().substring(11,16))
                                        binding.endTime.setText(str.end.toString().substring(11,16))
                                        binding.mtngRec.setText(str.recurrence?.name.toString())
                                        binding.mtngFor.setText(str.meetingfor.toString())
                                        binding.mtngFotValue.setText(str.meetingforvalue.toString())
                                        binding.mtngDesc.setText(str.description.toString())

                                        try {
                                            if (str!=null && str.organizer!=null){
                                                binding.txtOrganizerBy.setText(""+ str.organizer!!.username.toString())
                                                binding.txtOrganizerType.setText(""+ str.organizer!!.type.toString())
                                                organizerId = str.organizer!!.id!!
                                            }

                                            //added attender list
                                            if (str!=null && str.attenders!=null && str.attenders!!.size>0){
                                                addedAttenderShowList.clear()
                                                for (itemRow in str.attenders!!.indices){
                                                    if (str.attenders!![itemRow]?.user !=null && str.attenders!![itemRow]?.user?.id!! > 0){
                                                        addedAttenderShowList.add(DataItemL(
                                                            str.attenders!![itemRow]?.user?.username.toString(),
                                                            str.attenders!![itemRow]?.user?.firstName.toString(),
                                                            str.attenders!![itemRow]?.user?.lastName.toString(),
                                                            "",
                                                        "",
                                                        "",
                                                        false,
                                                            str.attenders!![itemRow]?.user?.id,
                                                        ""))
                                                    }
                                                }
                                            }
                                            addedAttenderShowAdapter?.notifyDataSetChanged()
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                                RequestApis.GET_MEETING_ATTENDER_QUERY_AUTO_TEXT -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        searchAttendersList.clear()
                                        val listType =
                                            object : TypeToken<List<DataItemL?>?>() {}.type
                                        val contactDataList: List<DataItemL> =
                                            Gson().fromJson<List<DataItemL>>(users.data.toString(), listType)
                                        searchAttendersList.addAll(contactDataList)
                                        createAttender()
                                    }
                                }

                                RequestApis.ADD_USER_IN_MEETING -> {
                                    getMeetingListbyId()
                                }
                                RequestApis.DELETE_USER_IN_MEETING -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        getMeetingListbyId()
                                    }
                                }
                                RequestApis.DELETE_MEETING -> {
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
    }

    private fun getAttenderOnAutoText(str: String) {
        mainViewModel.getDataFromAutoQueryServerWithoutAuth(
            ""+str, RequestApis.get_meeting_attender_query_autotext, RequestApis.GET_MEETING_ATTENDER_QUERY_AUTO_TEXT
        )
    }

    override fun onResume() {
        super.onResume()
        getMeetingListbyId()
    }

    private fun createAttender() {
        delayMethod()
    }

    // function defination
    fun delayMethod(){
        attenderAdapter = AutocompleteCustomArrayAdapter(this, R.layout.custom_spinner_item, searchAttendersList)
        binding.autoCompleteAttender.setAdapter(attenderAdapter)
        binding.autoCompleteAttender.setOnItemClickListener { parent, _, position, _ ->
            val item = attenderAdapter!!.getItem(position) as DataItemL?
            binding.autoCompleteAttender.setText("")
            if (item != null) {
                if (addedAttenderShowList!=null){
                    if (addedAttenderShowList.size==0){
                        createRequest(item)
                       // addedAttenderShowList.add(item)
                    } else{
                        var attenderAddStatus: Boolean = true
                        for (itemRow in addedAttenderShowList.indices){
                            if (addedAttenderShowList.get(itemRow).id==item.id){
                                attenderAddStatus = false
                                break
                            }
                        }
                        if (attenderAddStatus){
                            if (addedAttenderShowList.size>=6){
                                Toast.makeText(this@MeetingDetailsActivity, "You have can not add more then 6 attenders.", Toast.LENGTH_SHORT).show()
                            } else{
                                createRequest(item)
                                //addedAttenderShowList.add(item)
                            }
                        }
                    }
                }
            }
            addedAttenderShowAdapter?.notifyDataSetChanged()
            //loadAddedAttenderShowAdapter()
        }
    }

    private fun loadAddedAttenderShowAdapter(){
        binding.rvAddedAttenderList.layoutManager = LinearLayoutManager(this)
        addedAttenderShowAdapter = AddedAttenderShowAdapter("edit", addedAttenderShowList, this)
        binding.rvAddedAttenderList.adapter = addedAttenderShowAdapter
    }

    private fun listener(){
        binding.autoCompleteAttender.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                try {
                    getAttenderOnAutoText(s.toString())
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.saveMeetingBtn.setOnClickListener {
        }

        binding.deleteMeetingBtn.setOnClickListener {
            doDelete()
        }
    }

    private fun doDelete(){
        val jsonObject = SaveMeetingModel()
        jsonObject.id = meetingId
        Log.e("createRequest", "  -->> " + Gson().toJson(jsonObject))
        mainViewModel.deleteMeetingWithoutAuth(
            Gson().toJson(jsonObject),
            RequestApis.deleteMeeting, RequestApis.DELETE_MEETING
        )
    }

    private fun createRequest(item: DataItemL){
        val jsonObject = SaveMeetingModel()
        var jsonArray = ArrayList<SaveMeetingAttender>()
        val jsonObjectAttender = SaveMeetingAttender()
        jsonObjectAttender.attender_id = item.id!!
        jsonObjectAttender.admin = false
        jsonObjectAttender.attender_type = 2 //2 added by pramod
        jsonArray.add(jsonObjectAttender)
        jsonObject.attenders = jsonArray

        val jsonOrganizer = MeetingOrganizerModel()
        if (model.userid!=null && !model.userid.isEmpty()){
            jsonOrganizer.id = model.userid.toInt()
        } else{
            jsonOrganizer.id = organizerId
        }
        jsonOrganizer.type = 1
        jsonObject.organizer = jsonOrganizer

        jsonObject.id = meetingId
        Log.e("createRequest", "  -->> " + Gson().toJson(jsonObject))
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(jsonObject),
            RequestApis.addUserInMeeting, RequestApis.ADD_USER_IN_MEETING
        )
    }

    fun getMeetingListbyId() {
            mainViewModel.featchDataFromServerWithAuth(Gson().toJson(
                MeetingDetailsModel(meetingid)
            ), RequestApis.meeting_detail_viewbyId, RequestApis.MEETING_DETAILS_VIEWBYID
            )
    }
    fun convertISOTimeToDate(isoTime: String): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var convertedDate: Date? = null
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(isoTime)
            formattedDate = SimpleDateFormat("MMM dd").format(convertedDate)
//            formattedDate = SimpleDateFormat("MMM dd,yyyy").format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }

    override fun getItemSelected(invListItem: DataItemL) {
       // TODO("Not yet implemented")
    }

    override fun onDeleteClicked(invListItem: DataItemL) {
        createDeleteRequest(invListItem)
    }

    private fun createDeleteRequest(item: DataItemL){
        val jsonObject = DeleteMeetingModel()

        val jsonOrganizer = MeetingOrganizerModel()
        if (model.userid!=null && !model.userid.isEmpty()){
            jsonOrganizer.id = model.userid.toInt()
        } else{
            jsonOrganizer.id = organizerId
        }
        jsonOrganizer.type = 1
        jsonObject.organizer = jsonOrganizer

        jsonObject.id = meetingId
        jsonObject.userid = item.id!!
        Log.e("createRequest", "  -->> " + Gson().toJson(jsonObject))
        mainViewModel.featchDataFromServerWithAuth(
            Gson().toJson(jsonObject),
            RequestApis.deleteUserInMeeting, RequestApis.DELETE_USER_IN_MEETING
        )
    }
}