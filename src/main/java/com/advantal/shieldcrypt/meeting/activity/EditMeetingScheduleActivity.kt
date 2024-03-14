package com.advantal.shieldcrypt.meeting.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.databinding.ActivityEditMeetingScheduleBinding
import com.advantal.shieldcrypt.meeting.adapter.*
import com.advantal.shieldcrypt.meeting.model.*
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditMeetingScheduleActivity : AppCompatActivity(),
    CustomDropDownAdapter.onSelectedItemClicked, AddedAttenderShowAdapter.ItemClickListner {
    lateinit var binding: ActivityEditMeetingScheduleBinding
    private val mainViewModel: MainViewModel by viewModels()
    var adapter: MeetingRecrAdapter? = null
    var statuslist = ArrayList<DataItemL>()
    var selectedStartDatecCalendar = Calendar.getInstance()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    var meetingObj = Data()
    var meetingId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditMeetingScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtStartDate.setFocusable(false)
        binding.edtStartDate.setClickable(true)
        binding.edtStartTime.setFocusable(false)
        binding.edtStartTime.setClickable(true)

        binding.edtEndDate.setFocusable(false)
        binding.edtEndDate.setClickable(true)
        binding.edtEndTime.setFocusable(false)
        binding.edtEndTime.setClickable(true)

        loadIntentData()
        listener()

        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.UPDATE_MEETING -> {
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

    fun loadIntentData(){
        var selectedMeetingObj: String? = intent.getStringExtra("selectedMeetingObj")
        if (selectedMeetingObj!=null && selectedMeetingObj.isNotEmpty()){
            meetingObj = Gson().fromJson(selectedMeetingObj, Data::class.java)
            Log.e("selectedMeetingObj", " selectedMeetingObj--> " + selectedMeetingObj)
            if (meetingObj!=null){

                meetingId = meetingObj.id!!
                if (meetingObj.title!=null && meetingObj.title.toString().isNotEmpty()){
                    binding.edtMeetingTitle.setText(""+meetingObj.title.toString())
                }
                if (meetingObj.description!=null && meetingObj.description.toString().isNotEmpty()){
                    binding.edtDescription.setText(""+meetingObj.description.toString())
                }
                if (meetingObj.start!=null && meetingObj.start.toString().isNotEmpty()){
                    val strMainDate = meetingObj.start.toString().split(" ").toTypedArray()
                    if (strMainDate!=null && strMainDate.size>0){
                        val strs = strMainDate[0].split("-").toTypedArray()
                        Log.e("newStrStartDate",  " start-> " + strs.size + " -> " + strs[2]
                                + " -> " + strs[0]+ " -> " + strs[1])
                        var newStrStartDate = strs[1]+"-"+strs[2]+"-"+strs[0]
                        binding.edtStartDate.setText(""+newStrStartDate)
                    }

                    if (strMainDate!=null && strMainDate.size>1){
                        binding.edtStartTime.setText(""+strMainDate[1].toString())
                    }
                }
                if (meetingObj.end!=null && meetingObj.end.toString().isNotEmpty()){
                    val strMainDate = meetingObj.end.toString().split(" ").toTypedArray()
                    if (strMainDate!=null && strMainDate.size>0){
                        val strs = strMainDate[0].split("-").toTypedArray()
                        Log.e("newStrStartDate",  " end-> " + strs.size + " -> " + strs[2]
                                + " -> " + strs[0]+ " -> " + strs[1])
                        var newStrStartDate = strs[1]+"-"+strs[2]+"-"+strs[0]
                        binding.edtEndDate.setText(""+newStrStartDate)
                    }

                    if (strMainDate!=null && strMainDate.size>1){
                        binding.edtEndTime.setText(""+strMainDate[1].toString())
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun listener(){
        binding.saveMeetingBtn.setOnClickListener {
            validation()
        }
        binding.ivBackButton.setOnClickListener{
            onBackPressed()
        }

        binding.edtStartDate.setOnClickListener{
            startDate()
        }

        binding.edtEndDate.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtStartTime.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
            } else{
                endDate()
            }
        }

        binding.edtStartTime.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else{
                startTimeDialog()
            }
        }

        binding.edtEndTime.setOnClickListener{
            if (binding.edtStartDate.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtStartTime.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
            } else if (binding.edtEndDate.text.toString().equals("")){
                Toast.makeText(this@EditMeetingScheduleActivity, "Please select end date.", Toast.LENGTH_SHORT).show()
            } else{
                endTimeDialog()
            }
        }
    }

    private fun validation(){
        if (binding.edtMeetingTitle.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please enter your meeting title.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtStartDate.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please select start date.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtStartTime.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please select start time.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtEndDate.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please select end date.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtEndTime.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please select end time.", Toast.LENGTH_SHORT).show()
        } else if (binding.edtDescription.text.toString().trim().equals("")){
            Toast.makeText(this@EditMeetingScheduleActivity, "Please select your description.", Toast.LENGTH_SHORT).show()
        } else{
            createRequest()
        }
    }

    private fun createRequest(){
        val jsonObject = SaveMeetingModel()
        try {
            jsonObject.id = meetingId
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

            val jsonObjectOrg = MeetingOrganizerModel()
            if (model.userid!=null && !model.userid.isEmpty()){
                jsonObjectOrg.id = model.userid.toInt()
            } else{
                jsonObjectOrg.id = meetingObj.organizer?.id!!
            }
            jsonObjectOrg.type = 1
            jsonObject.organizer = jsonObjectOrg
            Log.e("newStrStartDate",  " jsonObject-> " + Gson().toJson(jsonObject))
             mainViewModel.featchDataFromServerWithoutAuth(
                 Gson().toJson(jsonObject), RequestApis.updateMeeting, RequestApis.UPDATE_MEETING
             )
        } catch (e: NullPointerException){
            e.printStackTrace()
        }
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
        /*if (binding.edtStartDate.text.toString().equals(getCurrentData())){
            getTimeSlot = ArrayList<String>()
            getTimeSlot.clear()
            for (item in createTimeSlot().indices){
                if (getCurrentTime()<createTimeSlot().get(item)){
                    getTimeSlot.add(createTimeSlot().get(item))
                }
            }
        } else {
            getTimeSlot = createTimeSlot()
        }*/

        if (binding.edtStartTime.text.toString()!=null && !binding.edtStartTime.text.toString().isEmpty()){
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

            selectedStartDatecCalendar.set(Calendar.DAY_OF_MONTH, view.getDayOfMonth())
            selectedStartDatecCalendar.set(Calendar.MONTH, view.getMonth())
            selectedStartDatecCalendar.set(Calendar.YEAR, view.getYear())
        }, year, month, day)
        datePickerDialog.datePicker.minDate = Calendar.getInstance().getTime().time
        datePickerDialog.show()
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