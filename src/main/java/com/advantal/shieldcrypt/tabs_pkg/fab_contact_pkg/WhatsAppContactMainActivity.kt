package com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.WhatsappContactActivityBinding

class WhatsAppContactMainActivity : AppCompatActivity() {
    lateinit var bindding: WhatsappContactActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whats_app_contact_main)
    }
}