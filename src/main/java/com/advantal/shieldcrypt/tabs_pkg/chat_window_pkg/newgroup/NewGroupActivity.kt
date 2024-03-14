package com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.newgroup

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.advantal.shieldcrypt.databinding.ActivityNewGroupBinding
import com.advantal.shieldcrypt.auth_pkg.AuthViewModelFactory


class NewGroupActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding = ActivityNewGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val iin = intent
//        val b = iin.extras
//
//        if (b != null) {
//            val j = b["mystatusimage"] as String?
////            Textv.setText(j)
//            Glide.with(this)
//                .load(j)
//                .into(binding.i)
//
//            j?.let { Log.e( "onCreateprashant: ", it) }
//        }


    }
}