package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.advantal.shieldcrypt.databinding.ActivityFragmentBinding
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.MyStatusFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.TextStatusFragment
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.storie_view.ui.activity.MiStoryDisplayActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivityFragment : AppCompatActivity() {
    lateinit var binding: ActivityFragmentBinding
    var comingFrom = ""

    companion object {
        var ComingFromTextStatus = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        val comingExtra = intent.extras
        if (comingExtra != null) {
            comingFrom = (comingExtra["comingFrom"] as String?)!!
        }

        if (comingFrom == "TextStatusStory") {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(binding.flContainer.id, TextStatusFragment(), "TextStatusFragment")
            ft.commit()

        } else {
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            ft.replace(
                binding.flContainer.id,
                MyStatusFragment(),
                "MyStatusFragment"
            )
            ft.commit()

        }

    }

    override fun onBackPressed() {
        if (ComingFromTextStatus) {
            ComingFromTextStatus = false
            val intent =  Intent()
            intent.putExtra("comingFrom", "ActivityFragment")
            setResult(RESULT_OK, intent)
        }
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()

        } else {
            supportFragmentManager.popBackStack()
            super.onBackPressed()
        }
    }

}

