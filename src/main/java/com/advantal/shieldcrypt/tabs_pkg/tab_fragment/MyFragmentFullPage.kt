package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentMyFullPageBinding
import com.advantal.shieldcrypt.databinding.FragmentMyStatusBinding
import com.advantal.shieldcrypt.databinding.FragmentStatusBinding
import com.advantal.shieldcrypt.network_pkg.MainViewModel
import com.advantal.shieldcrypt.network_pkg.NetworkHelper
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.MyStatusViewbyAdapter
import com.advantal.shieldcrypt.tabs_pkg.model.DataItemStatus
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFragmentFullPage : Fragment() {


    lateinit var binding: FragmentMyFullPageBinding
@Inject
  lateinit var networkHelper: NetworkHelper
    var adapter: MyStatusViewbyAdapter? = null
    var statuslist = ArrayList<DataItemStatus>()
    var isLoading = false
    var mediastatusid = ""
    private val mainViewModel: MainViewModel by viewModels()
    var model = MySharedPreferences.getSharedprefInstance().getLoginData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyFullPageBinding.inflate(inflater, container, false)


        return binding.root
    }

}