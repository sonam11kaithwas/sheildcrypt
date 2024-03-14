package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.audio_pkg

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityAudioBinding
import com.advantal.shieldcrypt.databinding.MenuSearchLayoutBinding
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.ChatsActivity
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.audio_pkg.audio_model.AudioModel
import com.advantal.shieldcrypt.utils_pkg.AppUtills
import com.advantal.shieldcrypt.utils_pkg.CallbackAlertDialog
import com.advantal.shieldcrypt.utils_pkg.MyApp

class AudioActivity : AppCompatActivity(), AudioListAdpter.AudioSelected, View.OnClickListener {
    lateinit var binding: ActivityAudioBinding
    lateinit var menuSearchBindding: MenuSearchLayoutBinding
    var adapter: AudioListAdpter? = null
    var audioList = ArrayList<AudioModel>()
    var mMediaPlayer: MediaPlayer? = null
    var tempPlayAudio: String? = null

    // 1. Plays the water sound
//    https://codersguidebook.com/how-to-create-an-android-app/play-sounds-music-android-app
    fun playSound(uri: Uri) {
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, uri)
            mMediaPlayer!!.isLooping = true
            mMediaPlayer!!.start()
        } else mMediaPlayer!!.start()
    }


    // 2. Pause playback
    fun pauseSound() {
        if (mMediaPlayer?.isPlaying == true) mMediaPlayer?.pause()
    }

    // 3. Stops playback
    fun stopSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer!!.stop()
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }

    // 4. Destroys the MediaPlayer instance when the app is closed
    override fun onStop() {
        super.onStop()
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioBinding.inflate(layoutInflater)
        menuSearchBindding = MenuSearchLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        updateACtionBar()
        getAllAudioFromDevice()
        setDataInViews()
    }

    private fun updateACtionBar() {

        if (adapter != null && (adapter?.selectedItemPositionList?.size)!! > 0) {

            setSupportActionBar(binding.searchLayout.toolbar)
            supportActionBar?.title =
                adapter?.selectedItemPositionList?.size.toString() + " Selected"

            binding.searchLayout.txtSend.visibility = View.VISIBLE

            binding.searchLayout.searchView.visibility = View.GONE
            binding.searchLayout.threeDotsImg.visibility = View.GONE

        } else {

            setSupportActionBar(binding.searchLayout.toolbar)

            supportActionBar?.title = getString(R.string.audio_title)
            binding.searchLayout.searchView.visibility = View.VISIBLE
            binding.searchLayout.threeDotsImg.visibility = View.VISIBLE
            binding.searchLayout.txtSend.visibility = View.GONE
        }


    }

    private fun setDataInViews() {
        adapter = AudioListAdpter(applicationContext, audioList, this)

        binding.recycleAudio.layoutManager = LinearLayoutManager(this)
        binding.recycleAudio.adapter = adapter


        binding.searchLayout.searchEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtercallsList(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

    }

    fun filtercallsList(query: String) {
        adapter?.filter?.filter(query)
    }

    private fun getAllAudioFromDevice(): List<AudioModel> {
        Log.e("Call", "Call")
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
//            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE
        )

        var cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )


        if (cursor != null) {
            while (cursor.moveToNext()) {
                Log.e("Name :" + cursor.getString(0), "")
                Log.e("PATHS :" + cursor.getString(3), "")
                var exte: String
                var fileSizeKb = cursor.getString(6).toInt() / 1024
                exte = if (fileSizeKb < 1024) {
                    " KB"
                } else {
                    fileSizeKb /= 1024
                    " MB"
                }

                var audioTime: String
                val dur = cursor.getString(5).toInt()
                val hrs = dur / 3600000
                val mns = dur / 60000 % 60000
                val scs = dur % 60000 / 1000



                audioTime = if (hrs > 0) {
                    String.format("%02d:%02d:%02d", hrs, mns, scs)
                } else {
                    String.format("%02d:%02d", mns, scs)
                }

                audioList.add(
                    AudioModel(
                        cursor.getString(0)//, "", "", ""
                        ,
                        cursor.getString(1),
                        cursor.getString(2),
                        "$fileSizeKb$exte",
                        cursor.getString(4),
                        audioTime, cursor.getString(3), "0"
                    )
                )


            }
            cursor.close()

        }
        return audioList
    }

    override fun getSelectMultipleAudio(listSelected: ArrayList<String>) {
        updateACtionBar()
    }

    override fun playAudio(playAudio: String, pauseResume: String) {

        when (pauseResume) {
            "1" -> {
                stopSound()
                playSound(Uri.parse(playAudio))
            }
            "0" -> {
                pauseSound()
            }

            else -> {

            }
        }


    }

    private fun hideShowTitleAndSearchBar(type: Int) {
        when (type) {
            1 -> {
                if (binding.searchLayout.searchLay.visibility == View.VISIBLE) {
                    binding.searchLayout.searchLay.visibility = View.GONE
                    binding.searchLayout.menuLayout.visibility = View.VISIBLE
                } else {
                    if (binding.searchLayout.txtSend.visibility == View.VISIBLE) {
                        adapter?.selectedItemPositionList?.clear()
                        adapter?.notifyDataSetChanged()
                        getSelectMultipleAudio(ArrayList())
                    } else {
                        this.finish()
                    }
                }
            }
            2 -> {
                binding.searchLayout.searchLay.visibility = View.GONE
                binding.searchLayout.menuLayout.visibility = View.VISIBLE
            }
            3 -> {
                binding.searchLayout.searchLay.visibility = View.VISIBLE
                binding.searchLayout.menuLayout.visibility = View.GONE
            }
        }

    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.txt_send -> AppUtills.setDialog(this, resources.getString(R.string.dialog_msg),
                resources.getString(R.string.ok),
                object : CallbackAlertDialog {
                    override fun onPossitiveCall() {

                    }

                    override fun onNegativeCall() {

                    }
                })
            R.id.three_dots_img -> {
                 MyApp.getAppInstance().showToastMsg("Click")
            }
            R.id.search_view -> {
                hideShowTitleAndSearchBar(3)
            }
            R.id.search_back -> {
                hideShowTitleAndSearchBar(2)
            }
            R.id.ic_backarrow -> {
                hideShowTitleAndSearchBar(1)
            }

        }

    }

    override fun onBackPressed() {
        binding.searchLayout.searchEdt.setText("")

        if (binding.searchLayout.searchLay.visibility == View.VISIBLE) {
            hideShowTitleAndSearchBar(1)
        } else {
            var intent = Intent(this@AudioActivity, ChatsActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }


}





