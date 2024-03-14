package com.advantal.shieldcrypt.auth_pkg.fragment_pkg

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.advantal.shieldcrypt.R
import com.advantal.shieldcrypt.databinding.FragmentPrivacyPolicyHelpBinding
import com.advantal.shieldcrypt.utils_pkg.MyApp


class PrivacyPolicyHelpFragment : Fragment(), View.OnClickListener {

    lateinit var binding: FragmentPrivacyPolicyHelpBinding
    private var isLoaded: Boolean = false
    private var doubleBackToExitPressedOnce = false
    private var webURL =
        //"http://znzmask.com:8080/MaskingAdmin/payment/paypal.jsp"
        "https://dagger.dev/hilt/gradle-setup" // Change it with your URL


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    /*clean Current FRAGMENT*/
                    findNavController(requireView()).navigateUp()
//                    requireActivity().finish()
//                    updateUI()
                }
            })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrivacyPolicyHelpBinding.inflate(inflater, container, false)
        loadMyWebView()
        return binding.root
    }

    private fun loadMyWebView() {
        binding.webPdf.settings.javaScriptEnabled = true
        binding.webPdf.settings.loadWithOverviewMode = true
        binding.webPdf.settings.useWideViewPort = true

        binding.webPdf.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                binding.progressBar.visibility = View.INVISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {

                binding.progressBar.visibility = View.VISIBLE

                super.onPageFinished(view, url)
            }

        }


        binding.webPdf.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                binding.progressBar.progress = newProgress
                if (newProgress == 100) {
                    binding.progressBar.visibility = View.GONE

                }
            }
        }

        binding.webPdf.loadUrl(webURL)


        clickListners()
    }

    private fun clickListners() {
        binding.toolbarBar.icBackarrow.setOnClickListener(this)
        binding.toolbarBar.toolbar.text = "Privacy & Policy"
    }

    fun updateUI() {
        if (binding.webPdf.canGoBack()) {
            binding.webPdf.goBack()
        } else {
            showToastToExit()
        }
    }


    private fun showToastToExit() {
        when {
            doubleBackToExitPressedOnce -> {
                requireActivity().finish()
            }
            else -> {
                doubleBackToExitPressedOnce = true
                MyApp.getAppInstance().showToastMsg("Exit")
                Handler(Looper.myLooper()!!).postDelayed(
                    { doubleBackToExitPressedOnce = false }, 2000
                )
            }
        }
//        requireActivity().finish()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ic_backarrow -> {
                /*clean Current FRAGMENT*/
                findNavController(requireView()).navigateUp()
            }
        }
    }


}