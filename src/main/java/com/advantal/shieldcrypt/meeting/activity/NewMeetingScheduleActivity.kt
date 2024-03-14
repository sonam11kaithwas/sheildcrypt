package com.advantal.shieldcrypt.meeting.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.advantal.shieldcrypt.databinding.ActivityNewMeetingScheduleBinding
import com.advantal.shieldcrypt.meeting.adapter.*
import com.advantal.shieldcrypt.meeting.model.DataItemL
import com.advantal.shieldcrypt.meeting.model.SaveMeetingAttender
import com.advantal.shieldcrypt.meeting.model.SaveMeetingModel
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule


@AndroidEntryPoint
class NewMeetingScheduleActivity : AppCompatActivity(),
    CustomDropDownAdapter.onSelectedItemClicked, AddedAttenderShowAdapter.ItemClickListner {
    lateinit var binding: ActivityNewMeetingScheduleBinding
    private val mainViewModel: MainViewModel by viewModels()
    var adapter: MeetingRecrAdapter? = null
    var startTimeSlot = ArrayList<String>()
    var statuslist = ArrayList<DataItemL>()
    var spnUnitData = ArrayList<DataItemL>()
    var spnLocationData = ArrayList<DataItemL>()
    var spnRecurrenceData = ArrayList<DataItemL>()
    var spnDesignationData = ArrayList<DataItemL>()
    var MeetingForStaticData = ArrayList<DataItemL>()
    var searchAttendersList = ArrayList<DataItemL>()
    var selectedStartDateLong: Long? = null
    var strRecurrenceId: Int = 0
    var strMeeetingFor: String = "Individual"
    var strMeeetingForValue: String = ""
    var attenderAdapter: AutocompleteCustomArrayAdapter? = null
    var selectedStartDatecCalendar = Calendar.getInstance()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var addedAttenderShowAdapter: AddedAttenderShowAdapter? = null
    var addedAttenderShowList = ArrayList<DataItemL>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewMeetingScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtStartDate.setFocusable(false)
        binding.edtStartDate.setClickable(true)
        binding.edtStartTime.setFocusable(false)
        binding.edtStartTime.setClickable(true)

        binding.edtEndDate.setFocusable(false)
        binding.edtEndDate.setClickable(true)
        binding.edtEndTime.setFocusable(false)
        binding.edtEndTime.setClickable(true)

        //delayMethod()
        createForMeetingFor()
        getmeetingrecur()
        /*getmeetingUnit()
        getmeetinglocation()
        getmeetingdesignation()*/
        listener()
        //loadAddedAttenderShowAdapter()

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.GET_MEETING_RECUR -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        spnRecurrenceData.clear()
                                        val listType =
                                            object : TypeToken<List<DataItemL?>?>() {}.type
                                        val contactDataList: List<DataItemL> =
                                            Gson().fromJson<List<DataItemL>>(users.data.toString(), listType)
                                        spnRecurrenceData.addAll(contactDataList)
                                        Log.e( "GET_MEETING_RECUR: ",contactDataList.toString() )
                                       // adapterMeetingRecurrence?.updateStatuslist(Gson().fromJson<List<DataItemL>>(users.data.toString(), listType) as ArrayList<DataItemL>)
                                        val adapterMeetingRecurrence = CustomDropDownAdapter(this, contactDataList)
                                        binding.spnMeetingRecurrence.adapter = adapterMeetingRecurrence
                                    }

                                    getmeetingUnit()
                                    getmeetinglocation()
                                    getmeetingdesignation()
                                }
                                RequestApis.GET_MEETING_LOCATION -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        spnLocationData.clear()
                                        val listType =
                                            object : TypeToken<List<DataItemL?>?>() {}.type
                                        val contactDataList: List<DataItemL> =
                                            Gson().fromJson<List<DataItemL>>(users.data.toString(), listType)
                                        spnLocationData.addAll(contactDataList)
                                        Log.e( "GET_MEETING_LOCATION: ",contactDataList.toString() )
                                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnLocationData)
                                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
                                    }
                                }
                                RequestApis.GET_MEETING_UNIT -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        spnUnitData.clear()
                                        val listType =
                                            object : TypeToken<List<DataItemL?>?>() {}.type
                                        val contactDataList: List<DataItemL> =
                                            Gson().fromJson<List<DataItemL>>(users.data.toString(), listType)
                                        spnUnitData.addAll(contactDataList)
                                        Log.e( "GET_MEETING_UNIT: ",contactDataList.toString() )

                                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnUnitData)
                                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
                                    }
                                }
                                RequestApis.GET_MEETING_DESIGNATION -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        spnDesignationData.clear()
                                        val listType =
                                            object : TypeToken<List<DataItemL?>?>() {}.type
                                        val contactDataList: List<DataItemL> =
                                            Gson().fromJson<List<DataItemL>>(users.data.toString(), listType)
                                        spnDesignationData.addAll(contactDataList)
                                        Log.e( "GET_MEETING_DESIGNATION: ",contactDataList.toString() )
                                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnDesignationData)
                                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
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
                                        Log.e( "GET_MEETING_ATTENDER_QUERY_AUTO_TEXT: ",contactDataList.toString())
                                        createAttender()
                                    }
                                }
                                RequestApis.SAVE_MEETING -> {
                                    if (users!=null && users.data!=null && users.data.length>0){
                                        onBackPressed()
                                    }
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

    override fun onResume() {
        super.onResume()
        getmeetingrecur()
        /*Timer().schedule(100) {
            getmeetingUnit()
            getmeetinglocation()
            getmeetingdesignation()
        }*/
    }

    private fun loadAddedAttenderShowAdapter(){
        binding.rvAddedAttenderList.layoutManager = LinearLayoutManager(this)
        addedAttenderShowAdapter = AddedAttenderShowAdapter("add", addedAttenderShowList,this)
        binding.rvAddedAttenderList.adapter = addedAttenderShowAdapter
    }

    private fun getAttenderOnAutoText(str: String) {
        mainViewModel.getDataFromAutoQueryServerWithoutAuth(
            ""+str, RequestApis.get_meeting_attender_query_autotext, RequestApis.GET_MEETING_ATTENDER_QUERY_AUTO_TEXT
        )
    }

    private fun createAttender() {
        delayMethod()
    }

    // function defination
    fun delayMethod(){
        attenderAdapter = AutocompleteCustomArrayAdapter(this, com.advantal.shieldcrypt.R.layout.custom_spinner_item, searchAttendersList)
        binding.autoCompleteAttender.setAdapter(attenderAdapter)
        binding.autoCompleteAttender.setOnItemClickListener { parent, _, position, _ ->
            val item = attenderAdapter!!.getItem(position) as DataItemL?
            binding.autoCompleteAttender.setText("")
            if (item != null) {
                if (addedAttenderShowList!=null){
                    if (addedAttenderShowList.size==0){
                        addedAttenderShowList.add(item)
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
                                Toast.makeText(this@NewMeetingScheduleActivity, "You have can not add more then 6 attenders.", Toast.LENGTH_SHORT).show()
                            } else{
                                addedAttenderShowList.add(item)
                            }
                        }
                    }
                }
            }
            //addedAttenderShowAdapter?.notifyDataSetChanged()
            loadAddedAttenderShowAdapter()
        }
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
            validation()
        }
        binding.ivBackButton.setOnClickListener{
            onBackPressed()
        }
        binding.spnMeetingRecurrence?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spnRecurrenceData!=null && spnRecurrenceData.size>0){
                    strRecurrenceId = spnRecurrenceData.get(position).id!!
                    Log.e("onItemSelected", "  11-> " + strRecurrenceId)
                }
            }
        }

        binding.spnChooseAnyOne?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (strMeeetingFor.toString().equals("Unit")){
                    if (spnUnitData!=null && spnUnitData.size>0){
                        Log.e("onItemSelected", "  spnUnitData-> " + spnUnitData.get(position).name)
                        strMeeetingForValue = spnUnitData.get(position).name.toString()
                    }
                } else if (strMeeetingFor.toString().equals("Location")){
                    if (spnLocationData!=null && spnLocationData.size>0){
                        Log.e("onItemSelected", "  spnLocationData-> " + spnLocationData.get(position).name)
                        strMeeetingForValue = spnLocationData.get(position).name.toString()
                    }
                } else if (strMeeetingFor.toString().equals("Designation")){
                    if (spnDesignationData!=null && spnDesignationData.size>0){
                        Log.e("onItemSelected", "  spnDesignationData-> " + spnDesignationData.get(position).name)
                        strMeeetingForValue = spnDesignationData.get(position).name.toString()
                    }
                }
            }
        }

        binding.spnMeetingFor?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                strMeeetingFor = MeetingForStaticData.get(position).name.toString()
                Log.e("onItemSelected", "  22-> " + strMeeetingFor)
                binding.llAttenderMainView.visibility = View.GONE
                if (MeetingForStaticData.get(position).name.toString().equals("Unit")){
                    binding.llChooseAnyOneMain.visibility = View.VISIBLE
                    if (spnUnitData!=null && spnUnitData.size>0){
                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnUnitData)
                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
                    } else{
                        getmeetingUnit()
                    }
                } else if (MeetingForStaticData.get(position).name.toString().equals("Location")){
                    binding.llChooseAnyOneMain.visibility = View.VISIBLE
                    if (spnLocationData!=null && spnLocationData.size>0){
                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnLocationData)
                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
                    } else{
                        getmeetinglocation()
                    }
                } else if (MeetingForStaticData.get(position).name.toString().equals("Designation")){
                    binding.llChooseAnyOneMain.visibility = View.VISIBLE
                    if (spnDesignationData!=null && spnDesignationData.size>0){
                        val adapterMeetingRecurrence = CustomDropDownAdapter(this@NewMeetingScheduleActivity, spnDesignationData)
                        binding.spnChooseAnyOne.adapter = adapterMeetingRecurrence
                    } else{
                        getmeetingdesignation()
                    }
                } else {
                    if (MeetingForStaticData.get(position).name.toString().equals("Individual")){
                        binding.llAttenderMainView.visibility = View.VISIBLE
                    }
                    binding.llChooseAnyOneMain.visibility = View.GONE
                }
            }
        }

        binding.edtStartDate.setOnClickListener{
            startDate()
        }

        binding.edtEndDate.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtStartTime.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
            } else{
                endDate()
            }
        }

        binding.edtStartTime.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else{
                startTimeDialog()
            }
        }

        binding.edtEndTime.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtStartTime.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtEndDate.text.toString().equals("")){
                Toast.makeText(this@NewMeetingScheduleActivity, "Please select end date.", Toast.LENGTH_SHORT).show()
            } else{
                endTimeDialog()
            }
        }
    }

    private fun validation(){
        if (binding.edtMeetingTitle.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please enter your meeting title.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtStartDate.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtStartTime.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtEndDate.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select end date.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtEndTime.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select end time.", Toast.LENGTH_SHORT).show()
        } else if (strRecurrenceId<=0){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select meeting recurrence.", Toast.LENGTH_SHORT).show()
        } else if (strMeeetingFor.toString().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select meeting for.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtDescription.text.toString().trim().equals("")){
            Toast.makeText(this@NewMeetingScheduleActivity, "Please select your description.", Toast.LENGTH_SHORT).show()
        } else{
            if (strMeeetingFor.equals("Individual")){
                strMeeetingForValue = ""
                if (addedAttenderShowList.size<=1){
                    Toast.makeText(this@NewMeetingScheduleActivity, "Please select at least two attenders.", Toast.LENGTH_SHORT).show()
                } else{
                   // Toast.makeText(this@NewMeetingScheduleActivity, "Success in case Individual", Toast.LENGTH_SHORT).show()
                    createRequest()
                }
            } else if (strMeeetingFor.equals("All User")){
                strMeeetingForValue = ""
              //  Toast.makeText(this@NewMeetingScheduleActivity, "Success in case All User", Toast.LENGTH_SHORT).show()
                createRequest()
            } else {
                if (strMeeetingForValue.toString().equals("")){
                    Toast.makeText(this@NewMeetingScheduleActivity, "Please select your meeting for value.", Toast.LENGTH_SHORT).show()
                } else{
                    //Toast.makeText(this@NewMeetingScheduleActivity, "Success in case All other value.", Toast.LENGTH_SHORT).show()
                    createRequest()
                }
            }
        }
    }

    private fun createRequest(){
        val jsonObject = SaveMeetingModel()


        jsonObject.title = binding.edtMeetingTitle.text.toString().trim()
        jsonObject.description = ""+binding.edtDescription.text.toString().trim()

        if (binding.edtStartDate.text.toString()!=null && binding.edtStartDate.text.toString().length>0){
            var strSelectedStartDate = binding.edtStartDate.text.toString().trim()
            val strs = strSelectedStartDate.split("-").toTypedArray()
            Log.e("newStrStartDate",  " -> " + strs.size + " -> " + strs[2]
                    + " -> " + strs[0]+ " -> " + strs[1])
            var newStrStartDate = strs[2]+"-"+strs[0]+"-"+strs[1]
            jsonObject.start = ""+newStrStartDate+" "+binding.edtStartTime.text.toString().trim()
        }
        if (binding.edtEndDate.text.toString()!=null && binding.edtEndDate.text.toString().length>0){
            var strSelectedStartDate = binding.edtEndDate.text.toString().trim()
            val strs = strSelectedStartDate.split("-").toTypedArray()
            Log.e("newStrStartDate",  " -> " + strs.size + " -> " + strs[2]
                    + " -> " + strs[0]+ " -> " + strs[1])
            var newStrStartDate = strs[2]+"-"+strs[0]+"-"+strs[1]
            jsonObject.end = ""+newStrStartDate+" "+binding.edtEndTime.text.toString().trim()
        }
        jsonObject.status = true
        if (strMeeetingFor.equals("All User")){
            jsonObject.allUserSelected = true
        } else{
            jsonObject.allUserSelected = false
        }
        jsonObject.meetingFor = ""+strMeeetingFor
        jsonObject.recurrence_id = ""+strRecurrenceId
        jsonObject.meetingForValue = ""+strMeeetingForValue

    //    val jsonArray = JSONArray()
        var jsonArray = ArrayList<SaveMeetingAttender>()
        val jsonObjectAttender = SaveMeetingAttender()
        jsonObjectAttender.attender_id = model.userid.toInt()//1
        jsonObjectAttender.admin = false
        jsonObjectAttender.attender_type = 1
        jsonArray.add(jsonObjectAttender)
        if (strMeeetingFor.equals("Individual")){
            if (addedAttenderShowList!=null && addedAttenderShowList.size>0){
                for (itemRow in addedAttenderShowList.indices){
                    val jsonObjectAttender = SaveMeetingAttender()
                    jsonObjectAttender.attender_id = addedAttenderShowList.get(itemRow).id!!
                    jsonObjectAttender.admin = false
                    jsonObjectAttender.attender_type = 2

                    jsonArray.add(jsonObjectAttender)
                }
            }
        }
        jsonObject.attenders = jsonArray
        mainViewModel.featchDataFromServerWithoutAuth(
            Gson().toJson(jsonObject), RequestApis.save_meeting, RequestApis.SAVE_MEETING
        )
    }

    private fun endTimeDialog(){
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your time")

        var getTimeSlot = ArrayList<String>()
        if (binding.edtEndDate.text.toString().equals(getCurrentData())){
            getTimeSlot = ArrayList<String>()
            getTimeSlot.clear()
            for (item in createTimeSlot().indices){
                if (binding.edtStartTime.text.toString()<createTimeSlot().get(item)){
                    getTimeSlot.add(createTimeSlot().get(item))
                }
            }
        } else {
            if (binding.edtStartDate.text.toString().equals(binding.edtEndDate.text.toString())){
                getTimeSlot = ArrayList<String>()
                getTimeSlot.clear()
                for (item in createTimeSlot().indices){
                    if (binding.edtStartTime.text.toString()<createTimeSlot().get(item)){
                        getTimeSlot.add(createTimeSlot().get(item))
                    }
                }
            } else {
                getTimeSlot = createTimeSlot()
            }
        }
        if (getTimeSlot!=null&& getTimeSlot.size>0){
            val times = arrayOfNulls<String>(getTimeSlot.size)
            for (item in getTimeSlot.indices){
                times[item] = getTimeSlot.get(item)
            }
            builder.setItems(times) { dialog, which ->
                binding.edtEndTime.setText(times[which])
            }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun startTimeDialog(){
        // setup the alert builder
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your time")

        var getTimeSlot = ArrayList<String>()
        if (binding.edtStartDate.text.toString().equals(getCurrentData())){
            getTimeSlot = ArrayList<String>()
            getTimeSlot.clear()
            for (item in createTimeSlot().indices){
                if (getCurrentTime()<createTimeSlot().get(item)){
                    getTimeSlot.add(createTimeSlot().get(item))
                }
            }
        } else {
            getTimeSlot = createTimeSlot()
        }
        if (getTimeSlot!=null&& getTimeSlot.size>0){
            val times = arrayOfNulls<String>(getTimeSlot.size)
            for (item in getTimeSlot.indices){
                times[item] = getTimeSlot.get(item)
            }
            builder.setItems(times) { dialog, which ->
                binding.edtStartTime.setText(times[which])

                binding.edtEndDate.setText("")
                binding.edtEndTime.setText("")
            }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun getCurrentData(): String{
        val c = Calendar.getInstance()
        val day = c[Calendar.DAY_OF_MONTH]
        val month = c[Calendar.MONTH]
        val year = c[Calendar.YEAR]
        //val date3 = ""+(month + 1) + "-" + day.toString() + "-" + year
        var strDayOfMonth = ""
        if (day<=9){
            strDayOfMonth = "0"+day.toString()
        } else {
            strDayOfMonth = day.toString()
        }
        var strMonthOfYear = ""
        if ((month+1)<=9){
            strMonthOfYear = "0"+(month + 1)
        } else {
            strMonthOfYear = ""+(month + 1)
        }
        val dat = (strMonthOfYear + "-" + strDayOfMonth + "-" + year)
        return dat
    }

    private fun getCurrentTime(): String{
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH:mm")
        val strDate = "" + mdformat.format(calendar.time)
        return (strDate+":00")
    }

    private fun endDate(){
        val c = Calendar.getInstance()
        val year = selectedStartDatecCalendar.get(Calendar.YEAR)
        val month = selectedStartDatecCalendar.get(Calendar.MONTH)
        val day = selectedStartDatecCalendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            var strDayOfMonth = ""

            if (dayOfMonth<=9){
                strDayOfMonth = "0"+dayOfMonth.toString()
            } else {
                strDayOfMonth = dayOfMonth.toString()
            }
            var strMonthOfYear = ""
            if ((monthOfYear+1)<=9){
                strMonthOfYear = "0"+(monthOfYear + 1)
            } else {
                strMonthOfYear = ""+(monthOfYear + 1)
            }
            val dat = (strMonthOfYear + "-" + strDayOfMonth + "-" + year)
            binding.edtEndDate.setText(""+dat)
            binding.edtEndTime.setText("")
        }, year, month, day)
        datePickerDialog.datePicker.minDate = selectedStartDatecCalendar.timeInMillis
        datePickerDialog.show()
    }

    private fun startDate(){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
            var strDayOfMonth = ""

            if (dayOfMonth<=9){
                strDayOfMonth = "0"+dayOfMonth.toString()
            } else {
                strDayOfMonth = dayOfMonth.toString()
            }
            var strMonthOfYear = ""
            if ((monthOfYear+1)<=9){
                strMonthOfYear = "0"+(monthOfYear + 1)
            } else {
                strMonthOfYear = ""+(monthOfYear + 1)
            }
            val dat = (strMonthOfYear + "-" + strDayOfMonth + "-" + year)
            binding.edtStartDate.setText(""+dat)

            binding.edtStartTime.setText("")
            binding.edtEndDate.setText("")
            binding.edtEndTime.setText("")

            selectedStartDatecCalendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth());
            selectedStartDatecCalendar.set(Calendar.MONTH, view.getMonth());
            selectedStartDatecCalendar.set(Calendar.YEAR, view.getYear());
        }, year, month, day)
        datePickerDialog.datePicker.minDate = Calendar.getInstance().getTime().time
        datePickerDialog.show()
    }

    private fun getmeetingUnit() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.getDataFromServerWithoutAuth(
            Gson().toJson(hashMap), RequestApis.get_meeting_unit, RequestApis.GET_MEETING_UNIT
        )
    }

    private fun getmeetinglocation() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.getDataFromServerWithoutAuth(
            Gson().toJson(hashMap), RequestApis.get_meeting_location, RequestApis.GET_MEETING_LOCATION
        )
    }

    private fun getmeetingrecur() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.getDataFromServerWithoutAuth(
            Gson().toJson(hashMap), RequestApis.get_meeting_recur, RequestApis.GET_MEETING_RECUR
        )
    }

    private fun getmeetingdesignation() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.getDataFromServerWithoutAuth(
            Gson().toJson(hashMap), RequestApis.get_meeting_designation, RequestApis.GET_MEETING_DESIGNATION
        )
    }

    private fun createForMeetingFor(){
        MeetingForStaticData.add(DataItemL("", "", "","", "", "Individual", false, 0, ""))
        MeetingForStaticData.add(DataItemL("", "", "","", "", "All User", false, 0, ""))
        MeetingForStaticData.add(DataItemL("", "", "","", "", "Unit", false, 0, ""))
        MeetingForStaticData.add(DataItemL("", "", "","", "", "Location", false, 0, ""))
        MeetingForStaticData.add(DataItemL("", "", "","", "", "Designation", false, 0, ""))
        val adapterMeetingRecurrence = CustomDropDownAdapter(this, MeetingForStaticData)
        binding.spnMeetingFor.adapter = adapterMeetingRecurrence
    }

    private fun createTimeSlot(): ArrayList<String> {
        var startTimeSlot = ArrayList<String>()
        startTimeSlot.add("00:00:00")
        startTimeSlot.add("00:30:00")
        startTimeSlot.add("01:00:00")
        startTimeSlot.add("01:30:00")
        startTimeSlot.add("02:00:00")
        startTimeSlot.add("02:30:00")
        startTimeSlot.add("03:00:00")
        startTimeSlot.add("03:30:00")
        startTimeSlot.add("04:00:00")
        startTimeSlot.add("04:30:00")

        startTimeSlot.add("05:00:00")
        startTimeSlot.add("05:30:00")
        startTimeSlot.add("06:00:00")
        startTimeSlot.add("06:30:00")
        startTimeSlot.add("07:00:00")
        startTimeSlot.add("07:30:00")
        startTimeSlot.add("08:00:00")
        startTimeSlot.add("08:30:00")
        startTimeSlot.add("09:00:00")
        startTimeSlot.add("09:30:00")

        startTimeSlot.add("10:00:00")
        startTimeSlot.add("10:30:00")
        startTimeSlot.add("11:00:00")
        startTimeSlot.add("11:30:00")
        startTimeSlot.add("12:00:00")
        startTimeSlot.add("12:30:00")
        startTimeSlot.add("13:00:00")
        startTimeSlot.add("13:30:00")
        startTimeSlot.add("14:00:00")
        startTimeSlot.add("14:30:00")

        startTimeSlot.add("15:00:00")
        startTimeSlot.add("15:30:00")
        startTimeSlot.add("16:00:00")
        startTimeSlot.add("16:30:00")
        startTimeSlot.add("17:00:00")
        startTimeSlot.add("17:30:00")
        startTimeSlot.add("18:00:00")
        startTimeSlot.add("18:30:00")
        startTimeSlot.add("19:00:00")
        startTimeSlot.add("19:30:00")

        startTimeSlot.add("20:00:00")
        startTimeSlot.add("20:30:00")
        startTimeSlot.add("21:00:00")
        startTimeSlot.add("21:30:00")
        startTimeSlot.add("22:00:00")
        startTimeSlot.add("22:30:00")
        startTimeSlot.add("23:00:00")
        startTimeSlot.add("23:30:00")

        return startTimeSlot
    }

    override fun onClick(item: DataItemL) {

    }

    override fun getItemSelected(invListItem: DataItemL) {
        //TODO("Not yet implemented")
    }

    override fun onDeleteClicked(invListItem: DataItemL) {
       // TODO("Not yet implemented")
    }
}