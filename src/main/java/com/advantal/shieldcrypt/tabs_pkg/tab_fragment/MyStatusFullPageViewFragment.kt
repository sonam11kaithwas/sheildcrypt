package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advantal.shieldcrypt.databinding.FragmentMyStatusFullPageViewBinding
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences


class MyStatusFullPageViewFragment : Fragment() {
   lateinit var binding:FragmentMyStatusFullPageViewBinding
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()
//    var mystatuslist = MySharedPreferences.getSharedprefInstance().getMyStatuslist()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStatusFullPageViewBinding.inflate(inflater, container, false)

        val bundle = this.arguments

        if (bundle != null) {

           var value2 = bundle.getString("key_name", "")
            Log.e("onCreateView: ", value2 )
        }


        return binding.root
    }


}