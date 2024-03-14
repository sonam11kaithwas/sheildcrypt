package com.advantal.shieldcrypt.qr_code_pkg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.advantal.shieldcrypt.R



/**
 * A simple [Fragment] subclass.
 * Use the [MyQrCodeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyQrCodeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_qr_code, container, false)
    }

}