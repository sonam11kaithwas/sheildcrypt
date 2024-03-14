package com.advantal.shieldcrypt.meeting.activity

//import org.jitsi.meet.sdk.*
//import timber.log.Timber
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.databinding.ActivityMeetingMainBinding
import com.advantal.shieldcrypt.meeting.adapter.MeetingMainAdapter
import com.advantal.shieldcrypt.meeting.model.DataItem
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.network_pkg.RequestApis
import com.advantal.shieldcrypt.network_pkg.Status
import com.advantal.shieldcrypt.sip.SharedPrefrence
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.privacy_help_pkg.WebViewActivity
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.MyApp
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MeetingMainActivity : AppCompatActivity(),MeetingMainAdapter.ItemClickListner {

    lateinit var binding: ActivityMeetingMainBinding
    var Date = ""
    var adapter: MeetingMainAdapter? = null
    var invlistitem = ArrayList<DataItem>()
    var mySharedPreferences: SharedPrefrence? = null
@Inject
  lateinit var networkHelper: NetworkHelper
    var isLoading = false
    var meetingJoinedStatus = false
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeetingMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mySharedPreferences = SharedPrefrence.getInstance(this)
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val current = formatter.format(time)
       binding.idTVDate.setText(current)
//        initJitsi()
        binding.calendarView.setOnDateChangeListener(
            CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
               Date = (dayOfMonth.toString() + "-"
                        + (month + 1) + "-" + year)
                binding.idTVDate.setText(Date)
                val c = Calendar.getInstance()
                c[year, month] = dayOfMonth
                Log.e("itemListStatus", " 2222->> " + Date(c.timeInMillis) + " --> " + c.time)
                if (invlistitem!=null && invlistitem.size>0){
                    meetingScrollToCurrentDate(Date(c.timeInMillis), invlistitem)
                }
            })

        binding.cameraicon.setOnClickListener{
            val intent = Intent (this, NewMeetingScheduleActivity::class.java)

            startActivity(intent)
        }

        binding.icBackarrow.setOnClickListener {
            //  MyApp.getAppInstance().showToastMsg("test")
            onBackPressed()
//            Navigation.findNavController(it)
//                .navigate(R.id.action_profileinfoFragment_to_tabLayoutFragment)

        }



        mainViewModel.responceCallBack.observe(
            this, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { users ->
                            when (users.requestCode) {
                                RequestApis.GET_ALL_MEETING_LIST -> {

//                                    var str =
//                                        Gson().fromJson(it.data.data, MeetingMainResponse::class.java).data

                                    val listType =
                                        object : TypeToken<List<DataItem?>?>() {}.type
                                    val contactDataList: List<DataItem> =
                                        Gson().fromJson<List<DataItem>>(users.data.toString(), listType)

                                    if (invlistitem==null){
                                        invlistitem = ArrayList<DataItem>()
                                    }
                                    invlistitem.clear()
                                    invlistitem.addAll(contactDataList)

                                    adapter?.updateStatuslist(Gson().fromJson<List<DataItem>>(users.data.toString(), listType) as ArrayList<DataItem>)
                                    if (invlistitem!=null && invlistitem.size>0){
                                        meetingScrollToCurrentDate(Date(), invlistitem)
                                    }

                                }

                                RequestApis.CUSTOM_STATUS_SAVE -> {

                                    MyApp.getAppInstance().showToastMsg(it.data.toString())

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


        adapter = MeetingMainAdapter(invlistitem,this)


        binding.meetinglist.adapter = adapter
        binding.meetinglist.layoutManager = LinearLayoutManager(this)
    }

    private fun meetingScrollToCurrentDate(currentDate: Date, itemListStatus: ArrayList<DataItem>){
        try {
            for (i in itemListStatus.indices) {
                if (itemListStatus.get(i).start!=null && itemListStatus.get(i).start.toString().isNotEmpty()){
                    var selectedDate =  stringToDate(itemListStatus.get(i).start.toString())
                    if (currentDate.equals(selectedDate) ||
                        currentDate.before(selectedDate)){
                        Log.e("itemListStatus ", " 1111--> " + selectedDate)
                        if (i>0){
                            if (itemListStatus.size==(i-1)){
                                binding.meetinglist.scrollToPosition(i-1)
                                return
                            } else{
                                binding.meetinglist.scrollToPosition(i)
                                return
                            }
                        }
                    }
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun compareMeetingDate(strSelectedDate: String){

    }

    private fun stringToDate(dtStart: String): Date{
        var date = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            if (dtStart!=null && dtStart.isNotEmpty()){
                date = format.parse(dtStart)
                Log.e("itemListStatus ", " date--> " +date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }

    private fun getCurrentDate() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        try {
            val date = Date()
            val dateTime = dateFormat.format(date)
            Log.e("itemListStatus ", " CurrentDateTime--> " +dateTime +  " date-> " +date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    fun getAllMeetinglist() {
        var hashMap: HashMap<String, Int> = HashMap<String, Int>()
        mainViewModel.callGetApiWithToken(
            Gson().toJson(hashMap), RequestApis.get_all_meeting_list, RequestApis.GET_ALL_MEETING_LIST
        )
    }

    override fun onResume() {
        getAllMeetinglist()
        super.onResume()
    }

    override fun getItemSelected(invListItem: DataItem) {

        val intent = Intent (this, MeetingDetailsActivity::class.java)
        intent.putExtra("meetingid", invListItem.id.toString())
        Log.e("getItemSelected: ", invListItem.id.toString())
        startActivity(intent)
    }

    override fun onJoinMeetingClicked(invListItem: DataItem) {
        if (!meetingJoinedStatus){
           // doJoinRoom(invListItem)
            /*val intent = Intent (this, AddNewMeetingActivity::class.java)
            startActivity(intent)*/

            openConferenceWebView(invListItem)

          /*  val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://92.204.128.15:7443/ofmeet/abc"))
            startActivity(intent)*/
        } else{
            Toast.makeText(this, "You have already in meeting.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openConferenceWebView(invListItem: DataItem) {
        if (invListItem!=null && invListItem.roomName!=null && invListItem.roomName.length > 0) {
            /*var jitsiMeetUserInfo = JitsiMeetUserInfo()
            jitsiMeetUserInfo.displayName = ""+mySharedPreferences!!.getValue("loginUserFirstName")+" "+
                    ""+mySharedPreferences!!.getValue("loginUserLastName")*/
            val intent = Intent (this, WebViewActivity::class.java)
            intent.putExtra("selectedRoomName", ""+invListItem.roomName)
            intent.putExtra("meetingBaseUrl", ""+RequestApis.meetingBaseUrl)
            startActivity(intent)
        }
    }

/*
    private fun doJoinRoom(invListItem: DataItem){
        if (invListItem!=null && invListItem.roomName!=null && invListItem.roomName.length > 0) {
            //initJitsi()
            // Build options object for joining the conference. The SDK will merge the default
            // one we set earlier and this one when joining.
            */
/* JitsiMeetView jitsiMeetView = new JitsiMeetView(this);
            JitsiMeetActivity jitsiMeetActivity = new JitsiMeetActivity();
            JitsiMeetUserInfo jitsiMeetUserInfo = new JitsiMeetUserInfo();*//*

            var jitsiMeetUserInfo = JitsiMeetUserInfo()
            jitsiMeetUserInfo.displayName = ""+mySharedPreferences!!.getValue("loginUserFirstName")+" "+
                    ""+mySharedPreferences!!.getValue("loginUserLastName")
            val serverURL: URL
            serverURL = try {
               // URL("https://meet.jit.si")
                URL("https://92.204.128.15:7443/ofmeet/")
               // URL("https://92.204.128.15:7443")
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                throw RuntimeException("Invalid server URL!")
            }
          //  serverURL.openConnection()
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(*/
/*"https://92.204.128.15:7443/ofmeet/"+*//*
invListItem.roomName) // Settings for audio and video
                .setUserInfo(jitsiMeetUserInfo)
                .setServerURL(serverURL)
                //.setToken("eyJraWQiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIvYmQ2YjUyLVNBTVBMRV9BUFAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJqaXRzaSIsImlzcyI6ImNoYXQiLCJpYXQiOjE2NzE2MDkyNTEsImV4cCI6MTY3MTYxNjQ1MSwibmJmIjoxNjcxNjA5MjQ2LCJzdWIiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIiLCJjb250ZXh0Ijp7ImZlYXR1cmVzIjp7ImxpdmVzdHJlYW1pbmciOnRydWUsIm91dGJvdW5kLWNhbGwiOnRydWUsInNpcC1vdXRib3VuZC1jYWxsIjpmYWxzZSwidHJhbnNjcmlwdGlvbiI6dHJ1ZSwicmVjb3JkaW5nIjp0cnVlfSwidXNlciI6eyJoaWRkZW4tZnJvbS1yZWNvcmRlciI6ZmFsc2UsIm1vZGVyYXRvciI6dHJ1ZSwibmFtZSI6ImFydmluZC5tYWxpIiwiaWQiOiJhdXRoMHw2M2EyYmIyMWQ3YWZiM2Q1ZDNjYjJiYjIiLCJhdmF0YXIiOiIiLCJlbWFpbCI6ImFydmluZC5tYWxpQGFkdmFudGFsLm5ldCJ9fSwicm9vbSI6IioifQ.PTe2DResBe_WDgMnNshvLtT6T8SV8fj0BBjTKyBofme_sgRvAgDxPHiWNZYcMn8BMRBiua-Y8vu-gj904zG1aLUUWQVb3Hhx51WrznnI0D9a4FY5KGub1ienIfdORMi4HetyhKB-uhrQarfPQVYOOfqcsB3YfhAKIPgwfP_wIyLiEN6TeVkWEGfzseoyYpdnGg60mkmgvUdO4Z3JikV9HT8GA2oqMFYc082gvXc1tFuBVgOm03pLM5AtqQQO4UTnWQoAGE52Qz6VMYcbaQWRxIxwny4Sbs2PQk8TmoZwiIYSrzW_wDFSDFMkiITffg-Eb2E0KG6GXgborm8sTh6vdg")
                //.setAudioMuted(true)
                //.setVideoMuted(true)
                .build()
            // Launch the new activity with the given options. The launch() method takes care
            // of creating the required Intent and passing the options.
            JitsiMeetActivity.launch(this, options)
        }
    }
*/

/*
    private fun initJitsi(){
        // Initialize default options for Jitsi Meet conferences.
        val serverURL: URL
        serverURL = try {
           // URL("https://meet.jit.si")
            URL("https://92.204.128.15:7443/ofmeet/")
            //URL("https://92.204.128.15:7443")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
       // serverURL.openConnection()
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL) // When using JaaS, set the obtained JWT here
            // Different features flags can be set
            // .setFeatureFlag("toolbox.enabled", false)
            // .setFeatureFlag("filmstrip.enabled", false)
          //  .setFeatureFlag("pip.enabled", false) // <- this line you have to add

           // .setToken("eyJraWQiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIvYmQ2YjUyLVNBTVBMRV9BUFAiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJqaXRzaSIsImlzcyI6ImNoYXQiLCJpYXQiOjE2NzE2MDkyNTEsImV4cCI6MTY3MTYxNjQ1MSwibmJmIjoxNjcxNjA5MjQ2LCJzdWIiOiJ2cGFhcy1tYWdpYy1jb29raWUtZjYyNTA5OTBiNTgxNDdkMzliZDUwMWJiODkwZDBmZTIiLCJjb250ZXh0Ijp7ImZlYXR1cmVzIjp7ImxpdmVzdHJlYW1pbmciOnRydWUsIm91dGJvdW5kLWNhbGwiOnRydWUsInNpcC1vdXRib3VuZC1jYWxsIjpmYWxzZSwidHJhbnNjcmlwdGlvbiI6dHJ1ZSwicmVjb3JkaW5nIjp0cnVlfSwidXNlciI6eyJoaWRkZW4tZnJvbS1yZWNvcmRlciI6ZmFsc2UsIm1vZGVyYXRvciI6dHJ1ZSwibmFtZSI6ImFydmluZC5tYWxpIiwiaWQiOiJhdXRoMHw2M2EyYmIyMWQ3YWZiM2Q1ZDNjYjJiYjIiLCJhdmF0YXIiOiIiLCJlbWFpbCI6ImFydmluZC5tYWxpQGFkdmFudGFsLm5ldCJ9fSwicm9vbSI6IioifQ.PTe2DResBe_WDgMnNshvLtT6T8SV8fj0BBjTKyBofme_sgRvAgDxPHiWNZYcMn8BMRBiua-Y8vu-gj904zG1aLUUWQVb3Hhx51WrznnI0D9a4FY5KGub1ienIfdORMi4HetyhKB-uhrQarfPQVYOOfqcsB3YfhAKIPgwfP_wIyLiEN6TeVkWEGfzseoyYpdnGg60mkmgvUdO4Z3JikV9HT8GA2oqMFYc082gvXc1tFuBVgOm03pLM5AtqQQO4UTnWQoAGE52Qz6VMYcbaQWRxIxwny4Sbs2PQk8TmoZwiIYSrzW_wDFSDFMkiITffg-Eb2E0KG6GXgborm8sTh6vdg")
            .setFeatureFlag("calendar.enabled", false) // optional
           // .setFeatureFlag("call-integration.enabled", false) // optional
            .setFeatureFlag("calendar.enabled", false)
           // .setFeatureFlag("call-integration.enabled", false)
          //  .setFeatureFlag("close-captions.enabled", false)
            .setFeatureFlag("chat.enabled", true)
            .setFeatureFlag("invite.enabled", false)
            .setFeatureFlag("live-streaming.enabled", false)
            .setFeatureFlag("meeting-name.enabled", false)
            .setFeatureFlag("meeting-password.enabled", false)
            .setFeatureFlag("raise-hand.enabled", false)
            .setFeatureFlag("video-share.enabled", false)
            .setFeatureFlag("welcomepage.enabled", false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)

        registerForBroadcastMessages()

    }
*/

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

/*
    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        */
/* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         *//*
for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }
*/

    // Example for handling different JitsiMeetSDK events
/*
    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.CONFERENCE_JOINED ->{
                    meetingJoinedStatus = true
                     Timber.i(
                     "Conference Joined with url%s",
                     event.data["url"]
                 )
                }
                BroadcastEvent.Type.PARTICIPANT_JOINED -> Timber.i(
                    "Participant joined%s",
                    event.data["name"]
                )
                BroadcastEvent.Type.CONFERENCE_TERMINATED ->{
                    meetingJoinedStatus = false
                    Timber.i(
                        "Participant joined%s",
                        event.data["name"])
                }
                else -> {}
            }
        }
    }
*/

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            onBroadcastReceived(intent)
        }
    }
}