package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg.audio_call_pkg

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityAudioCallBinding

class AudioCallActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityAudioCallBinding
    var SPEAKERONOFF = false
    var CALLMUTE = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioCallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListners()
    }

    private fun clickListners() {
        binding.toolbarBar.icBackarrow.setOnClickListener(this)
        binding.toolbarBar.toolbar.text = "Privacy & Policy"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                this.finish()
            }
            R.id.end_call_img -> {
                this.finish()
            }
            R.id.mute_img -> {
                CALLMUTE = if (CALLMUTE) {
                    binding.speakerImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_audio_call
                        )
                    )
                    false
                } else {
                    binding.speakerImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_mute_audio_call
                        )
                    )
                    true
                }

            }
            R.id.speaker_img -> {
                SPEAKERONOFF = if (SPEAKERONOFF) {
                    binding.speakerImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_speaker_on
                        )
                    )
                    false
                } else {
                    binding.speakerImg.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_speaker_off
                        )
                    )
                    true
                }
            }
            R.id.ic_backarrow -> {
                this.finish()
            }


        }
    }

}