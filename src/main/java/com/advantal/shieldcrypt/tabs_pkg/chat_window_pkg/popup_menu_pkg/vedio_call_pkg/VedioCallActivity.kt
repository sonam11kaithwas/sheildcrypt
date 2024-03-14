package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.popup_menu_pkg.vedio_call_pkg

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.ActivityVedioCallBinding
import com.advantal.shieldcrypt.utils_pkg.MyApp

class VedioCallActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityVedioCallBinding
    var VEDIOONOFF = false
    var CALLMUTE = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVedioCallBinding.inflate(layoutInflater)
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
            R.id.call_recieve_img -> {
                binding.vedioCallViewLayout.visibility = View.VISIBLE
                binding.recieveCallLayout.visibility = View.GONE
            }

            R.id.end_call_img -> {
                this.finish()
            }
            R.id.camera_rotate_button -> {
                 MyApp.getAppInstance().showToastMsg("Camera")
            }
            R.id.vedio_call_button -> {
                VEDIOONOFF = if (VEDIOONOFF) {
                    binding.vedioCallButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_vedio_call
                        )
                    )
                    false
                } else {
                    binding.vedioCallButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_mute_vedio_call
                        )
                    )
                    true
                }
            }
            R.id.mute_call_button -> {
                CALLMUTE = if (CALLMUTE) {
                    binding.muteCallButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_audio_call
                        )
                    )
                    false
                } else {
                    binding.muteCallButton.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_mute_audio_call
                        )
                    )
                    true
                }

            }

        }
    }

}