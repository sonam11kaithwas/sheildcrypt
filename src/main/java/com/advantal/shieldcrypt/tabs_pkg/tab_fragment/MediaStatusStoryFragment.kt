package com.advantal.shieldcrypt.tabs_pkg.tab_fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentMediaStatusStoryBinding
import com.advantal.shieldcrypt.databinding.FragmentStatusBinding


class MediaStatusStoryFragment : Fragment() {

    lateinit var binding: FragmentMediaStatusStoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediaStatusStoryBinding.inflate(inflater, container, false)
        return binding.root
    }


}